package com.kmarket.repository.product;

import com.kmarket.dto.product.CategoryDTO;
import com.kmarket.dto.product.MemberPointDTO;
import com.kmarket.dto.product.OrderDTO;
import com.kmarket.dto.product.OrderItemDTO;

public interface ProductRepository {
    CategoryDTO getCategory(Integer cate1, Integer cate2);
    void increaseHit(Long productId);
    void increaseSoldNumber(Long productId);
    Integer findGeneralPoint(String LoginId);
    Integer findSellerPoint(String LoginId);
    int saveOrder(OrderDTO order);
    int saveOrderItem(OrderItemDTO orderItem);
    int saveMemberPoint(MemberPointDTO memberPoint);
    int updateGeneralMemberPoint(Integer savePoint, Integer usedPoint, String loginId);
    int updateSellerMemberPoint(Integer savePoint, Integer usedPoint, String loginId);
}
