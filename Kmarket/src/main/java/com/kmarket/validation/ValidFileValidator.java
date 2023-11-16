package com.kmarket.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * MultipartFile 에 대해서 ValidFile 어노테이션의 제약을 준수했는지 체크
 */
public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file != null && !file.isEmpty();
    }
}
