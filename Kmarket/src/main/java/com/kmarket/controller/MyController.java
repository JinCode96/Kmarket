package com.kmarket.controller;

import com.kmarket.api.ApiResponse;
import com.kmarket.constant.ApiResponseConst;
import com.kmarket.constant.MemberConst;
import com.kmarket.domain.Review;
import com.kmarket.dto.my.MyOrderHistory;
import com.kmarket.dto.product.MemberPointDTO;
import com.kmarket.security.PrincipalDetails;
import com.kmarket.service.MyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.kmarket.constant.ApiResponseConst.*;
import static com.kmarket.constant.MemberConst.GENERAL_UPPER;
import static com.kmarket.constant.MemberConst.SELLER_UPPER;

/**
 * 마이페이지 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyController {

    private final MyService myService;

    /**
     * 마이페이지 홈 화면
     * 최근 주문 내역 조회
     * 포인트 적립 내역 조회
     * 리뷰 조회
     */
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        String uid = principalDetails.getUsername();

        // 최근 주문 내역 (주문 날짜, 상품 코드, 상품명, 상품 이미지(카테고리1,2, 상품코드), 상품 가격, 주문한 상품 수량, 주문 코드)
        List<MyOrderHistory> orderProducts = myService.selectOrderHistory(uid);

        // 포인트 적립 내역 조회
        List<MemberPointDTO> points = myService.selectMemberPoint(uid);

        // 리뷰 조회
        List<Review> reviews = myService.findByUid(uid);

        model.addAttribute("orderProducts", orderProducts);
        model.addAttribute("member", principalDetails.getMembers());
        model.addAttribute("points", points);
        model.addAttribute("reviews", reviews);
        return "my/home";
    }

    /**
     * 회원 정보 수정
     */
    @GetMapping("/info")
    public String info(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        String email = principalDetails.getMembers().getEmail();
        String emailPrefix = null;
        String emailDomain = null;

        // 일반회원일 때만 이메일 있음
        if (email != null) {
            String[] parts = email.split("@");
            emailPrefix = parts[0];
            emailDomain = parts[1];
        }

        String findPhoneNumber = principalDetails.getMembers().getPhoneNumber();
        String phoneNumber = null;
        // 일반회원과 판매 회원만 번호가 있음
        if (findPhoneNumber != null) {
            phoneNumber = findPhoneNumber.replaceAll("-", "");
        }
        log.info("phoneNumber={}", phoneNumber);

        model.addAttribute("emailPrefix", emailPrefix);
        model.addAttribute("emailDomain", emailDomain);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("member", principalDetails.getMembers());
        return "my/info";
    }

    /**
     * 리뷰 저장
     */
    @ResponseBody
    @PostMapping("/saveReview")
    public ApiResponse saveReview(@RequestBody Review review, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("리뷰 저장...");

        // 리뷰 세팅
        review.setUid(principalDetails.getUsername());
        review.setRegistrationDate(LocalDateTime.now());
        log.info("review={}", review);

        Review savedReview = myService.saveReview(review);
        if (savedReview != null) {
            return new ApiResponse(SUCCESS);
        } else {
            return new ApiResponse(FAIL);
        }
    }

    /**
     * 이메일 수정 PUT
     */
    @ResponseBody
    @PutMapping("/updateEmail")
    public ApiResponse updateEmail(@RequestBody Map<String, String> map, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("이메일 수정...");

        String email = map.get("email");
        int result = myService.updateEmail(email, principalDetails.getUsername());

        if (result == 1) {
            return new ApiResponse(SUCCESS);
        } else {
            return new ApiResponse(FAIL);
        }
    }

    /**
     * 휴대폰 수정
     */
    @ResponseBody
    @PutMapping("/updatePhoneNumber")
    public ApiResponse updatePhoneNumber(@RequestBody Map<String, String> map, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("휴대폰 번호 수정...");
        String phoneNumber = map.get("phoneNumber");

        int result = 0;

        if (principalDetails.getMembers().getType().equals(GENERAL_UPPER)) {
            result = myService.updatePhoneNumberGeneral(phoneNumber, principalDetails.getUsername());
        } else if (principalDetails.getMembers().getType().equals(SELLER_UPPER)) {
            result = myService.updatePhoneNumberSeller(phoneNumber, principalDetails.getUsername());
        }

        if (result == 1) {
            return new ApiResponse(SUCCESS);
        } else if (result == 300) {
            return new ApiResponse(300); // 이미 같은 전화번호가 있을 때
        } else {
            return new ApiResponse(FAIL); // 수정 실패
        }
    }

    /**
     * 주소 수정 PUT
     */
    @ResponseBody
    @PutMapping("/updateAddress")
    public ApiResponse updateAddress(@RequestBody Map<String, String> map, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("주소 수정...");
        map.put("loginId", principalDetails.getUsername());
        int result = 0;

        if (principalDetails.getMembers().getType().equals(GENERAL_UPPER)) {
            result = myService.updateAddressGeneral(map);
        } else if (principalDetails.getMembers().getType().equals(SELLER_UPPER)) {
            result = myService.updateAddressSeller(map);
        }

        if (result == 1) {
            return new ApiResponse(SUCCESS);
        } else {
            return new ApiResponse(FAIL);
        }
    }
}
