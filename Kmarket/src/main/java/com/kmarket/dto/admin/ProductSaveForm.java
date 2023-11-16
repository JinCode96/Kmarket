package com.kmarket.dto.admin;

import com.kmarket.domain.Products;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ProductSaveForm {
    @NotNull
    private Integer category1Code;
    @NotNull
    private Integer category2Code;
    @NotBlank
    private String productName;
    @NotBlank
    private String description;
    @NotBlank
    private String company;
    @NotNull
    private Integer price;
    @NotNull
    private Integer discountRate;
    @NotNull
    private Integer point;
    @NotNull
    private Integer stock;
    @NotNull
    private Integer deliveryCost;
    @NotNull
    private MultipartFile thumbnailList;
    @NotNull
    private MultipartFile thumbnailMain;
    @NotNull
    private MultipartFile thumbnailDetail;
    @NotNull
    private MultipartFile detailCut;
    @NotBlank
    private String status;
    @NotBlank
    private String duty;
    @NotBlank
    private String receipt;
    @NotBlank
    private String businessType;
    @NotBlank
    private String origin;

    public Products productSaveFormToDomain(String username, String thumbnailList, String thumbnailMain, String thumbnailDetail, String detailCut) {
        return new Products().builder()
                .category1Code(this.getCategory1Code())
                .category2Code(this.getCategory2Code())
                .seller(username)
                .productName(this.getProductName())
                .description(this.getDescription())
                .company(this.getCompany())
                .price(this.getPrice())
                .discountRate(this.getDiscountRate())
                .point(this.getPoint())
                .stock(this.getStock())
                .deliveryCost(this.getDeliveryCost())
                .thumbnailList(thumbnailList)
                .thumbnailMain(thumbnailMain)
                .thumbnailDetail(thumbnailDetail)
                .detailCut(detailCut)
                .status(this.getStatus())
                .duty(this.getDuty())
                .receipt(this.getReceipt())
                .businessType(this.getBusinessType())
                .origin(this.getOrigin())
                .build();
    }
}
