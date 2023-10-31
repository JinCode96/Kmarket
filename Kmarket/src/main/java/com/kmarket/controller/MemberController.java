package com.kmarket.controller;

import com.kmarket.api.ApiResponse;
import com.kmarket.domain.Members;
import com.kmarket.dto.member.*;
import com.kmarket.entity.TermsEntity;
import com.kmarket.service.MemberService;
import com.kmarket.util.EmailService;
import com.kmarket.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.*;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    @GetMapping("/login")
    public String loginForm(String failCheck, Model model) {
        model.addAttribute("failCheck", failCheck);
        return "member/loginForm";
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
     * 체크박스 검증
     */
    @ResponseBody
    @PostMapping("/sign-up/{type}")
    public ApiResponse termsValid(@RequestBody TermsDTO terms, HttpSession session, @PathVariable String type) {
        if (termsOK(terms)) { // 서버 사이드 검증 실패
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        } else { // todo spring security 에서 세션에 값이 있으면 회원가입 페이지로 갈 수 있고, 없으면 갈 수 없게 하기.
            session.setAttribute("termsAgreed", true);
            return new ApiResponse(TERMS_OK, 200);
        }
    }

    private boolean termsOK(TermsDTO terms) {
        return !terms.isTermsTerms() || !terms.isTermsFinance() || !terms.isTermsPrivacy();
    }

    /**
     * 회원가입 폼
     */
    @GetMapping("/register/{type}")
    public String registerForm(@PathVariable String type, Model model) {

        log.info("registerForm...");
        model.addAttribute("member", new Members()); // th:object 사용 위해서

        if (type.equals(GENERAL)) {
            log.info("general");
            return "member/registerGeneral";
        } else if (type.equals(SELLER)) {
            log.info("seller");
            return "member/registerSeller";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "잘못된 url 요청");
    }

    /**
     * 일반 회원가입
     */
    @ResponseBody
    @PostMapping("/register/general")
    public ApiResponse saveGeneral(@Validated @RequestBody GeneralMemberDTO generalMemberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(NOT_FOUND, 400);
        }
        int result = memberService.saveGeneralMember(generalMemberDTO.generalDTOToDomain());
        if (result == 1) {
            return new ApiResponse(REGISTER_OK, result);
        }
        return new ApiResponse(REGISTER_NOT_OK, result);
    }

    /**
     * 판매자 회원가입
     */
    @ResponseBody
    @PostMapping("/register/seller")
    public ApiResponse saveSeller(@Validated @RequestBody SellerMemberDTO sellerMemberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(NOT_FOUND, 400);
        }
        int result = memberService.saveSellerMember(sellerMemberDTO.generalDTOToDomain());
        if (result == 1) {
            return new ApiResponse(REGISTER_OK, result);
        }
        return new ApiResponse(REGISTER_NOT_OK, result);
    }

    @GetMapping("/findId")
    public String findIdForm() {
        return "member/findId";
    }

    @GetMapping("/findPw")
    public String findPwForm() {
        return "member/findPw";
    }

//    @GetMapping("/findIdResult")
//    public String findIdResult(Members members, Model model) {
//        model.addAttribute("members", members);
//        return "member/findIdResult";
//    }

    @GetMapping("/findPwResult")
    public String findPwResult() {
        return "member/findPwResult";
    }

    /**
     * 아이디 중복 검사
     */
    @ResponseBody
    @PostMapping("/register/checkLoginId")
    public ApiResponse checkLoginId(@RequestBody LoginIdDTO loginId) {
        int result = memberService.checkLoginId(loginId.getLoginId());
        if (result == 1) {
            return new ApiResponse(ID_NOT_OK, result);
        } else {
            return new ApiResponse(ID_OK, result);
        }
    }

    /**
     * 이메일 중복 검사
     */
    @ResponseBody
    @PostMapping("/register/checkEmail")
    public ApiResponse checkEmail(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        int result = memberService.checkEmail(searchIdAndPassDTO.getEmail());
        if (result == 1) {
            return new ApiResponse(EMAIL_NOT_OK, result);
        } else {
            return new ApiResponse(EMAIL_OK, result);
        }
    }

    /**
     * 이름과 이메일로 회원 찾기
     */
    @ResponseBody
    @PostMapping("/checkMemberNameAndEmail")
    public ApiResponse checkMemberNameAndEmail(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        int result = memberService.checkMemberNameAndEmail(searchIdAndPassDTO);
        if (result == 1) {
            return new ApiResponse(MEMBER_FOUND, result);
        } else {
            return new ApiResponse(MEMBER_NOT_FOUND, result);
        }
    }

    /**
     * 이메일 인증번호 전송
     */
    @ResponseBody
    @PostMapping("/mailConfirm")
    public ApiResponse mailConfirm(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) throws MessagingException, UnsupportedEncodingException {
        emailService.sendEmail(searchIdAndPassDTO.getEmail());
        return new ApiResponse(EMAIL_CODE_OK, 200);
    }

    /**
     * redis 코드 인증
     */
    @ResponseBody
    @PostMapping("/codeConfirm")
    public ApiResponse codeConfirm(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        String value = redisUtil.getData(searchIdAndPassDTO.getAuthCode());
        if (value == null) {
            return new ApiResponse("코드 맞지 않거나 만료된 코드임", 400);
        } else {
            return new ApiResponse("코드 확인 완료", 200);
        }
    }

    /**
     * 아이디 찾기
     */
    @PostMapping("/searchId")
    public String searchId(@ModelAttribute SearchIdAndPassDTO searchIdAndPassDTO, Model model) {
        searchIdAndPassDTO.makeEmail();
        log.info("searchIdAndPassDTO={}", searchIdAndPassDTO);
        Members members = memberService.searchId(searchIdAndPassDTO);
        log.info("members = {}", members);

        model.addAttribute("members", members);

        if (members != null) {
            return "member/findIdResult";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "잘못된 url 요청");
        }
    }
}