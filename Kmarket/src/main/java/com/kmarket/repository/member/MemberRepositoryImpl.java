package com.kmarket.repository.member;

import com.kmarket.domain.Members;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{

    private final MemberMapper memberMapper;

    @Override
    public int saveGeneral(Members member) {
        return memberMapper.saveGeneral(member);
    }

    @Override
    public Members saveSeller(Members member) {
        memberMapper.saveSeller(member);
        return member;
    }

    @Override
    public int checkGeneralLoginId(String loginId) {
        return memberMapper.checkGeneralLoginId(loginId);
    }

    @Override
    public int checkEmail(String email) {
        return memberMapper.checkEmail(email);
    }

}
