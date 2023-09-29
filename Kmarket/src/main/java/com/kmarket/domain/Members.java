package com.kmarket.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Members {

    // 일반 회원 필드
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private Integer type; // 구분 (0:탈퇴, 1:일반회원, 2:판매회원, 3:관리자)
    private Integer point; // 누적 포인트 (default : 0)
    private Integer level; // 등급 (1:일반, 2:실버, 3:골드) (default : 1)
    private String zipCode; // 우편번호
    private String address;
    private String detailAddress;
    private String withdrawalDate; // 회원 탈퇴 날짜
    private LocalDateTime registrationDate; // 회원 등록 날짜

    // 판매자 필드
    private String company; // 회사명
    private String ceo; // 대표 이름
    private String companyRegistrationNumber; // 사업자 등록 번호
    private String reportNumber; // 통신 판매 신고 번호
    private String managerName; // 담당자 이름
    private String managerPhoneNumber;
    private String faxNumber;

    // 추가 필드 //
    private String emailPrefix;
    private String emailDomain;
    private Integer areaNumber; // 전화번호
    private Integer middleNumber;
    private Integer lastNumber;
    private String confirmPass; // 비밀번호 확인

}
