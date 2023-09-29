package com.kmarket.service;

import com.kmarket.domain.Members;
import com.kmarket.entity.TermsEntity;
import com.kmarket.repository.member.MemberRepository;
import com.kmarket.repository.member.TermsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final TermsJpaRepository termsJpaRepository;

    public int saveGeneralMember(Members member) {
        return memberRepository.saveGeneral(member);
    }

    public Members saveSellerMember(Members member) {
        return memberRepository.saveSeller(member);
    }

    public int checkGeneralLoginId(String loginId) {
        return memberRepository.checkGeneralLoginId(loginId);
    }

    public int checkEmail(String email) {
        return memberRepository.checkEmail(email);
    }

    public Optional<TermsEntity> getTerms() {
        return termsJpaRepository.findById(1);
    }



}
