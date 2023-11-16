package com.kmarket.service;

import com.kmarket.domain.Products;
import com.kmarket.repository.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    public int insertProduct(Products products) {
        return adminRepository.insertProduct(products);
    }
}
