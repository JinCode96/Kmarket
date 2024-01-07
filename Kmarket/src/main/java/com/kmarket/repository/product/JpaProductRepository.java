package com.kmarket.repository.product;

import com.kmarket.domain.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<Products, Long> {
    Page<Products> findByCategory1CodeAndCategory2Code(Integer category1Code, Integer category2Code, Pageable pageable);
}
