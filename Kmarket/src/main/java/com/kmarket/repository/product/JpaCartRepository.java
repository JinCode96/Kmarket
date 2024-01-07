package com.kmarket.repository.product;

import com.kmarket.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByProductIdAndUid(Long productId, String uid);

    List<Cart> findByUid(String uid);

    void deleteByProductIdAndUid(Long productId, String uid);
}
