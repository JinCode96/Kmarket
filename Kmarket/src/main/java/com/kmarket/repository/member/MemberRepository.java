package com.kmarket.repository.member;

import com.kmarket.domain.Members;

public interface MemberRepository {

    int saveGeneral(Members member);

    Members saveSeller(Members member);

    int checkGeneralLoginId(String loginId);

    int checkEmail(String email);

//    void update();
//    Optional<MemberDto> findById(String loginId);
}
