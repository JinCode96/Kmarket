package com.kmarket.dto.member;

import lombok.Data;

/**
 * 아이디 찾기, 비밀번호 찾기에 사용되는 dto
 */
@Data
public class SearchIdAndPassDTO {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String authCode;

    private String emailPrefix;
    private String emailDomain;

    public void makeEmail() {
        this.email = this.emailPrefix + "@" + this.emailDomain;
    }
}
