package com.kmarket.repository.my;

import com.kmarket.dto.my.MyOrderHistory;
import com.kmarket.dto.product.MemberPointDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MyRepositoryImpl implements MyRepository {
    private final MyMapper myMapper;

    public List<MyOrderHistory> selectOrderHistory(String uid) {
        return myMapper.selectOrderHistory(uid);
    }

    @Override
    public List<MemberPointDTO> selectMemberPoint(String uid) {
        return myMapper.selectMemberPoint(uid);
    }

    @Override
    public int updateEmail(String email, String loginId) {
        return myMapper.updateEmail(email, loginId);
    }

    @Override
    public int findSamePhoneNumberGeneral(String phoneNumber, String loginId) {
        return myMapper.findSamePhoneNumberGeneral(phoneNumber, loginId);
    }

    @Override
    public int updatePhoneNumberGeneral(String phoneNumber, String loginId) {
        return myMapper.updatePhoneNumberGeneral(phoneNumber, loginId);
    }

    @Override
    public int findSamePhoneNumberSeller(String phoneNumber, String loginId) {
        return myMapper.findSamePhoneNumberSeller(phoneNumber, loginId);
    }

    @Override
    public int updatePhoneNumberSeller(String phoneNumber, String loginId) {
        return myMapper.updatePhoneNumberSeller(phoneNumber, loginId);
    }

    @Override
    public int updateAddressGeneral(Map<String, String> map) {
        return myMapper.updateAddressGeneral(map);
    }

    @Override
    public int updateAddressSeller(Map<String, String> map) {
        return myMapper.updateAddressSeller(map);
    }
}
