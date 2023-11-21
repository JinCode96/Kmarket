package com.kmarket.repository.admin;

import com.kmarket.domain.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository{

    private final AdminMapper adminMapper;

    @Override
    public void insertProduct(Products products) {
        adminMapper.insertProduct(products);
    }
}
