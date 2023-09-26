package com.kmarket.service;

import com.kmarket.entity.TermsEntity;
import com.kmarket.repository.member.TermsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final TermsJpaRepository termsJpaRepository;

    public Optional<TermsEntity> getTerms() {
        return termsJpaRepository.findById(1);
    }

}
