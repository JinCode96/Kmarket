package com.kmarket.service;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.SearchIdAndPassDTO;
import com.kmarket.domain.Terms;
import com.kmarket.repository.member.MemberRepository;
import com.kmarket.repository.member.TermsJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 일반 회원 생성
     */
    public int saveGeneralMember(Members member) {
        int result = memberRepository.saveGeneral(member);
        if (result == 1) {
            memberRepository.saveCommonUser(member.getLoginId(), member.getType());
        }
        return result;
    }

    /**
     * 판매자 회원 생성
     */
    public int saveSellerMember(Members member) {
        int result = memberRepository.saveSeller(member);
        if (result == 1) {
            memberRepository.saveCommonUser(member.getLoginId(), member.getType());
        }
        return result;
    }

    /**
     * 아이디 중복 체크
     * 일반, 판매자 구분하여 체크
     */
    public int checkLoginId(String loginId) {
        int generalResult = memberRepository.checkGeneralLoginId(loginId);
        int sellerResult = memberRepository.checkSellerLoginId(loginId);
        if (generalResult == 1 || sellerResult == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 이메일 존재 여부 체크
     */
    public int checkEmail(String email) {
        return memberRepository.checkEmail(email);
    }

    /**
     * 이용약관 가져오기
     */
    public Optional<Terms> getTerms() {
        return termsJpaRepository.findById(1);
    }

    /**
     * 회원 존재 여부 체크
     */
    public int checkMemberNameAndEmail(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberRepository.checkMemberNameAndEmail(searchIdAndPassDTO);
    }

    /**
     * 회원 찾기
     */
    public Members searchId(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberRepository.searchId(searchIdAndPassDTO);
    }

    /**
     * 비밀번호 변경
     */
    public int updatePass(SearchIdAndPassDTO searchIdAndPassDTO){
        return memberRepository.updatePass(searchIdAndPassDTO);
    }

}
