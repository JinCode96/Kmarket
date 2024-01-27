package com.kmarket.controller;

import com.kmarket.api.ApiResponse;
import com.kmarket.constant.ApiResponseConst;
import com.kmarket.constant.MemberConst;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.*;

/**
 * 관리자 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final FileStore fileStore;

    /**
     * 관리자 메인 화면
     */
    @GetMapping("/")
    public String home() {
        return "admin/main";
    }

    /**
     * 상품 등록 화면
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("products", new ProductSaveForm()); // th:object 를 위해
        return "admin/register";
    }

    /**
     * 상품 등록 POST
     * @Validated 사용
     */
    @PostMapping("/register")
    public String insertProduct(@Validated @ModelAttribute("products") ProductSaveForm productSaveForm, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("상품 등록...");
        if (bindingResult.hasErrors()) {
            log.info("bindingResult error = {}", bindingResult);
            return "admin/register";
        }

        Integer cate1 = productSaveForm.getCategory1Code();
        Integer cate2 = productSaveForm.getCategory2Code();
        String username = principalDetails.getUsername();
        
        // 한번에 처리
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
     * 상품 목록 화면
     * jpa 페이징 처리, 키워드 검색
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

        if (type.equals(SELLER_UPPER)) { // 일반 판매회원
            String username = principalDetails.getUsername();
            if (StringUtils.hasText(keyword) && StringUtils.hasText(searchField)) {
                productsPage = adminService.getProductsBySellerAndSearchField(username, searchField, keyword, pageable); // 검색기능을 사용
            } else {
                productsPage = adminService.getProductsBySeller(username, pageable); // 검색기능을 사용하지 않음
            }

        } else if (type.equals(ADMIN_UPPER)) { // 관리자
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

    /**
     * 상품 단일 삭제 Delete
     */
    @ResponseBody
    @DeleteMapping("/deleteProduct")
    public ApiResponse deleteProduct(@RequestParam Long productId) {
        log.info("상품 단일 삭제...");
        try {
            adminService.deleteProductById(productId);
            return new ApiResponse(PRODUCT_DELETE_OK, SUCCESS);
        } catch (RuntimeException e) {
            log.error("상품 삭제 시 에러 발생", e);
            return new ApiResponse(PRODUCT_DELETE_NOT_OK, FAIL);
        }
    }

    /**
     * 상품 다중 선택 삭제
     */
    @ResponseBody
    @DeleteMapping("/deleteSelectedProducts")
    public ApiResponse deleteSelectedProducts(@RequestBody Map<String, List<Long>> requestBody) {
        log.info("상품 다중 삭제...");
        List<Long> productIds = requestBody.get("productIds");
        try {
            adminService.deleteSelectedProducts(productIds);
            return new ApiResponse(PRODUCT_DELETE_OK, SUCCESS);
        } catch (RuntimeException e) {
            log.error("상품 삭제 시 에러 발생", e);
            return new ApiResponse(PRODUCT_DELETE_NOT_OK, FAIL);
        }
    }

    /**
     * 상품 수정 화면
     */
    @GetMapping("/update")
    public String updateForm(Long productId, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String username = principalDetails.getUsername();
        Products product = adminService.findById(productId).orElse(null);
        if (product != null) {
            // 로그인된 회원과 상품을 등록한 판매자와 같지 않을 때
            if (!username.equals(product.getSeller())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
            }
            model.addAttribute("product", product);
            return "admin/update";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }
    }

    /**
     * 상품 수정 POST
     * @Validated 사용
     */
    @PostMapping("/update")
    public String update(@Validated ProductSaveForm productSaveForm, BindingResult bindingResult) {
        log.info("상품 수정...");
        if (bindingResult.hasErrors()) {
            log.info("bindingResult error = {}", bindingResult);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }
        Integer cate1 = productSaveForm.getCategory1Code();
        Integer cate2 = productSaveForm.getCategory2Code();
        try {
            // 파일 로컬에 저장
            String thumbnailList = fileStore.storeFile(productSaveForm.getThumbnailList(), cate1, cate2);
            String thumbnailMain = fileStore.storeFile(productSaveForm.getThumbnailMain(), cate1, cate2);
            String thumbnailDetail = fileStore.storeFile(productSaveForm.getThumbnailDetail(), cate1, cate2);
            String detailCut = fileStore.storeFile(productSaveForm.getDetailCut(), cate1, cate2);
            // 상품 수정
            adminService.update(productSaveForm, thumbnailList, thumbnailMain, thumbnailDetail, detailCut);
        } catch (IOException e) {
            log.error("상품 수정 중 파일 저장 오류", e);
            return "error/errorPage";
        }
        return "redirect:/admin/list";
    }
}
