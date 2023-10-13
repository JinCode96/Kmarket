package com.kmarket.service;

import com.kmarket.domain.Members;
import com.kmarket.entity.TermsEntity;
import com.kmarket.repository.member.MemberRepository;
import com.kmarket.repository.member.TermsJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final TermsJpaRepository termsJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public int saveGeneralMember(Members member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        int result = memberRepository.saveGeneral(member);
        if (result == 1) {
            memberRepository.saveCommonUser(member.getLoginId(), member.getType());
        }
        return result;
    }

    public int saveSellerMember(Members member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        int result = memberRepository.saveSeller(member);
        if (result == 1) {
            memberRepository.saveCommonUser(member.getLoginId(), member.getType());
        }
        return result;
    }

    public int checkLoginId(String loginId) {
        int generalResult = memberRepository.checkGeneralLoginId(loginId);
        int sellerResult = memberRepository.checkSellerLoginId(loginId);
        if (generalResult == 1 || sellerResult == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public int checkEmail(String email) {
        return memberRepository.checkEmail(email);
    }

    public Optional<TermsEntity> getTerms() {
        return termsJpaRepository.findById(1);
    }



}
