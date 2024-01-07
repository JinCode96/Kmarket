package com.kmarket.kakaopay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 카카오페이 서버에 전달할 상품 주문 정보 (장바구니)
 */
@Getter
@Setter
@ToString
public class ProductOrderCartDTO {
    @NotBlank
    private String receiptName;
    @NotBlank
    private String receiptHp;
    @NotBlank
    private String receiptZip;
    @NotBlank
    private String receiptAddress;
    @NotBlank
    private String receiptDetailAddress;
    private String productName;
    private List<IdAndQuantity> idAndQuantities; // 상품번호와 수량
    private Integer savePoint;
    @NotNull
    private Integer usedPoint;
    private Integer totalAmount; // 총 결제금액
    private Long orderNumber;
    private Integer totalDeliveryCost; // 모든 상품 배송비
    private Integer totalDiscountedPrice; // 모든 상품 가격
    
}
