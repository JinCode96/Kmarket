package com.kmarket.dto.product;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrderDTO {
    private Long orderNumber; // 주문 번호
    private String uid; // 아이디
    private Integer orderQuantity; // 주문 수량
    private Integer savePoint; // 적립되는 포인트
    private Integer usedPoint; // 사용한 포인트
    private Integer orderTotalPrice; // 총 결제 비용
    private String receiptName;
    private String receiptHp;
    private String receiptZip;
    private String receiptAddress;
    private String receiptDetailAddress;
    private Integer orderPayment;
    private Integer orderComplete;
    private String orderState;
    private LocalDateTime orderDate;
}
