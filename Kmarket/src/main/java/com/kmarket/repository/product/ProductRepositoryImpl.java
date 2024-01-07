package com.kmarket.repository.product;

import com.kmarket.dto.product.CategoryDTO;
import com.kmarket.dto.product.MemberPointDTO;
import com.kmarket.dto.product.OrderDTO;
import com.kmarket.dto.product.OrderItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    public final ProductMapper productMapper;

    @Override
    public CategoryDTO getCategory(Integer cate1, Integer cate2) {
        return productMapper.getCategory(cate1, cate2);
    }

    @Override
    public void increaseHit(Long productId) {
        productMapper.increaseHit(productId);
    }

    @Override
    public void increaseSoldNumber(Long productId) {
        productMapper.increaseSoldNumber(productId);
    }

    @Override
    public Integer findGeneralPoint(String LoginId) {
        return productMapper.findGeneralPoint(LoginId);
    }

    @Override
    public Integer findSellerPoint(String LoginId) {
        return productMapper.findSellerPoint(LoginId);
    }

    @Override
    public int saveOrder(OrderDTO order) {
        return productMapper.saveOrder(order);
    }

    @Override
    public int saveOrderItem(OrderItemDTO orderItem) {
        return productMapper.saveOrderItem(orderItem);
    }

    @Override
    public int saveMemberPoint(MemberPointDTO memberPoint) {
        return productMapper.saveMemberPoint(memberPoint);
    }

    @Override
    public int updateGeneralMemberPoint(Integer savePoint, Integer usedPoint, String loginId) {
        return productMapper.updateGeneralMemberPoint(savePoint, usedPoint, loginId);
    }

    @Override
    public int updateSellerMemberPoint(Integer savePoint, Integer usedPoint, String loginId) {
        return productMapper.updateSellerMemberPoint(savePoint, usedPoint, loginId);
    }
}
