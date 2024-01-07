package com.kmarket.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MemberPointDTO {
    private Long id;
    private String uid; // 아이디
    private Long orderNumber; // 상품 주문 번호
    private Integer earnedPoint; // 적립되는 포인트
    private LocalDateTime earnedPointDate; // 포인트 적립일
}
