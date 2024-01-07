package com.kmarket.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "km_product_review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String uid;
    private String content;
    private Integer rating;
    private String registrationDate;
}
