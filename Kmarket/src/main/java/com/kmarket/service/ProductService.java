package com.kmarket.service;

import com.kmarket.api.ApiResponse;
import com.kmarket.constant.ApiResponseConst;
import com.kmarket.constant.MemberConst;
import com.kmarket.constant.ProductConst;
import com.kmarket.domain.Cart;
import com.kmarket.domain.Products;
import com.kmarket.domain.Review;
import com.kmarket.dto.product.*;
import com.kmarket.repository.product.JpaCartRepository;
import com.kmarket.repository.product.JpaProductRepository;
import com.kmarket.repository.product.JpaReviewRepository;
import com.kmarket.repository.product.ProductRepository;
import com.kmarket.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.GENERAL_UPPER;
import static com.kmarket.constant.MemberConst.SELLER_UPPER;
import static com.kmarket.constant.ProductConst.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaReviewRepository jpaReviewRepository;
    private final JpaCartRepository jpaCartRepository;

    /**
     * 카테고리 조회
     */
    public CategoryDTO getCategory(Integer cate1, Integer cate2) {
        return productRepository.getCategory(cate1, cate2);
    }

    /**
     * 조회수 증가
     */
    public void increaseHit(Long productId){
        productRepository.increaseHit(productId);
    }

    /**
     * 상품 카테고리 코드로 조회
     */
    public Page<Products> findByCategory1CodeAndCategory2Code(Integer cate1, Integer cate2, Pageable pageable, String sort) {
        switch (sort) {
            case  SOLD -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(SOLD_NUMBER).descending());
            }
            case LOW_PRICE -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(PRICE).ascending());
            }
            case HIGH_PRICE -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(PRICE).descending());
            }
            case HIGH_RATING -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(SCORE).descending());
            }
            case MANY_REVIEW -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(REVIEW).descending());
            }
            case RECENT -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(REGISTRATION_DATE).descending());
            }
            default -> {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(ID).descending());
            }
        }
        return jpaProductRepository.findByCategory1CodeAndCategory2Code(cate1, cate2, pageable);
    }

    /**
     * 상품 단건 조회
     */
    public Optional<Products> findById(Long id) {
        return jpaProductRepository.findById(id);
    }

    /**
     * 상품 다중 조회
     */
    public List<Cart> findById(List<Long> productIds, String uid) {
        List<Cart> products = new ArrayList<>();
        for (Long productId : productIds) {
            products.add(jpaCartRepository.findByProductIdAndUid(productId, uid).orElse(null));
        }
        return products;
    }

    /**
     * 장바구니 uid 로 조회
     */
    public List<Cart> findByUid(String uid) {
        return jpaCartRepository.findByUidOrderByRegistrationDateDesc(uid);
    }

    /**
     * 장바구니 상품 저장
     */
    public int insertCart(ProductIdAndQuantity idAndQuantity, String username) {
        // 해당 상품 정보 가져오기
        Products findProduct = jpaProductRepository.findById(idAndQuantity.getProductId()).orElse(null);
        // 상품 없으면 에러
        if (findProduct == null) {
            return FAIL;
        }
        // 해당 회원의 장바구니에서 해당 상품 정보 가져오기
        Cart findCartProduct = jpaCartRepository.findByProductIdAndUid(idAndQuantity.getProductId(), username).orElse(null);
        // 장바구니에 해당 상품 있는지 체크
        if (findCartProduct == null) {
            // 해당 상품이 장바구니에 없다면 새로 추가해서 넣기
            Cart cart = new Cart().builder()
                    .productId(idAndQuantity.getProductId())
                    .uid(username)
                    .productName(findProduct.getProductName())
                    .quantity(idAndQuantity.getQuantity())
                    .price(findProduct.getPrice())
                    .discountRate(findProduct.getDiscountRate())
                    .point(findProduct.getPoint())
                    .deliveryCost(findProduct.getDeliveryCost())
                    .discountedPrice(findProduct.getDiscountedPrice())
                    .thumbnailList(findProduct.getThumbnailList())
                    .category1Code(findProduct.getCategory1Code())
                    .category2Code(findProduct.getCategory2Code())
                    .build();
            jpaCartRepository.save(cart);
        } else {
            // 해당 상품이 이미 장바구니에 있다면 기존 quantity 에 더하기 (사용자가 같고 상품이 이미 있다면)
            findCartProduct.setQuantity(findCartProduct.getQuantity() + idAndQuantity.getQuantity()); // 수량 추가
        }
        return SUCCESS;
    }

    /**
     * productId 로 리뷰 조회
     */
    public Page<Review> findByProductId(Long productId, Pageable pageable) {
        return jpaReviewRepository.findByProductId(productId, pageable);
    }

    /**
     * 일반회원 포인트 조회
     */
    public Integer findGeneralPoint(String loginId) {
        return productRepository.findGeneralPoint(loginId);
    }

    /**
     * 판매자 회원 포인트 조회
     */
    public Integer findSellerPoint(String loginId) {
        return productRepository.findSellerPoint(loginId);
    }

    /**
     * 단일 상품 결제 후 테이블에 저장할 것들
     * 트랜잭션
     */
    public void orderProcess(OrderDTO orderDTO, OrderItemDTO orderItemDTO, MemberPointDTO memberPointDTO, String memberType) {

        // 1. 주문 정보 테이블 삽입
        productRepository.saveOrder(orderDTO);

        // 2. 주문 상품 테이블 삽입
        orderItemDTO.setOrderNumber(orderDTO.getOrderNumber());
        productRepository.saveOrderItem(orderItemDTO);

        // 3. 멤버 포인트 테이블 삽입
        memberPointDTO.setOrderNumber(orderDTO.getOrderNumber());
        productRepository.saveMemberPoint(memberPointDTO);

        // 4. 상품 주문 횟수 추가
        productRepository.increaseSoldNumber(orderItemDTO.getProductId());

        // 5. 멤버 테이블 포인트 업데이트
        if (GENERAL_UPPER.equals(memberType)) {
            productRepository.updateGeneralMemberPoint(orderDTO.getSavePoint(), orderDTO.getUsedPoint(), orderDTO.getUid());
        } else if (SELLER_UPPER.equals(memberType)) {
            productRepository.updateSellerMemberPoint(orderDTO.getSavePoint(), orderDTO.getUsedPoint(), orderDTO.getUid());
        }
    }

    /**
     * 장바구니 결제 후 테이블에 저장할 것들
     * 트랜잭션
     */
    public void orderProcessCart(OrderDTO orderDTO, List<OrderItemDTO> orderItemDTOs, MemberPointDTO memberPointDTO, String memberType) {

        // 1. 주문 정보 테이블 삽입
        productRepository.saveOrder(orderDTO);

        // 2. 주문 상품 테이블 삽입
        for (OrderItemDTO orderItemDTO : orderItemDTOs) {
            orderItemDTO.setOrderNumber(orderDTO.getOrderNumber());
            productRepository.saveOrderItem(orderItemDTO);
        }

        // 3. 멤버 포인트 테이블 삽입
        memberPointDTO.setOrderNumber(orderDTO.getOrderNumber());
        productRepository.saveMemberPoint(memberPointDTO);

        // 4. 상품 주문 횟수 추가
        for (OrderItemDTO orderItemDTO : orderItemDTOs) {
            productRepository.increaseSoldNumber(orderItemDTO.getProductId());
        }

        // 5. 멤버 테이블 포인트 업데이트
        if (GENERAL_UPPER.equals(memberType)) {
            productRepository.updateGeneralMemberPoint(orderDTO.getSavePoint(), orderDTO.getUsedPoint(), orderDTO.getUid());
        } else if (SELLER_UPPER.equals(memberType)) {
            productRepository.updateSellerMemberPoint(orderDTO.getSavePoint(), orderDTO.getUsedPoint(), orderDTO.getUid());
        }
    }

    /**
     * 장바구니 상품 수량 변경
     */
    public int changeQuantity(Map<String, String> map, PrincipalDetails principalDetails) {
        Long productId = Long.valueOf(map.get("productId"));
        int quantity = Integer.parseInt(map.get("quantity"));
        String uid = principalDetails.getUsername();
        
        Cart findCart = jpaCartRepository.findByProductIdAndUid(productId, uid).orElse(null);
        
        if (map.get("action").equals("decrease")) {
            if (findCart != null && quantity > 1) {
                findCart.setQuantity(findCart.getQuantity() - 1); // 수량 감소
                return SUCCESS;
            }
        } else if (map.get("action").equals("increase")) {
            if (findCart != null && quantity < 100) {
                findCart.setQuantity(findCart.getQuantity() + 1); // 수량 증가
                return SUCCESS;
            }
        } else if (map.get("action").equals("input")) {
            if (findCart != null && quantity >= 1 && quantity <= 100) {
                findCart.setQuantity(Integer.valueOf(map.get("quantity"))); // 수량 변경
                return SUCCESS;
            }
        }
        return FAIL;
    }

    /**
     * 장바구니 상품 삭제
     */
    public void deleteCart(List<Long> productIds, PrincipalDetails principalDetails) {
        for (Long productId : productIds) {
            jpaCartRepository.deleteByProductIdAndUid(productId, principalDetails.getUsername());
        }
    }

    // 히트상품
    public List<Products> findTop8ByOrderByHitDesc() {
        return jpaProductRepository.findTop8ByOrderByHitDesc();
    }
    // 추천상품
    public List<Products> findRandomProducts() {
        return jpaProductRepository.findRandomProducts();
    }
    // 최신상품
    public List<Products> findTop8ByOrderByRegistrationDateDesc() {
        return jpaProductRepository.findTop8ByOrderByRegistrationDateDesc();
    }
    // 할인상품
    public List<Products> findTop8ByOrderByDiscountRateDesc() {
        return jpaProductRepository.findTop8ByOrderByDiscountRateDesc();
    }
    // 베스트 상품
    public List<Products> findTop5ByOrderBySoldNumberDesc() {
        return jpaProductRepository.findTop5ByOrderBySoldNumberDesc();
    }
}
