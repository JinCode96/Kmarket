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
import java.util.Map;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.*;

/**
 * MemberController
 * 회원 가입
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
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
     * 일반 회원, 사업자 회원 구분 화면
     */
    @GetMapping("/join")
    public String join() {
        return "member/join";
    }

    /**
     * 이용 약관 화면
     * @PathVariable 사용
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
     * 약관 체크박스 검증
     */
    @ResponseBody
    @PostMapping("/sign-up/{type}")
    public ApiResponse termsValid(@PathVariable String type, @RequestBody TermsDTO terms) {
        log.info("이용 약관 검증...");
        if (!terms.isTermsTerms() || !terms.isTermsFinance() || !terms.isTermsPrivacy()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        } else {
            return new ApiResponse(TERMS_OK, SUCCESS);
        }
    }

    /**
     * 회원가입 화면
     */
    @GetMapping("/add/{type}")
    public String registerForm(@PathVariable String type, Model model) {
        model.addAttribute("member", new Members()); // th:object 사용을 위해서
        if (type.equals(GENERAL_LOWER)) {
            return "member/registerGeneral";
        } else if (type.equals(SELLER_LOWER)) {
            return "member/registerSeller";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND); // 404
    }

    /**
     * 일반 회원가입 post
     * @Validated 사용
     */
    @ResponseBody
    @PostMapping("/add/general")
    public ApiResponse saveGeneral(@Validated @RequestBody GeneralMemberDTO generalMemberDTO, BindingResult bindingResult) {
        log.info("일반 회원 가입...");
        if (bindingResult.hasErrors()) {
            return new ApiResponse(BAD_REQUEST, FAIL);
        }
        generalMemberDTO.setPassword(passwordEncoder.encode(generalMemberDTO.getPassword())); // 패스워드 암호화
        int result = memberService.saveGeneralMember(generalMemberDTO.generalDTOToDomain()); // 일반 회원 저장
        if (result == 1) {
            return new ApiResponse(REGISTER_OK, result);
        }
        return new ApiResponse(REGISTER_NOT_OK, result);
    }

    /**
     * 판매자 회원가입 post
     * @Validated 사용
     */
    @ResponseBody
    @PostMapping("/add/seller")
    public ApiResponse saveSeller(@Validated @RequestBody SellerMemberDTO sellerMemberDTO, BindingResult bindingResult) {
        log.info("판매자 회원 가입...");
        if (bindingResult.hasErrors()) {
            return new ApiResponse(BAD_REQUEST, FAIL);
        }
        sellerMemberDTO.setPassword(passwordEncoder.encode(sellerMemberDTO.getPassword())); // 패스워드 암호화 
        int result = memberService.saveSellerMember(sellerMemberDTO.generalDTOToDomain()); // 판매자 회원 저장
        if (result == 1) {
            return new ApiResponse(REGISTER_OK, result);
        }
        return new ApiResponse(REGISTER_NOT_OK, result);
    }

    /**
     * 아이디 중복 검사
     */
    @ResponseBody
    @PostMapping("/checkIds")
    public ApiResponse checkLoginId(@RequestBody Map<String, String> map) {
        log.info("아이디 중복 체크...");
        int result = memberService.checkLoginId(map.get("loginId"));
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
    @PostMapping("/checkEmails")
    public ApiResponse checkEmail(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        log.info("이메일 중복 검사...");
        int result = memberService.checkEmail(searchIdAndPassDTO.getEmail());
        if (result == 1) {
            return new ApiResponse(EMAIL_NOT_OK, result);
        } else {
            return new ApiResponse(EMAIL_OK, result);
        }
    }

    /**
     * 회원 찾기
     * 이름과 이메일로 회원 존재 여부 찾기
     */
    @ResponseBody
    @PostMapping("/checkMembers")
    public ApiResponse checkMemberNameAndEmail(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        log.info("회원 찾기...");
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
    @PostMapping("/sendMails")
    public ApiResponse mailConfirm(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) throws MessagingException, UnsupportedEncodingException {
        log.info("이메일 코드 전송...");
        emailService.sendEmail(searchIdAndPassDTO.getEmail());
        return new ApiResponse(EMAIL_CODE_OK, 200);
    }

    /**
     * redis 코드 인증
     */
    @ResponseBody
    @PostMapping("/checkCodes")
    public ApiResponse codeConfirm(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        log.info("Redis 이메일 코드 인증...");
        String value = redisUtil.getData(searchIdAndPassDTO.getAuthCode());
        if (value == null) {
            return new ApiResponse(CODE_NOT_OK, 400);
        } else {
            return new ApiResponse(CODE_OK, 200);
        }
    }

    /**
     * 아이디 찾기 화면
     */
    @GetMapping("/findId")
    public String findIdForm() {
        return "member/findId";
    }

    /**
     * 아이디 찾기 Post
     */
    @PostMapping("/findId")
    public String searchId(@ModelAttribute SearchIdAndPassDTO searchIdAndPassDTO, RedirectAttributes redirectAttributes) {
        log.info("아이디 찾기...");
        searchIdAndPassDTO.makeEmail(); // 이메일 완성
        Members members = memberService.searchId(searchIdAndPassDTO);  // 이름과 이메일로 회원 찾기 (Oauth2 가 아닌 회원)
        if (members != null) {
            redirectAttributes.addFlashAttribute("members", members); // members 객체 redirect
            return "redirect:/member/findIdResult";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }
    }

    /**
     * 아이디 찾기 결과 화면
     */
    @GetMapping("/findIdResult")
    public String findIdResult(@ModelAttribute("members") Members members) {
        return "member/findIdResult";
    }

    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping("/findPw")
    public String findPwForm() {
        return "member/findPw";
    }

    /**
     * 비밀번호 찾기
     * 회원 찾기 POST
     */
    @PostMapping("/findPw")
    public String searchPass(SearchIdAndPassDTO searchIdAndPassDTO, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 찾기...");
        searchIdAndPassDTO.makeEmail(); // 이메일 완성
        Members members = memberService.searchId(searchIdAndPassDTO); // 회원 찾기
        if (members != null) {
            redirectAttributes.addFlashAttribute("members", members);
            return "redirect:/member/findPwResult";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST); // 회원이 없다면 404
        }
    }

    /**
     * 비밀번호 변경 화면
     */
    @GetMapping("/resetPw")
    public String findPwResult(@ModelAttribute("members") Members members) {
        return "member/findPwResult";
    }

    /**
     * 회원 비밀번호 변경 PUT
     */
    @ResponseBody
    @PutMapping("/resetPw")
    public ApiResponse updatePassword(@RequestBody SearchIdAndPassDTO searchIdAndPassDTO) {
        log.info("회원 비밀번호 변경...");
        searchIdAndPassDTO.setPassword(passwordEncoder.encode(searchIdAndPassDTO.getPassword()));
        int result = memberService.updatePass(searchIdAndPassDTO);
        if (result == 1) {
            return new ApiResponse(PASSWORD_UPDATE_OK, result);
        }
        return new ApiResponse(PASSWORD_UPDATE_NOT_OK, result);
    }
}