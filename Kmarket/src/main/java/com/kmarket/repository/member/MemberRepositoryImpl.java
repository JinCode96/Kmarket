package com.kmarket.repository.member;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.SearchIdAndPassDTO;
import com.kmarket.dto.member.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{

    private final MemberMapper memberMapper;

    @Override
    public int saveGeneral(Members member) {
        return memberMapper.saveGeneral(member);
    }

    @Override
    public int saveSeller(Members member) {
        return memberMapper.saveSeller(member);
    }

    @Override
    public int saveCommonUser(String loginId, String type) {
        return memberMapper.saveCommonUser(loginId, type);
    }

    @Override
    public int checkGeneralLoginId(String loginId) {
        return memberMapper.checkGeneralLoginId(loginId);
    }

    @Override
    public int checkSellerLoginId(String loginId) {
        return memberMapper.checkSellerLoginId(loginId);
    }

    @Override
    public int checkEmail(String email) {
        return memberMapper.checkEmail(email);
    }

    @Override
    public Optional<UserDTO> findById(String loginId) {
        return memberMapper.findById(loginId);
    }

    @Override
    public Optional<Members> findByIdGeneral(String loginId) {
        return memberMapper.findByIdGeneral(loginId);
    }

    @Override
    public Optional<Members> findByIdSeller(String loginId) {
        return memberMapper.findByIdSeller(loginId);
    }

    @Override
    public int checkMemberNameAndEmail(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberMapper.checkMemberNameAndEmail(searchIdAndPassDTO);
    }

    @Override
    public Members searchId(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberMapper.searchId(searchIdAndPassDTO);
    }

    @Override
    public int updatePass(SearchIdAndPassDTO searchIdAndPassDTO) {
        return memberMapper.updatePass(searchIdAndPassDTO);
    }


}
