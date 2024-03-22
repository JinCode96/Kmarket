package com.kmarket.controller;

import com.kmarket.exception.OrderProcessingException;
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
        log.info("카카오 페이 직접 결제...");

        try {
            // 1. 포인트 유효성 검사
            if (productOrderDTO.getUsedPoint() != 0) {
                validatePoints(productOrderDTO, principalDetails);
            }

            // 2. 상품정보 DB에서 가져오기
            Products products = productService.findById(productOrderDTO.getProductId()).orElse(null);

            // 3. 상품 정보 세팅
            productOrderDTO.setSavePoint(products.getPoint());
            productOrderDTO.setProductName(products.getProductName());
            productOrderDTO.setTotalAmount(getTotalAmount(productOrderDTO, products));

            // 4. 카카오페이 준비
            KakaoReadyResponse kakaoReadyResponse = kakaoPayService.kakaoPayReady(productOrderDTO);

            // 5. 세션에 주문 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("productOrderDTO", productOrderDTO);

            return kakaoReadyResponse;
        } catch (Exception e) {
            // 롤백 및 예외 처리
            log.error("카카오페이 주문 처리 중 오류가 발생했습니다.", e);
            throw new OrderProcessingException("카카오페이 주문 처리 중 오류가 발생했습니다.", e);
        }
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
        log.info("직접결제 완료 후 호출...");

        try {
            KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken); // 카카오페이 요청 승인
            ProductOrderDTO productOrderDTO = (ProductOrderDTO) session.getAttribute("productOrderDTO"); // 세션 주문 정보 가져오기
            session.removeAttribute("productOrderDTO"); // 세션 닫아주기

            if (productOrderDTO == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "세션에 저장된 주문정보 없음");
            }

            // 필요한 객체 생성
            OrderDTO orderDTO = createOrderDTO(principalDetails.getUsername(), kakaoApprove, productOrderDTO);
            OrderItemDTO orderItemDTO = createOrderItemDTO(productOrderDTO);
            MemberPointDTO memberPointDTO = createMemberPointDTO(principalDetails.getUsername(), kakaoApprove, productOrderDTO);

            productService.orderProcess(orderDTO, orderItemDTO, memberPointDTO, principalDetails.getMembers().getType()); // 주문 프로세스

            productOrderDTO.setOrderNumber(orderDTO.getOrderNumber()); // 주문번호 set
            redirectAttributes.addFlashAttribute("productOrderDTO", productOrderDTO);

            return "redirect:/product/complete";
        } catch (Exception e) {
            log.error("직접주문 처리 중 오류가 발생했습니다.", e);
            throw new OrderProcessingException("직접주문 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 직접 결제 주문 완료 화면
     */
    @GetMapping("/complete")
    public String complete(ProductOrderDTO productOrderDTO, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        log.info("직접 결제 완료...");
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
        CartSummary cartSummary = calculateCartSummary(findCarts);
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

        try {
            // 상품 정보 세팅
            List<IdAndQuantity> orderInfos = getProductOrderInfo(productOrderCartDTO.getIdAndQuantities());

            // 총 결제 금액 계산
            int totalSavePoint = calculateTotalSavePoint(orderInfos);
            int totalDiscountedPrice = calculateTotalDiscountedPrice(orderInfos);
            int totalDeliveryCost = calculateTotalDeliveryCost(orderInfos);
            int totalAmount = calculateTotalAmount(totalDiscountedPrice, totalDeliveryCost, productOrderCartDTO.getUsedPoint());

            // 상품명 설정
            String productName = generateProductName(orderInfos);

            // 주문 정보 설정
            productOrderCartDTO.setSavePoint(totalSavePoint);
            productOrderCartDTO.setProductName(productName);
            productOrderCartDTO.setTotalDiscountedPrice(totalDiscountedPrice);
            productOrderCartDTO.setTotalDeliveryCost(totalDeliveryCost);
            productOrderCartDTO.setTotalAmount(totalAmount);

            // 카카오페이 준비
            KakaoReadyResponse kakaoReadyResponse = kakaoPayService.kakaoPayReady(productOrderCartDTO);

            // 세션에 주문 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("productOrderCartDTO", productOrderCartDTO);

            return kakaoReadyResponse;
        } catch (Exception e) {
            log.error("장바구니 주문 처리 중 오류가 발생했습니다.", e);
            throw new OrderProcessingException("장바구니 주문 처리 중 오류가 발생했습니다.", e);
        }
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

        try {
            // 카카오페이 요청 승인
            KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(pgToken);

            // 세션 주문 정보 가져오기
            ProductOrderCartDTO productOrderCartDTO = (ProductOrderCartDTO) session.getAttribute("productOrderCartDTO");
            session.removeAttribute("productOrderCartDTO");

            // 주문정보 없을 때 처리
            if (productOrderCartDTO == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "세션에 주문정보가 없음");
            }

            // 필요한 객체 생성
            OrderDTO orderDTO = createOrderDTO(principalDetails.getUsername(), kakaoApprove, productOrderCartDTO);
            List<OrderItemDTO> orderItemDTOs = createOrderItemDTO(productOrderCartDTO);
            MemberPointDTO memberPointDTO = createMemberPointDTO(principalDetails.getUsername(), kakaoApprove, productOrderCartDTO);

            // 주문 프로세스 실행
            productService.orderProcessCart(orderDTO, orderItemDTOs, memberPointDTO, principalDetails.getMembers().getType());

            // 주문번호 설정 후 리다이렉트
            productOrderCartDTO.setOrderNumber(orderDTO.getOrderNumber());
            redirectAttributes.addFlashAttribute("productOrderCartDTO", productOrderCartDTO);
            return "redirect:/product/completeCart";
        } catch (Exception e) {
            log.error("장바구니 주문 처리 중 오류가 발생했습니다.", e);
            throw new OrderProcessingException("장바구니 주문 처리 중 오류가 발생했습니다.", e);
        }
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
        CartSummary cartSummary = calculateCartSummary(findCarts);
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

    // 상품 정보 세팅 메서드
    private List<IdAndQuantity> getProductOrderInfo(List<IdAndQuantity> idAndQuantities) {
        List<IdAndQuantity> orderInfos = new ArrayList<>();
        for (IdAndQuantity idAndQuantity : idAndQuantities) {
            Products product = productService.findById(idAndQuantity.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, PRODUCT_NOT_FOUND));
            setProductOrderInfo(idAndQuantity, product);
            orderInfos.add(idAndQuantity);
        }
        return orderInfos;
    }

    // 주문 정보 설정 메서드
    private void setProductOrderInfo(IdAndQuantity orderInfo, Products product) {
        orderInfo.setProductName(product.getProductName());
        orderInfo.setPoint(product.getPoint());
        orderInfo.setDeliveryCost(product.getDeliveryCost());
        orderInfo.setDiscountedPrice(product.getDiscountedPrice());
        orderInfo.setDiscountRate(product.getDiscountRate());
        orderInfo.setDescription(product.getDescription());
        orderInfo.setPrice(product.getPrice());
        orderInfo.setCategory1Code(product.getCategory1Code());
        orderInfo.setCategory2Code(product.getCategory2Code());
        orderInfo.setThumbnailList(product.getThumbnailList());
    }

    // 총 적립 포인트 계산 메서드
    private int calculateTotalSavePoint(List<IdAndQuantity> orderInfos) {
        return orderInfos.stream()
                .mapToInt(info -> info.getPoint() * info.getQuantity())
                .sum();
    }

    // 총 할인 가격 계산 메서드
    private int calculateTotalDiscountedPrice(List<IdAndQuantity> orderInfos) {
        return orderInfos.stream()
                .mapToInt(info -> info.getDiscountedPrice() * info.getQuantity())
                .sum();
    }

    // 총 배송비 계산 메서드
    private int calculateTotalDeliveryCost(List<IdAndQuantity> orderInfos) {
        return orderInfos.stream()
                .mapToInt(IdAndQuantity::getDeliveryCost)
                .sum();
    }

    // 총 결제 금액 계산 메서드
    private int calculateTotalAmount(int totalDiscountedPrice, int totalDeliveryCost, int usedPoint) {
        return totalDiscountedPrice + totalDeliveryCost - usedPoint;
    }

    // 상품명 생성 메서드
    private String generateProductName(List<IdAndQuantity> orderInfos) {
        if (orderInfos.size() > 1) {
            return orderInfos.get(0).getProductName() + "외 " + (orderInfos.size() - 1) + "개";
        } else {
            return orderInfos.get(0).getProductName();
        }
    }

    // 장바구니 요약 정보 계산 메서드
    private CartSummary calculateCartSummary(List<Cart> carts) {
        CartSummary cartSummary = new CartSummary();
        for (Cart cart : carts) {
            cartSummary.addProduct(cart);
        }
        // 배송비를 상품 총 가격에 더함
        cartSummary.setTotalAmount(cartSummary.getTotalAmount() + cartSummary.getTotalDeliveryCost());
        return cartSummary;
    }
}
