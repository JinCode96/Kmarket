package com.kmarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/")
    public String home() {
        return "admin/main";
    }
    @GetMapping("/register")
    public String register() {
        return "admin/register";
    }
    @GetMapping("/list")
    public String list() {
        return "admin/list";
    }
}
