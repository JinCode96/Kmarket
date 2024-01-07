package com.kmarket.kakaopay;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 결제 금액 정보
 */
@Getter
@Setter
@ToString
public class Amount {
    private Integer total; // 총 결제 금액
    private Integer tax_free; // 비과세 금액
    private Integer tax; // 부가세 금액
    private Integer point; // 사용한 포인트
    private Integer discount; // 할인금액
    private Integer green_deposit; // 컵 보증금
}
