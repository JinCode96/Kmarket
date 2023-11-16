package com.kmarket.repository.member;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.SearchIdAndPassDTO;
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
    Optional<UserDTO> findById(String loginId);
    Optional<Members> findByIdGeneral(String loginId);
    Optional<Members> findByIdSeller(String loginId);
    int checkMemberNameAndEmail(SearchIdAndPassDTO searchIdAndPassDTO);
    Members searchId(SearchIdAndPassDTO searchIdAndPassDTO);

    int updatePass(SearchIdAndPassDTO searchIdAndPassDTO);
}
