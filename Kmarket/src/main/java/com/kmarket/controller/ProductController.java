package com.kmarket.controller;

import com.kmarket.kakaopay.KakaoPayService;
import com.kmarket.api.ApiResponse;
import com.kmarket.domain.*;
import com.kmarket.dto.product.*;
import com.kmarket.kakaopay.*;
import com.kmarket.security.PrincipalDetails;
import com.kmarket.service.ProductService;
import com.kmarket.util.FileStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.*;

/**
 * 상품 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStore fileStore;
    private final KakaoPayService kakaoPayService;

    /**
     * 상품 목록 화면
     * 카테고리에 맞는 상품 불러오기, 페이징 처리
     */
    @GetMapping("/list")
    public String list(Pageable pageable,
                       Integer cate1, Integer cate2,
                       @RequestParam(defaultValue = "popular") String sort,
                       Model model) {
        CategoryDTO category = productService.getCategory(cate1, cate2);
        Page<Products> findProducts = productService.findByCategory1CodeAndCategory2Code(cate1, cate2, pageable, sort); // 카테고리별 조회

        model.addAttribute("category", category);
        model.addAttribute("productsPage", findProducts);
        model.addAttribute("currentSort", sort);
        return "product/list";
    }

    /**
     * 상품 상세 화면
     * 리뷰 페이징 처리
     */
    @GetMapping("/view")
    public String view(Long id, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Products findProduct = productService.findById(id).orElse(null);
        if (findProduct == null) {
            model.addAttribute("errorMessage", PRODUCT_NOT_FOUND); // 에러페이지
            return "error/errorPage";
        }

        productService.increaseHit(id); // 상품 조회수 +1
        Page<Review> reviewPage = productService.findByProductId(id, pageable); // 상품 리뷰 페이징 처리

        model.addAttribute("category", productService.getCategory(findProduct.getCategory1Code(), findProduct.getCategory2Code()));
        model.addAttribute("product", findProduct);
        model.addAttribute("reviewPage", reviewPage);
        return "product/view";
    }

    /**
     * 장바구니에 추가 POST
     */
    @ResponseBody
    @PostMapping("/addCarts")
    public ApiResponse directInsertCart(@RequestBody ProductIdAndQuantity productIdAndQuantity, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("상품 장바구니에 추가...");

        if (principalDetails == null) {
            return new ApiResponse( 600); // 로그인 되어있지 않음
        }
        if (productIdAndQuantity.getQuantity() > 100) {
            return new ApiResponse(MAX_PURCHASE_PER_PERSON, FAIL);
        }

        int result = productService.insertCart(productIdAndQuantity, principalDetails.getUsername());
        if (result == SUCCESS) {
            return new ApiResponse(ADD_TO_CART_SUCCESS, result);
        } else {
            return new ApiResponse(ADD_TO_CART_FAILED, result);
        }
    }

    /**
     * 직접 주문 화면
     */
    @GetMapping("/directOrder")
    public String directOrder(Long productId, Integer quantity, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        if (!(1 <= quantity && quantity <= 100)) {
            model.addAttribute("errorMessage", MAX_PURCHASE_PER_PERSON);
            return "error/errorPage";
        }
        Products findProduct = productService.findById(productId).orElse(null); // 해당 상품 가져오기, 장바구니 추가
        if (findProduct == null) { // 상품 null 체크
            model.addAttribute("errorMessage", PRODUCT_NOT_FOUND);
            return "error/errorPage";
        }

        Members member = principalDetails.getMembers();

        model.addAttribute("quantity", quantity);
        model.addAttribute("product", findProduct);
        model.addAttribute("member", member);
        return "product/directOrder";
    }

    /**
     * 카카오 페이 직접 결제
     * 포인트 유효성 검사
     */
    @ResponseBody
    @PostMapping("/orderPay")
    public KakaoReadyResponse readyToKakaoPay(@RequestBody @Validated ProductOrderDTO productOrderDTO,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails,
                                              HttpServletRequest request) {
        log.info("카카오 페이 단건 결제...");

        if (productOrderDTO.getUsedPoint() != 0) {
            validatePoints(productOrderDTO, principalDetails); // 포인트 유효성 검사
        }

        Products products = productService.findById(productOrderDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, PRODUCT_NOT_FOUND)); // 실제 상품 가져오기

        // 실제 상품 정보 세팅
        productOrderDTO.setSavePoint(products.getPoint());
        productOrderDTO.setProductName(products.getProductName());
        productOrderDTO.setTotalAmount(getTotalAmount(productOrderDTO, products)); 

        KakaoReadyResponse kakaoReadyResponse = kakaoPayService.kakaoPayReady(productOrderDTO); // 카카오페이 준비

        // 세션에 주문 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("productOrderDTO", productOrderDTO); 

        return kakaoReadyResponse; // 카카오페이 준비
    }

    /**
     * 직접 결제 완료 후 호출
     * 1. order 테이블에 주문 내용 1개 추가
     * 2. order_item 테이블에 주문한 상품 추가. (단일 상품일 시 1개 추가)
     * 3. member_point 테이블 포인트 적립 추가
     * 4. 상품 주문 횟수 +1
     * 5. member_general 또는 member_seller 테이블 포인트 업데이트
     * 모두 트랜잭션으로 한번에 처리
     * 결제 완료 창으로 리다이렉트
     */
    @GetMapping("/orderApproval")
    public String orderApproval(@RequestParam("pg_token") String pgToken, @AuthenticationPrincipal PrincipalDetails principalDetails, HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("orderApproval...");

        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken); // 카카오페이 요청 승인
        ProductOrderDTO productOrderDTO = (ProductOrderDTO) session.getAttribute("productOrderDTO"); // 세션 주문 정보 가져오기
        session.removeAttribute("productOrderDTO"); // 세션 닫아주기

        if (productOrderDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session attribute 'productOrderDTO' not found or expired");
        }

        // 필요한 객체 생성
        OrderDTO orderDTO = createOrderDTO(principalDetails.getUsername(), kakaoApprove, productOrderDTO);
        OrderItemDTO orderItemDTO = createOrderItemDTO(productOrderDTO);
        MemberPointDTO memberPointDTO = createMemberPointDTO(principalDetails.getUsername(), kakaoApprove, productOrderDTO);

        productService.orderProcess(orderDTO, orderItemDTO, memberPointDTO, principalDetails.getMembers().getType()); // 주문 프로세스

        productOrderDTO.setOrderNumber(orderDTO.getOrderNumber()); // 주문번호 set
        redirectAttributes.addFlashAttribute("productOrderDTO", productOrderDTO);

        return "redirect:/product/complete";
    }

    /**
     * 직접 결제 주문 완료 화면
     */
    @GetMapping("/complete")
    public String complete(ProductOrderDTO productOrderDTO, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        log.info("단건 결제 완료...");
        if (productOrderDTO.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "didn't order the product");
        }
        Products product = productService.findById(productOrderDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "product not found")); // 실제 상품 가져오기

        model.addAttribute("member", principalDetails.getMembers());
        model.addAttribute("product", product);
        model.addAttribute("productOrder", productOrderDTO);
        return "product/complete";
    }

    /**
     * 상품 장바구니 주문 화면
     */
    @GetMapping("/cartOrder")
    public String cartOrder(@RequestParam List<Long> productIds, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<Cart> findCarts = productService.findById(productIds, principalDetails.getUsername());

        CartSummary cartSummary = new CartSummary();

        for (Cart cart : findCarts) {
            cartSummary.addProduct(cart);
        }
        cartSummary.setTotalAmount(cartSummary.getTotalAmount() + cartSummary.getTotalDeliveryCost()); // 배송비를 상품 총 가격에 더함

        model.addAttribute("cartSummary", cartSummary);
        model.addAttribute("member", principalDetails.getMembers());
        model.addAttribute("carts", findCarts);
        return "product/cartOrder";
    }

    /**
     * 카카오페이 장바구니 결제
     */
    @ResponseBody
    @PostMapping("/cartOrderPay")
    public KakaoReadyResponse readyToKakaoPayCart(@RequestBody @Validated ProductOrderCartDTO productOrderCartDTO, HttpServletRequest request) {
        log.info("카카오 페이 장바구니 결제...");

        List<IdAndQuantity> orderInfos = new ArrayList<>();

        // 실제 product 가져와서 orderInfo 채우기
        for (IdAndQuantity idAndQuantity : productOrderCartDTO.getIdAndQuantities()) {
            Products findProducts = productService.findById(idAndQuantity.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, PRODUCT_NOT_FOUND));
            idAndQuantity.setProductName(findProducts.getProductName());
            idAndQuantity.setPoint(findProducts.getPoint());
            idAndQuantity.setDeliveryCost(findProducts.getDeliveryCost());
            idAndQuantity.setDiscountedPrice(findProducts.getDiscountedPrice());
            idAndQuantity.setDiscountRate(findProducts.getDiscountRate());
            idAndQuantity.setDescription(findProducts.getDescription());
            idAndQuantity.setPrice(findProducts.getPrice());
            idAndQuantity.setCategory1Code(findProducts.getCategory1Code());
            idAndQuantity.setCategory2Code(findProducts.getCategory2Code());
            idAndQuantity.setThumbnailList(findProducts.getThumbnailList());
            orderInfos.add(idAndQuantity);
        }

        int totalSavePoint = 0;
        int totalDeliveryCost = 0;
        int totalDiscountedPrice = 0;
        String productName = null;
        int totalAmount = 0;

        for (IdAndQuantity orderInfo : orderInfos) {
            // 1. totalSavePoint 산출
            totalSavePoint += orderInfo.getPoint() * orderInfo.getQuantity();
            // 2. totalDiscountedPrice 산출
            totalDiscountedPrice += orderInfo.getDiscountedPrice() * orderInfo.getQuantity();
            // 3. totalDeliveryCost 산출
            totalDeliveryCost += orderInfo.getDeliveryCost();
        }
        // 4. 상품명 외 n개
        if (orderInfos.size() > 1) {
            productName = orderInfos.get(0).getProductName() + "외 " + orderInfos.size() + "개";
        } else {
            productName = orderInfos.get(0).getProductName();
        }

        // 5. 총 결제 금액 산출
        totalAmount = totalDiscountedPrice + totalDeliveryCost - productOrderCartDTO.getUsedPoint();

        // 실제 상품 정보 세팅
        productOrderCartDTO.setSavePoint(totalSavePoint);
        productOrderCartDTO.setProductName(productName);
        productOrderCartDTO.setTotalDiscountedPrice(totalDiscountedPrice);
        productOrderCartDTO.setTotalDeliveryCost(totalDeliveryCost);
        productOrderCartDTO.setTotalAmount(totalAmount); // 총 비용

        KakaoReadyResponse kakaoReadyResponse = kakaoPayService.kakaoPayReady(productOrderCartDTO); // 카카오페이 준비

        // 세션에 주문 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("productOrderCartDTO", productOrderCartDTO);

        return kakaoReadyResponse; // 카카오페이 준비
    }

    /**
     * 카카오페이 결제 완료 후 호출 (장바구니)
     * 1. order 테이블에 주문 내용 1개 추가
     * 2. order_item 테이블에 주문한 상품 추가. (단일 상품일 시 1개 추가)
     * 3. member_point 테이블 포인트 적립 추가
     * 4. 상품 주문 횟수 +1
     * 5. member_general 또는 member_seller 테이블 포인트 업데이트
     */
    @GetMapping("/orderApprovalCart")
    public String orderApprovalCart(@RequestParam("pg_token") String pgToken, @AuthenticationPrincipal PrincipalDetails principalDetails, HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("orderApprovalCart...");

        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken); // 카카오페이 요청 승인

        ProductOrderCartDTO productOrderCartDTO = (ProductOrderCartDTO) session.getAttribute("productOrderCartDTO"); // 세션 주문 정보 가져오기
        session.removeAttribute("productOrderCartDTO"); // 세션 닫아주기

        if (productOrderCartDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session attribute 'productOrderDTO' not found or expired");
        }

        // 필요한 객체 생성
        OrderDTO orderDTO = createOrderDTO(principalDetails.getUsername(), kakaoApprove, productOrderCartDTO);
        List<OrderItemDTO> orderItemDTOs = createOrderItemDTO(productOrderCartDTO); // 리스트
        MemberPointDTO memberPointDTO = createMemberPointDTO(principalDetails.getUsername(), kakaoApprove, productOrderCartDTO);

        productService.orderProcessCart(orderDTO, orderItemDTOs, memberPointDTO, principalDetails.getMembers().getType()); // 주문 프로세스

        productOrderCartDTO.setOrderNumber(orderDTO.getOrderNumber()); // 주문번호 set
        redirectAttributes.addFlashAttribute("productOrderCartDTO", productOrderCartDTO);

        return "redirect:/product/completeCart";
    }

    /**
     * 주문 완료 페이지 (장바구니)
     */
    @GetMapping("/completeCart")
    public String completeCart(ProductOrderCartDTO productOrderCartDTO, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        log.info("장바구니 결제 완료...");
        if (productOrderCartDTO.getIdAndQuantities() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "didn't order the product");
        }

        model.addAttribute("member", principalDetails.getMembers());
        model.addAttribute("productOrderCartDTO", productOrderCartDTO);
        model.addAttribute("idAndQuantities", productOrderCartDTO.getIdAndQuantities());
        return "product/completeCart";
    }

    /**
     * 장바구니
     * 해당 회원의 장바구니 정보 가져오기
     * 장바구니의 가격 정보 더해서 출력
     */
    @GetMapping("/cart")
    public String cart(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<Cart> findCarts = productService.findByUid(principalDetails.getUsername());

        CartSummary cartSummary = new CartSummary();

        for (Cart cart : findCarts) {
            cartSummary.addProduct(cart); // 연산
        }
        cartSummary.setTotalAmount(cartSummary.getTotalAmount() + cartSummary.getTotalDeliveryCost()); // 배송비를 상품 총 가격에 더함

        model.addAttribute("cartSummary", cartSummary);
        model.addAttribute("carts", findCarts);
        return "product/cart";
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @ResponseBody
    @PutMapping("/cart/quantity")
    public ApiResponse changeQuantity(@RequestBody Map<String, String> map, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("장바구니 수량 변경...");

        int result = productService.changeQuantity(map, principalDetails);
        if (result == SUCCESS) {
            return new ApiResponse(PRODUCT_CHANGE_OK, result);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fail to change quantity of cart");
    }

    /**
     * 장바구니 상품 삭제 DELETE
     */
    @ResponseBody
    @DeleteMapping("/cart")
    public ApiResponse deleteCart(@RequestBody Map<String, List<Long>> requestBody,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("장바구니 상품 삭제...");

        List<Long> productIds = requestBody.get("productIds");
        try {
            productService.deleteCart(productIds, principalDetails);
            return new ApiResponse(PRODUCT_DELETE_OK, SUCCESS);
        } catch (RuntimeException e) {
            log.error("장바구니 상품 삭제 시 에러 발생", e);
            return new ApiResponse(PRODUCT_DELETE_NOT_OK, FAIL);
        }
    }
    
    //////////////////////////////////////////////////////////////////////

    /**
     * 이미지 보여주기
     */
    @ResponseBody
    @GetMapping("/images/{filename}/{cate1}/{cate2}")
    public Resource downloadImage(@PathVariable String filename, @PathVariable Integer cate1, @PathVariable Integer cate2) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename, cate1, cate2));
    }

    /**
     * 총 결제 금액 산출
     * 할인된 가격 * 수량 + 배송비 - 사용 포인트
     */
    private static int getTotalAmount(ProductOrderDTO productOrderDTO, Products products) {
        return products.getDiscountedPrice() * productOrderDTO.getQuantity() + products.getDeliveryCost() - productOrderDTO.getUsedPoint();
    }

    /**
     * 포인트 유효성 검사
     */
    private void validatePoints(ProductOrderDTO productOrderDTO, PrincipalDetails principalDetails) {
        log.info("포인트 유효성 검사...");

        String type = principalDetails.getMembers().getType();
        Integer availablePoint = null;

        if (type.equals(GENERAL_UPPER)) {
            availablePoint = productService.findGeneralPoint(principalDetails.getUsername());
        } else if (type.equals(SELLER_UPPER)) {
            availablePoint = productService.findSellerPoint(principalDetails.getUsername());
        }

        // 소지한 포인트보다 입력한 포인트가 더 클 때, 소지한 포인트가 5000 미만일 때
        if (productOrderDTO.getUsedPoint() > availablePoint || availablePoint < 5000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exceeded available points.");
        }
    }

    // OrderDTO 생성 메서드
    private OrderDTO createOrderDTO(String uid, KakaoApproveResponse kakaoApprove, ProductOrderDTO productOrderDTO) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUid(uid);
        orderDTO.setOrderQuantity(kakaoApprove.getQuantity());
        orderDTO.setSavePoint(productOrderDTO.getSavePoint());
        orderDTO.setUsedPoint(productOrderDTO.getUsedPoint());
        orderDTO.setOrderTotalPrice(productOrderDTO.getTotalAmount());
        orderDTO.setReceiptName(productOrderDTO.getReceiptName());
        orderDTO.setReceiptHp(productOrderDTO.getReceiptHp());
        orderDTO.setReceiptZip(productOrderDTO.getReceiptZip());
        orderDTO.setReceiptAddress(productOrderDTO.getReceiptAddress());
        orderDTO.setReceiptDetailAddress(productOrderDTO.getReceiptDetailAddress());
        orderDTO.setOrderDate(LocalDateTime.parse(kakaoApprove.getApproved_at()));
        return orderDTO;
    }

    // OrderItemDTO 생성 메서드
    private OrderItemDTO createOrderItemDTO(ProductOrderDTO productOrderDTO) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setProductId(productOrderDTO.getProductId());
        orderItemDTO.setQuantity(productOrderDTO.getQuantity());
        return orderItemDTO;
    }

    // MemberPointDTO 생성 메서드
    private MemberPointDTO createMemberPointDTO(String uid, KakaoApproveResponse kakaoApprove, ProductOrderDTO productOrderDTO) {
        MemberPointDTO memberPointDTO = new MemberPointDTO();
        memberPointDTO.setUid(uid);
        memberPointDTO.setEarnedPoint(productOrderDTO.getSavePoint());
        memberPointDTO.setEarnedPointDate(LocalDateTime.parse(kakaoApprove.getApproved_at()));
        return memberPointDTO;
    }

    /////////////////////// 장바구니 ///////////////////////

    // OrderDTO 생성 메서드
    private OrderDTO createOrderDTO(String uid, KakaoApproveResponse kakaoApprove, ProductOrderCartDTO productOrderCartDTO) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUid(uid);
        orderDTO.setOrderQuantity(productOrderCartDTO.getIdAndQuantities().size());
        orderDTO.setSavePoint(productOrderCartDTO.getSavePoint());
        orderDTO.setUsedPoint(productOrderCartDTO.getUsedPoint());
        orderDTO.setOrderTotalPrice(productOrderCartDTO.getTotalAmount());
        orderDTO.setReceiptName(productOrderCartDTO.getReceiptName());
        orderDTO.setReceiptHp(productOrderCartDTO.getReceiptHp());
        orderDTO.setReceiptZip(productOrderCartDTO.getReceiptZip());
        orderDTO.setReceiptAddress(productOrderCartDTO.getReceiptAddress());
        orderDTO.setReceiptDetailAddress(productOrderCartDTO.getReceiptDetailAddress());
        orderDTO.setOrderDate(LocalDateTime.parse(kakaoApprove.getApproved_at()));
        return orderDTO;
    }

    // OrderItemDTO 생성 메서드 (리스트)
    private List<OrderItemDTO> createOrderItemDTO(ProductOrderCartDTO productOrderCartDTO) {
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();

        for (IdAndQuantity idAndQuantity : productOrderCartDTO.getIdAndQuantities()) {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setProductId(idAndQuantity.getProductId());
            orderItemDTO.setQuantity(idAndQuantity.getQuantity());
            orderItemDTOs.add(orderItemDTO);
        }

        return orderItemDTOs;
    }

    // MemberPointDTO 생성 메서드
    private MemberPointDTO createMemberPointDTO(String uid, KakaoApproveResponse kakaoApprove, ProductOrderCartDTO productOrderCartDTO) {
        MemberPointDTO memberPointDTO = new MemberPointDTO();
        memberPointDTO.setUid(uid);
        memberPointDTO.setEarnedPoint(productOrderCartDTO.getSavePoint());
        memberPointDTO.setEarnedPointDate(LocalDateTime.parse(kakaoApprove.getApproved_at()));
        return memberPointDTO;
    }
}
