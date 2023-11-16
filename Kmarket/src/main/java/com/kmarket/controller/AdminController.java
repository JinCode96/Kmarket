package com.kmarket.controller;

import com.kmarket.domain.Products;
import com.kmarket.dto.admin.ProductSaveForm;
import com.kmarket.security.PrincipalDetails;
import com.kmarket.service.AdminService;
import com.kmarket.util.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

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

    @GetMapping("/list")
    public String list() {
        return "admin/list";
    }
}
