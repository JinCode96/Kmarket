package com.kmarket.service;

import com.kmarket.domain.Products;
import com.kmarket.dto.admin.ProductSaveForm;
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
import java.util.Optional;

import static com.kmarket.constant.ApiResponseConst.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final JpaAdminRepository jpaAdminRepository;

    /**
     * mybatis
     * 상품 등록
     */
    public void insertProduct(Products products) {
        adminRepository.insertProduct(products);
    }

    /**
     * ADMIN 회원
     * 모든 상품 보기
     * jpa 페이징 처리
     */
    public Page<Products> getProductsPage(Pageable pageable) {
        return jpaAdminRepository.findAll(pageable);
    }
    
    /**
     * ADMIN 회원
     * 검색된 상품 보기
     * jpa 페이징 처리
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
     * jpa 페이징
     */
    public Page<Products> getProductsBySeller(String seller, Pageable pageable) {
        return jpaAdminRepository.findBySeller(seller, pageable);
    }

    /**
     * SELLER 회원
     * 검색된 상품 보기
     * jpa 페이징
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

    /**
     * 상품 삭제
     */
    public void deleteProductById(Long productId) {
        jpaAdminRepository.deleteById(productId);
    }

    /**
     * 상품 다중 삭제
     */
    public void deleteSelectedProducts(List<Long> productIds) {
        for (Long productId : productIds) {
            jpaAdminRepository.deleteById(productId);
        }
    }

    /**
     * 상품 가져오기
     */
    public Optional<Products> findById(Long id) {
        return jpaAdminRepository.findById(id);
    }

    /**
     * 상품 수정
     * jpa
     */
    public void update(ProductSaveForm productSaveForm, String thumbnailList, String thumbnailMain, String thumbnailDetail, String detailCut) {
        Products findProduct = jpaAdminRepository.findById(productSaveForm.getId()).orElseThrow();
        findProduct.setCategory1Code(productSaveForm.getCategory1Code());
        findProduct.setCategory2Code(productSaveForm.getCategory2Code());
        findProduct.setProductName(productSaveForm.getProductName());
        findProduct.setDescription(productSaveForm.getDescription());
        findProduct.setCompany(productSaveForm.getCompany());
        findProduct.setPrice(productSaveForm.getPrice());
        findProduct.setDiscountRate(productSaveForm.getDiscountRate());
        findProduct.setDiscountedPrice(productSaveForm.getPrice() - Math.round(productSaveForm.getPrice() * productSaveForm.getDiscountRate() / 100)); // 할인된 금액 산출
        findProduct.setPoint(productSaveForm.getPoint());
        findProduct.setStock(productSaveForm.getStock());
        findProduct.setDeliveryCost(productSaveForm.getDeliveryCost());
        findProduct.setThumbnailList(thumbnailList);
        findProduct.setThumbnailMain(thumbnailMain);
        findProduct.setThumbnailDetail(thumbnailDetail);
        findProduct.setDetailCut(detailCut);
        findProduct.setStatus(productSaveForm.getStatus());
        findProduct.setDuty(productSaveForm.getDuty());
        findProduct.setBusinessType(productSaveForm.getBusinessType());
        findProduct.setOrigin(productSaveForm.getOrigin());
    }

}

