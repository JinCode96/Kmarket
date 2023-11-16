package com.kmarket.repository.admin;

import com.kmarket.domain.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository{

    private final AdminMapper adminMapper;

    @Override
    public int insertProduct(Products products) {
        return adminMapper.insertProduct(products);
    }
}
