package com.kmarket.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderItemDTO {
    private Long OrderNumber;
    private Long productId;
    private Integer quantity;
}
