package com.kmarket.dto.member;

import lombok.Data;

/**
 * 이용 약관 검증
 */
@Data
public class TermsDTO {
    private boolean termsTerms;
    private boolean termsFinance;
    private boolean termsPrivacy;
    private boolean termsLocation;
}
