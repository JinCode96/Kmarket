package com.kmarket.dto.member;

import lombok.Data;

/**
 * 회원가입 이메일 중복 체크 전용 dto
 */
@Data
public class EmailDTO {
    private String email;
}
