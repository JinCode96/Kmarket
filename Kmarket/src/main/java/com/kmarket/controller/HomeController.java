package com.kmarket.controller;

import com.kmarket.domain.Products;
import com.kmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    /**
     * 홈 화면
     */
    @GetMapping("/")
    public String home(Model model) {

        List<Products> hitProds = productService.findTop8ByOrderByHitDesc(); // 조회순 (히트상품)
        List<Products> recommendProds = productService.findRandomProducts(); // 랜덤 (추천상품)
        List<Products> recentProds = productService.findTop8ByOrderByRegistrationDateDesc(); // 최신순 (최신상품)
        List<Products> discountProds = productService.findTop8ByOrderByDiscountRateDesc(); // 할인순 (할인상품)
        List<Products> bestProds = productService.findTop5ByOrderBySoldNumberDesc(); // 판매순 (베스트상품)

        model.addAttribute("hitProds", hitProds);
        model.addAttribute("recommendProds", recommendProds);
        model.addAttribute("recentProds", recentProds);
        model.addAttribute("discountProds", discountProds);
        model.addAttribute("bestProds", bestProds);
        return "index";
    }

}
