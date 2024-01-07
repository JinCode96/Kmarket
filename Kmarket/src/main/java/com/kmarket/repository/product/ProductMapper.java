package com.kmarket.repository.product;

import com.kmarket.dto.product.CategoryDTO;
import com.kmarket.dto.product.MemberPointDTO;
import com.kmarket.dto.product.OrderDTO;
import com.kmarket.dto.product.OrderItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {
    CategoryDTO getCategory(@Param("cate1") Integer cate1, @Param("cate2") Integer cate2);
    void increaseHit(Long productId);
    void increaseSoldNumber(Long productId);
    Integer findGeneralPoint(String LoginId);
    Integer findSellerPoint(String LoginId);
    int saveOrder(OrderDTO order);
    int saveOrderItem(OrderItemDTO orderItem);
    int saveMemberPoint(MemberPointDTO memberPoint);
    int updateGeneralMemberPoint(@Param("savePoint") Integer savePoint, @Param("usedPoint") Integer usedPoint, @Param("loginId") String loginId);
    int updateSellerMemberPoint(@Param("savePoint") Integer savePoint, @Param("usedPoint") Integer usedPoint, @Param("loginId") String loginId);
}
