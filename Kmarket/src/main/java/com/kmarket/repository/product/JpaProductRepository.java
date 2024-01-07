package com.kmarket.repository.product;

import com.kmarket.domain.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<Products, Long> {
    Page<Products> findByCategory1CodeAndCategory2Code(Integer category1Code, Integer category2Code, Pageable pageable);

    List<Products> findTop8ByOrderByHitDesc();
    @Query(value = "SELECT * FROM km_product ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<Products> findRandomProducts();
    List<Products> findTop8ByOrderByRegistrationDateDesc();
    List<Products> findTop8ByOrderByDiscountRateDesc();
    List<Products> findTop5ByOrderBySoldNumberDesc(); // 베스트상품
}
