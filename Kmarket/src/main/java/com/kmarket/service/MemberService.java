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

    public int saveGeneralMember(Members member) {
        int result = memberRepository.saveGeneral(member);
        if (result == 1) {
            memberRepository.saveCommonUser(member.getLoginId(), member.getType());
        }
        return result;
    }

    public int saveSellerMember(Members member) {
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

    public Optional<Terms> getTerms() {
        return termsJpaRepository.findById(1);
    }

    public int checkMemberNameAndEmail(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberRepository.checkMemberNameAndEmail(searchIdAndPassDTO);
    }

    public Members searchId(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberRepository.searchId(searchIdAndPassDTO);
    }

    public int updatePass(SearchIdAndPassDTO searchIdAndPassDTO){
        return memberRepository.updatePass(searchIdAndPassDTO);
    }

}
