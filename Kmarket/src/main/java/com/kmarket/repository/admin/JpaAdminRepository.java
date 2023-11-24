package com.kmarket.repository.admin;

import com.kmarket.domain.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAdminRepository extends JpaRepository<Products, Long> {
    Optional<Products> findById(Long id);
    // ADMIN
    Page<Products> findByProductNameContaining(String keyword, Pageable pageable);
    Page<Products> findByCompanyContaining(String keyword, Pageable pageable);
    Page<Products> findBySellerContaining(String keyword, Pageable pageable);
    Page<Products> findById(Long id, Pageable pageable);

    // SELLER
    Page<Products> findBySeller(String seller, Pageable pageable);
    Page<Products> findBySellerAndProductNameContaining(String keyword, String seller, Pageable pageable);
    Page<Products> findBySellerAndId(String seller,Long id, Pageable pageable);
    Page<Products> findBySellerAndCompanyContaining(String keyword, String seller, Pageable pageable);
    Page<Products> findBySellerAndSellerContaining(String keyword, String seller, Pageable pageable);

}
