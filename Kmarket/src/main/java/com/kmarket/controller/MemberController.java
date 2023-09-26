package com.kmarket.controller;

import com.kmarket.dto.TermsDto;
import com.kmarket.entity.TermsEntity;
import com.kmarket.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/join")
    public String join() {
        return "member/join";
    }

    /**
     * 이용 약관 폼
     */
    @GetMapping("/sign-up/{type}")
    public String signUp(@PathVariable String type, Model model) {
        TermsEntity terms = memberService.getTerms().get();
        model.addAttribute("terms", terms);
        model.addAttribute("type", type);
        return "member/signup";
    }

    /**
     * 이용 약관 POST
     * 체크박스 검증 후 리다이렉트
     */
    @PostMapping("/sign-up/{type}")
    public String termsValid(TermsDto terms, HttpSession session, @PathVariable String type, RedirectAttributes redirectAttributes) {
        log.info("terms={}", terms);
        redirectAttributes.addAttribute("type", type);

        // 필수 체크 모두 true 면 세션 만들기.
        if (terms.isTermsTerms() && terms.isTermsFinance() && terms.isTermsPrivacy()) {
            // 이용약관 동의 여부 세션에 저장
            // todo spring security 에서 세션에 값이 있으면 회원가입 페이지로 갈 수 있고, 없으면 갈 수 없게 하기.
            session.setAttribute("termsAgreed", true);
            
            // 모두 true 면 회원가입 페이지로 리다이렉트
            return "redirect:/member/register/{type}";
        }

        // 검증 실패 하면 돌려 보내기
        return "redirect:/member/sign-up/{type}";
    }

    /**
     * 회원가입 폼
     * referer 을 통해 url 직접 접근을 막는다.
     */
    @GetMapping("/register/{type}")
    public String registerGeneral(@PathVariable String type) {

//        model.addAttribute("referer", request.getHeader("referer")); // url 직접 접근 자바스크립트로 막기

        if (type.equals("general")) {
            return "member/registerGeneral";
        } else if (type.equals("seller")) {
            return "member/registerSeller";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "잘못된 url 요청"); // 404
    }

    @GetMapping("/test")
    public String test() {
        return "member/test";
    }
}
