package com.kmarket.web.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    @GetMapping("/list")
    public String list() {
        return "product/list";
    }
    @GetMapping("/order")
    public String order() {
        return "product/order";
    }
    @GetMapping("/view")
    public String view() {
        return "product/view";
    }
    @GetMapping("/complete")
    public String complete() {
        return "product/complete";
    }
    @GetMapping("/cart")
    public String cart() {
        return "product/cart";
    }
}
