package com.kmarket.controller;

import com.kmarket.api.ApiResponse;
import com.kmarket.constant.ApiResponseConst;
import com.kmarket.domain.Products;
import com.kmarket.dto.admin.ProductSaveForm;
import com.kmarket.security.PrincipalDetails;
import com.kmarket.service.AdminService;
import com.kmarket.util.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import static com.kmarket.constant.ApiResponseConst.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final FileStore fileStore;

    @GetMapping("/")
    public String home() {
        return "admin/main";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("products", new ProductSaveForm());
        return "admin/register";
    }

    /**
     * @Valid 설정 하기
     */
    @PostMapping("/register")
    public String insertProduct(@Validated @ModelAttribute("products") ProductSaveForm productSaveForm, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (bindingResult.hasErrors()) {
            log.info("bindingResult error = {}", bindingResult);
            return "admin/register";
        }
        log.info("ProductSaveForm={}", productSaveForm);

        Integer cate1 = productSaveForm.getCategory1Code();
        Integer cate2 = productSaveForm.getCategory2Code();
        String username = principalDetails.getUsername();

        try {
            // 각 파일에 대한 저장 처리
            String thumbnailList = fileStore.storeFile(productSaveForm.getThumbnailList(), cate1, cate2);
            String thumbnailMain = fileStore.storeFile(productSaveForm.getThumbnailMain(), cate1, cate2);
            String thumbnailDetail = fileStore.storeFile(productSaveForm.getThumbnailDetail(), cate1, cate2);
            String detailCut = fileStore.storeFile(productSaveForm.getDetailCut(), cate1, cate2);

            // Products 객체로 변환
            Products products = productSaveForm.productSaveFormToDomain(username, thumbnailList, thumbnailMain, thumbnailDetail, detailCut);

            // 상품 insert
            adminService.insertProduct(products);
        } catch (IOException e) {
            log.error("상품 등록 중 파일 저장 오류", e);
            return "error/errorPage";
        }
        return "redirect:/admin/list";
    }

    /**
     * 상품 목록, jpa 페이징 처리, 키워드 검색
     * SELLER (일반판매회원) 일 때, 해당 회원이 등록한 상품만 조회
     * ADMIN (관리자) 일 때, 모든 상품 조회
     */
    @GetMapping("/list")
    public String list(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String keyword,
            Model model,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String type = principalDetails.getMembers().getType();
        Page<Products> productsPage = null;

        if (type.equals("SELLER")) { // 일반 판매회원
            String username = principalDetails.getUsername();
            if (StringUtils.hasText(keyword) && StringUtils.hasText(searchField)) {
                productsPage = adminService.getProductsBySellerAndSearchField(username, searchField, keyword, pageable); // 검색기능을 사용
            } else {
                productsPage = adminService.getProductsBySeller(username, pageable); // 검색기능을 사용하지 않음
            }
        } else if (type.equals("ADMIN")) { // 관리자
            if (StringUtils.hasText(keyword) && StringUtils.hasText(searchField)) {
                productsPage = adminService.searchProducts(searchField, keyword, pageable); // 검색기능을 사용
            } else {
                productsPage = adminService.getProductsPage(pageable); // 검색기능을 사용하지 않음
            }
        }
        model.addAttribute("searchField", searchField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("productsPage", productsPage);
        return "admin/list";
    }

    /**
     * 이미지 보여주기
     */
    @ResponseBody
    @GetMapping("/images/{filename}/{cate1}/{cate2}")
    public Resource downloadImage(@PathVariable String filename, @PathVariable Integer cate1, @PathVariable Integer cate2) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename, cate1, cate2));
    }

    @ResponseBody
    @PostMapping("/deleteProduct")
    public ApiResponse deleteProduct(@RequestParam Long productId) {
        try {
            adminService.deleteProductById(productId);
            return new ApiResponse(PRODUCT_DELETE_OK, SUCCESS);
        } catch (RuntimeException e) {
            log.error("상품 삭제 시 에러 발생", e);
            return new ApiResponse(PRODUCT_DELETE_NOT_OK, FAIL);
        }
    }

    @ResponseBody
    @PostMapping("/deleteSelectedProducts")
    public ApiResponse deleteSelectedProducts(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> productIds = requestBody.get("productIds");
        log.info("productIds={}", productIds);
        try {
            adminService.deleteSelectedProducts(productIds);
            return new ApiResponse(PRODUCT_DELETE_OK, SUCCESS);
        } catch (RuntimeException e) {
            log.error("상품 삭제 시 에러 발생", e);
            return new ApiResponse(PRODUCT_DELETE_NOT_OK, SUCCESS);
        }
    }
}
