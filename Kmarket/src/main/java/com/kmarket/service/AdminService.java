package com.kmarket.service;

import com.kmarket.domain.Products;
import com.kmarket.repository.admin.AdminRepository;
import com.kmarket.repository.admin.JpaAdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.kmarket.constant.ApiResponseConst.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final JpaAdminRepository jpaAdminRepository;

    public void insertProduct(Products products) {
        adminRepository.insertProduct(products);
    }

    /**
     * ADMIN 회원
     * 모든 상품 보기
     */
    public Page<Products> getProductsPage(Pageable pageable) {
        return jpaAdminRepository.findAll(pageable);
    }
    /**
     * ADMIN 회원
     * 검색된 상품 보기
     */
    public Page<Products> searchProducts(String searchField, String keyword, Pageable pageable) {
        switch (searchField) {
            case "productName" -> {
                return jpaAdminRepository.findByProductNameContaining(keyword, pageable);
            }
            case "id" -> {
                try {
                    Long id = Long.parseLong(keyword);
                    return jpaAdminRepository.findById(id, pageable);
                } catch (NumberFormatException e) {
                    log.warn("검색어를 숫자로 변환할 수 없습니다.", e);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
                }
            }
            case "company" -> {
                return jpaAdminRepository.findByCompanyContaining(keyword, pageable);
            }
            case "seller" -> {
                return jpaAdminRepository.findBySellerContaining(keyword, pageable);
            }
            default -> {
                log.warn("유효하지 않은 검색필드={}, keyword={}", searchField, keyword);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
            }
        }
    }

    /**
     * SELLER 회원
     * 모든 상품 보기
     */
    public Page<Products> getProductsBySeller(String seller, Pageable pageable) {
        return jpaAdminRepository.findBySeller(seller, pageable);
    }
    /**
     * SELLER 회원
     * 검색된 상품 보기
     */
    public Page<Products> getProductsBySellerAndSearchField(String seller, String searchField, String keyword, Pageable pageable) {
        switch (searchField) {
            case "productName" -> {
                return jpaAdminRepository.findBySellerAndProductNameContaining(seller, keyword, pageable);
            }
            case "id" -> {
                try {
                    Long id = Long.parseLong(keyword);
                    return jpaAdminRepository.findBySellerAndId(seller, id, pageable);
                } catch (NumberFormatException e) {
                    log.warn("검색어를 숫자로 변환할 수 없습니다.", e);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
                }
            }
            case "company" -> {
                return jpaAdminRepository.findBySellerAndCompanyContaining(seller, keyword, pageable);
            }
            case "seller" -> {
                return jpaAdminRepository.findBySellerAndSellerContaining(seller, keyword, pageable);
            }
            default -> {
                log.warn("유효하지 않은 검색필드={}, keyword={}", searchField, keyword);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
            }
        }
    }

    public void deleteProductById(Long productId) {
        jpaAdminRepository.deleteById(productId);
    }

    public void deleteSelectedProducts(List<Long> productIds) {
        for (Long productId : productIds) {
            jpaAdminRepository.deleteById(productId);
        }
    }
}

