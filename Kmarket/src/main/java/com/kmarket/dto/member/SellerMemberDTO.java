package com.kmarket.dto.member;

import com.kmarket.constant.MemberConst;
import com.kmarket.domain.Members;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.kmarket.constant.MemberConst.*;

/**
 * 판매자 회원 폼
 * controller 로 받는 역할만 수행
 */
@Data
public class SellerMemberDTO {

    // 판매자 필드
    @NotBlank
    @Pattern(regexp = "^[a-z][a-z0-9]{5,12}$") // 영문자 or 영문자 + 숫자 (6~13자리)
    private String loginId;
    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{7,15}") // 영문 대 소문자 + 숫자 + 특수문자 (8~16자리)
    private String password;
    @NotBlank
    @Pattern(regexp = "^(?:[가-힣\\s]{2,20}|[a-zA-Z\\s]{2,20})$") // 한글 or 영문 띄어쓰기, 혼용 안됨. (2~20자리)
    private String name;
    @NotBlank
    private String company; // 회사명
    @NotBlank
    @Pattern(regexp = "^(?:[가-힣\\s]{2,20}|[a-zA-Z\\s]{2,20})$") // 한글 or 영문 띄어쓰기, 혼용 안됨. (2~20자리)
    private String ceo; // 대표 이름
    @NotBlank
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$")
    private String businessRegistrationNumber; // 사업자 등록 번호
    @NotBlank
    @Pattern(regexp = "^(20\\d{2}|19\\d{2})-[가-힣]{2,7}-\\d{4,5}$")
    private String reportNumber; // 통신 판매 신고 번호
    @NotBlank
    @Pattern(regexp = "0[0-9]{1,2}-[0-9]{3,4}-[0-9]{4}")
    private String phoneNumber;
    @NotBlank
    private String zipCode; // 우편번호
    @NotBlank
    private String address;
    @NotBlank
    private String detailAddress;

    public Members generalDTOToDomain() {
        return new Members().builder()
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .company(this.company)
                .ceo(this.ceo)
                .businessRegistrationNumber(this.businessRegistrationNumber)
                .reportNumber(this.reportNumber)
                .phoneNumber(this.phoneNumber)
                .zipCode(this.zipCode)
                .address(this.address)
                .detailAddress(this.detailAddress)
                .type(SELLER_UPPER)
                .build();
    }
}
