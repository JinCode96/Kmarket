package com.kmarket.dto.admin;

import com.kmarket.domain.Products;
import com.kmarket.validation.ValidFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductSaveForm {

    private Long id;
    @NotNull(message = "{required.product.category1Code}")
    private Integer category1Code;

    @NotNull(message = "{required.product.category2Code}")
    private Integer category2Code;

    @NotBlank(message = "{required.product.productName}")
    private String productName;

    @NotBlank(message = "{required.product.description}")
    private String description;

    @NotBlank(message = "{required.product.company}")
    private String company;

    @NotNull(message = "{required.product.price}")
    @Range(min=0, max=100_000_000, message = "{range.product.price}")
    private Integer price;

    @NotNull(message = "{required.product.discountRate}")
    @Range(min=0, max =100, message = "{range.product.discountRate}")
    private Integer discountRate;

    @NotNull(message = "{required.product.point}")
    @Range(min = 0, max = 10_000, message = "{range.product.point}")
    private Integer point;

    @NotNull(message = "{required.product.stock}")
    @Range(min = 0, max = 1_000_000, message = "{range.product.stock}")
    private Integer stock;

    @NotNull(message = "{required.product.deliveryCost}")
    @Range(min = 0, max = 10_000, message = "{range.product.deliveryCost}")
    private Integer deliveryCost;

    @ValidFile(message = "{required.product.file}")
    private MultipartFile thumbnailList;

    @ValidFile(message = "{required.product.file}")
    private MultipartFile thumbnailMain;

    @ValidFile(message = "{required.product.file}")
    private MultipartFile thumbnailDetail;

    @ValidFile(message = "{required.product.file}")
    private MultipartFile detailCut;

    @NotBlank(message = "{required.product.status}")
    private String status;

    @NotBlank(message = "{required.product.duty}")
    private String duty;

    @NotBlank(message = "{required.product.receipt}")
    private String receipt;

    @NotBlank(message = "{required.product.businessType}")
    private String businessType;

    @NotBlank(message = "{required.product.origin}")
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
