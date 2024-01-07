package com.kmarket.kakaopay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 카카오페이 서버에 전달할 상품 주문 정보
 */
@Getter
@Setter
@ToString
public class ProductOrderDTO {
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

    // 리스트로?
    @NotNull
    private Long productId;
    @NotNull
    private Integer quantity;

    private Integer savePoint;
    @NotNull
    private Integer usedPoint;
    private Integer totalAmount;
    private Long orderNumber;
}
