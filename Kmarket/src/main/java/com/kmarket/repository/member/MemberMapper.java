package com.kmarket.repository.member;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    int saveGeneral(Members member);

    int saveSeller(Members member);

    int saveCommonUser(@Param("loginId") String loginId, @Param("type") String type);

    int checkGeneralLoginId(String loginId);

    int checkSellerLoginId(String loginId);

    int checkEmail(String email);

    Optional<UserDTO> findById(@Param("loginId") String loginId);

    Optional<Members> findByIdGeneral(@Param("loginId") String loginId);
    Optional<Members> findByIdSeller(@Param("loginId") String loginId);
}
