package com.kmarket.repository.my;

import com.kmarket.dto.my.MyOrderHistory;
import com.kmarket.dto.product.MemberPointDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MyRepository {
    List<MyOrderHistory> selectOrderHistory(String uid);
    List<MemberPointDTO> selectMemberPoint(String uid);
    int updateEmail(String email, String loginId);
    int findSamePhoneNumberGeneral(String phoneNumber, String loginId);
    int updatePhoneNumberGeneral(String phoneNumber, String loginId);

    int findSamePhoneNumberSeller(String phoneNumber, String loginId);
    int updatePhoneNumberSeller(String phoneNumber, String loginId);

    int updateAddressGeneral(Map<String, String> map);
    int updateAddressSeller(Map<String, String> map);
}
