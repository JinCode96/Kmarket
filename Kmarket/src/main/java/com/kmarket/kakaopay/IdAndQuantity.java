package com.kmarket.kakaopay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdAndQuantity {
    private Long productId;
    private Integer quantity;
    private Integer point;
    private String productName;
    private String description;
    private Integer deliveryCost;
    private Integer discountedPrice;
    private Integer discountRate;
    private Integer price;
    private Integer category1Code;
    private Integer category2Code;
    private String thumbnailList;
}
