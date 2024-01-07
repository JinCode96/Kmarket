package com.kmarket.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 장바구니 상품 연산
 */
@Getter
@Setter
@ToString
public class CartSummary {
    private int totalCartPrice;      // 총 상품 금액
    private int totalDiscount;       // 총 할인 금액
    private int totalDeliveryCost;   // 총 배송비
    private int totalAmount;         // 총 결제 금액
    
    public void addProduct(Cart cart) {
        int productPrice = cart.getPrice() * cart.getQuantity();
        int productDiscount = (cart.getPrice() * cart.getDiscountRate() / 100) * cart.getQuantity();
        int productDeliveryCost = cart.getDeliveryCost();
        int productAmount = cart.getDiscountedPrice() * cart.getQuantity();

        totalCartPrice += productPrice;
        totalDiscount += productDiscount;
        totalDeliveryCost += productDeliveryCost;
        totalAmount += productAmount;
    }
}
