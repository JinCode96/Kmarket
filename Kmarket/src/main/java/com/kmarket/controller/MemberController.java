package com.kmarket.controller;

import com.kmarket.api.ApiResponse;
import com.kmarket.domain.Members;
import com.kmarket.dto.member.EmailDTO;
import com.kmarket.dto.member.LoginIdDTO;
import com.kmarket.dto.member.TermsDTO;
import com.kmarket.dto.member.GeneralMemberDTO;
import com.kmarket.entity.TermsEntity;
import com.kmarket.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import static com.kmarket.constant.MemberConst.*;


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
    public String termsValid(TermsDTO terms, HttpSession session, @PathVariable String type, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("type", type);

        if (terms.isTermsTerms() && terms.isTermsFinance() && terms.isTermsPrivacy()) { // 서버 사이드 검증

            // todo spring security 에서 세션에 값이 있으면 회원가입 페이지로 갈 수 있고, 없으면 갈 수 없게 하기.
            session.setAttribute("termsAgreed", true);
            return "redirect:/member/register/{type}";
        }
        return "redirect:/member/sign-up/{type}";
    }

    /**
     * 회원가입 폼
     * referer 을 통해 url 직접 접근을 막는다.
     */
    @GetMapping("/register/{type}")
    public String registerForm(@PathVariable String type, Model model) {

        model.addAttribute("member", new Members()); // th:object 사용 위해서

        if (type.equals(GENERAL)) {
            return "member/registerGeneral";
        } else if (type.equals(SELLER)) {
            return "member/registerSeller";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "잘못된 url 요청");
    }

    @ResponseBody
    @PostMapping("/register/general")
    public ApiResponse saveMember1(@Validated @RequestBody GeneralMemberDTO generalMemberDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { // todo 오류 화면 내려주기
            log.info("비정상적인 접근!!!");
            model.addAttribute("error", true);
        }
        log.info("member={}", generalMemberDTO.toString());
        int result = memberService.saveGeneralMember(generalMemberDTO.generalDTOToDomain());
        if (result == 1) {
            return new ApiResponse("회원가입이 완료되었습니다. 로그인 해주세요.", result);
        } else {
            return new ApiResponse("회원가입에 실패하였습니다. 다시 회원가입을 진행해주세요.", result);
        }
    }

    /**
     * 아이디 중복 검사
     */
    @ResponseBody
    @PostMapping("/register/checkLoginId")
    public ApiResponse checkLoginId(@RequestBody LoginIdDTO loginId) {
        int result = memberService.checkGeneralLoginId(loginId.getLoginId());
        if (result == 1) {
            return new ApiResponse("이미 사용중인 아이디 입니다.", result);
        } else {
            return new ApiResponse("사용 가능한 아이디 입니다.", result);
        }
    }

    /**
     * 이메일 중복 검사
     */
    @ResponseBody
    @PostMapping("/register/checkEmail")
    public ApiResponse checkEmail(@RequestBody EmailDTO email) {
        int result = memberService.checkEmail(email.getEmail());
        if (result == 1) {
            return new ApiResponse("이미 사용중인 이메일입니다.", result);
        } else {
            return new ApiResponse("사용 가능한 이메일입니다.", result);
        }
    }


}