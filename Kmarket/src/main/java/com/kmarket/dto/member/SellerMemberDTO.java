package com.kmarket.dto.member;

import lombok.Data;

/**
 * 판매자 회원 폼
 * controller 로 받는 역할만 수행
 */
@Data
public class SellerMemberDTO {

    // 판매자 필드
    private String loginId;
    private String password;
    private String company; // 회사명
    private String ceo; // 대표 이름
    private String companyRegistrationNumber; // 사업자 등록 번호
    private String reportNumber; // 통신 판매 신고 번호
    private String phoneNumber;
    private String faxNumber;
    private String zipCode; // 우편번호
    private String address;
    private String detailAddress;
    private String managerName; // 담당자 이름
    private String managerPhoneNumber;

    // 추가 필드
    private Integer areaNumber; // 전화번호
    private Integer middleNumber;
    private Integer lastNumber;

    private String confirmPass; // 비밀번호 확인

    // 번호 완성
    public void makePhoneNumber() {
        if (areaNumber != null && middleNumber != null && lastNumber != null) {
            this.phoneNumber = areaNumber + "-" + middleNumber + "-" + lastNumber;
        } else {
            this.phoneNumber = null;
        }
    }
}
