package com.kmarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyController {
    @GetMapping("/home")
    public String home() {
        return "my/home";
    }
    @GetMapping("/info")
    public String info() {
        return "my/info";
    }

}
