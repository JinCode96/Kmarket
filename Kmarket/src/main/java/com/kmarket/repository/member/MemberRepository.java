package com.kmarket.repository.member;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.UserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface MemberRepository {

    int saveGeneral(Members member);

    int saveSeller(Members member);

    int saveCommonUser(String loginId, String type);

    int checkGeneralLoginId(String loginId);

    int checkSellerLoginId(String loginId);

    int checkEmail(String email);

    Optional<UserDTO> findById(String loginId);

    Optional<Members> findByIdGeneral(String loginId);

    Optional<Members> findByIdSeller(String loginId);

    //    void update();
}
