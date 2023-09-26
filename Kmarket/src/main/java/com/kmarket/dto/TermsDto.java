package com.kmarket.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 이용 약관 검증 처리
 */
@Data
public class TermsDto {

    private boolean termsTerms;
    private boolean termsFinance;
    private boolean termsPrivacy;
    private boolean termsLocation;
}
