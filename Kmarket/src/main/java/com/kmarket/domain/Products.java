package com.kmarket.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Products {
    private Integer category1Code;
    private Integer category2Code;
    private String seller;
    private String productName;
    private String description;
    private String company;
    private Integer price;
    private Integer discountRate;
    private Integer point;
    private Integer stock;
    private Integer soldNumber;
    private Integer deliveryCost;
    private Integer hit;
    private Integer score;
    private Integer review;
    private String thumbnailList;
    private String thumbnailMain;
    private String thumbnailDetail;
    private String detailCut;
    private String status;
    private String duty;
    private String receipt;
    private String businessType;
    private String origin;
    private String productIp;
    private String registrationDate;
}
