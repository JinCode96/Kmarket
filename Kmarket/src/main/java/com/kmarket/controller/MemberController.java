package com.kmarket.controller;

import com.kmarket.api.ApiResponse;
import com.kmarket.domain.Members;
import com.kmarket.dto.member.*;
import com.kmarket.domain.Terms;
import com.kmarket.service.MemberService;
import com.kmarket.util.EmailService;
import com.kmarket.util.RedisUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.*;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    /**
     * 로그인 화면
     */
    @GetMapping("/login")
    public String loginForm(String failCheck, Model model) {
        model.addAttribute("failCheck", failCheck);
        return "member/loginForm";
    }

    /**
     * join 화면
     */
    @GetMapping("/join")
    public String join() {
        return "member/join";
    }

    /**
     * 이용 약관 화면
     */
    @GetMapping("/sign-up/{type}")
    public String signUp(@PathVariable String type, Model model) {
        Terms terms = memberService.getTerms().orElse(null);
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
    public ApiResponse termsValid(@PathVariable String type, @RequestBody TermsDTO terms) {
        if (!terms.isTermsTerms() || !terms.isTermsFinance() || !terms.isTermsPrivacy()) { // 서버 사이드 검증 실패
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        } else {
//            session.setAttribute("termsAgreed", true);
            return new ApiResponse(TERMS_OK, 200);
        }
    }

    /**
     * 회원가입 화면
     */
    @GetMapping("/register/{type}")
    public String registerForm(@PathVariable String type, Model model) {
        model.addAttribute("member", new Members()); // th:object 사용 위해서
        if (type.equals(GENERAL_LOWER)) {
            return "member/registerGeneral";
        } else if (type.equals(SELLER_LOWER)) {
            return "member/registerSeller";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND);
    }

    /**
     * 일반 회원가입 post
     */
    @ResponseBody
    @PostMapping("/register/general")
    public ApiResponse saveGeneral(@Validated @RequestBody GeneralMemberDTO generalMemberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(BAD_REQUEST, 400);
        }
        generalMemberDTO.setPassword(passwordEncoder.encode(generalMemberDTO.getPassword())); // 패스워드 암호화
        int result = memberService.saveGeneralMember(generalMemberDTO.generalDTOToDomain());
        if (result == 1) {
            return new ApiResponse(REGISTER_OK, result);
        }
        return new ApiResponse(REGISTER_NOT_OK, result);
    }

    /**
     * 판매자 회원가입 post
     */
    @ResponseBody
    @PostMapping("/register/seller")
    public ApiResponse saveSeller(@Validated @RequestBody SellerMemberDTO sellerMemberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(BAD_REQUEST, 400);
        }
        sellerMemberDTO.setPassword(passwordEncoder.encode(sellerMemberDTO.getPassword()));
        int result = memberService.saveSellerMember(sellerMemberDTO.generalDTOToDomain());
        if (result == 1) {
            return new ApiResponse(REGISTER_OK, result);
        }
        return new ApiResponse(REGISTER_NOT_OK, result);
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
            return new ApiResponse(CODE_NOT_OK, 400);
        } else {
            return new ApiResponse(CODE_OK, 200);
        }
    }

    /**
     * 아이디 찾기
     */
    @GetMapping("/findId")
    public String findIdForm() {
        return "member/findId";
    }

    @PostMapping("/findId")
    public String searchId(@ModelAttribute SearchIdAndPassDTO searchIdAndPassDTO, RedirectAttributes redirectAttributes) {
        searchIdAndPassDTO.makeEmail(); // 이메일 완성
        Members members = memberService.searchId(searchIdAndPassDTO);
        if (members != null) {
            redirectAttributes.addFlashAttribute("members", members); // members 객체 redirect
            return "redirect:/member/findIdResult";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }
    }

    @GetMapping("/findIdResult")
    public String findIdResult(@ModelAttribute("members") Members members) {
        return "member/findIdResult";
    }

    /**
     * 비밀번호 찾기
     */
    @GetMapping("/findPw")
    public String findPwForm() {
        return "member/findPw";
    }

    @PostMapping("/findPw")
    public String searchPass(SearchIdAndPassDTO searchIdAndPassDTO, RedirectAttributes redirectAttributes) {
        searchIdAndPassDTO.makeEmail();
        Members members = memberService.searchId(searchIdAndPassDTO);
        if (members != null) {
            redirectAttributes.addFlashAttribute("members", members);
            return "redirect:/member/findPwResult";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }
    }

    /**
     * 비밀번호 변경
     */
    @GetMapping("/findPwResult")
    public String findPwResult(@ModelAttribute("members") Members members) {
        return "member/findPwResult";
    }

    @ResponseBody
    @PostMapping("/findPwResult")
    public ApiResponse updatePassword(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        searchIdAndPassDTO.setPassword(passwordEncoder.encode(searchIdAndPassDTO.getPassword()));
        int result = memberService.updatePass(searchIdAndPassDTO);
        if (result == 1) {
            return new ApiResponse(PASSWORD_UPDATE_OK, result);
        }
        return new ApiResponse(PASSWORD_UPDATE_NOT_OK, result);
    }
}