package com.kmarket.repository.member;

import com.kmarket.domain.Members;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    int saveGeneral(Members member);

    void saveSeller(Members member);

    int checkGeneralLoginId(String loginId);

    int checkEmail(String email);
}
