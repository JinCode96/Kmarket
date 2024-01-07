package com.kmarket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 장바구니 Entity
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "km_product_cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String uid;
    private String productName;
    private Integer quantity;
    private Integer price;
    private Integer discountRate;
    private Integer point;
    private Integer deliveryCost;
    private Integer discountedPrice;
    private String thumbnailList;
    @Column(name = "category1_code")
    private Integer category1Code;
    @Column(name = "category2_code")
    private Integer category2Code;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @PrePersist
    public void prePersist() {
        this.registrationDate = LocalDateTime.now();
    }
}
