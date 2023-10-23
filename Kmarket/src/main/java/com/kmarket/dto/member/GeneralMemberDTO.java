package com.kmarket.dto.member;

import com.kmarket.constant.MemberConst;
import com.kmarket.domain.Members;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.kmarket.constant.MemberConst.*;

/**
 * 일반 회원 폼 
 * controller 로 받는 역할 수행
 * validation 검증
 * password encode
 */
@Data
public class GeneralMemberDTO {

    // 일반 회원 필드
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
    @Pattern(regexp = "0[0-9]{1,2}-[0-9]{3,4}-[0-9]{4}")
    private String phoneNumber;
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$") // 이메일 정규식
    private String email;
    @NotBlank
    private String zipCode;
    @NotBlank
    private String address;
    @NotBlank
    private String detailAddress;

    /**
     * MemberDTO 로 변환
     */
    public Members generalDTOToDomain() {
        return new Members().builder()
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .zipCode(this.zipCode)
                .address(this.address)
                .detailAddress(this.detailAddress)
                .type("GENERAL")
                .build();
    }
}
