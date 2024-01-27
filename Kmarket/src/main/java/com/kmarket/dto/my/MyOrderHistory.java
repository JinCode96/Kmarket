package com.kmarket.dto.my;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyOrderHistory {
    private Long orderNumber;
    private String orderState;
    private String orderDate;
    private Long productId;
    private Integer discountedPrice;
    private Integer quantity;
    private String productName;
    private String thumbnailList;
    private Integer category1Code;
    private Integer category2Code;
}
