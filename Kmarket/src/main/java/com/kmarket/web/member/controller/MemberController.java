package com.kmarket.web.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/join")
    public String join() {
        return "member/join";
    }

    @GetMapping("/register")
    public String register() {
        return "member/register";
    }

    @GetMapping("/register-seller")
    public String registerSeller() {
        return "member/registerSeller";
    }

    @GetMapping("/sing-up")
    public String singUp() {
        return "member/signup";
    }
}
