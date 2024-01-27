package com.kmarket.repository.my;

import com.kmarket.dto.my.MyOrderHistory;
import com.kmarket.dto.product.MemberPointDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MyMapper {
    List<MyOrderHistory> selectOrderHistory(String uid);

    List<MemberPointDTO> selectMemberPoint(String uid);

    int updateEmail(@Param("email") String email, @Param("loginId") String loginId);
    int findSamePhoneNumberGeneral(@Param("phoneNumber") String phoneNumber, @Param("loginId") String loginId);
    int updatePhoneNumberGeneral(@Param("phoneNumber") String phoneNumber, @Param("loginId") String loginId);

    int findSamePhoneNumberSeller(@Param("phoneNumber") String phoneNumber, @Param("loginId") String loginId);
    int updatePhoneNumberSeller(@Param("phoneNumber") String phoneNumber, @Param("loginId") String loginId);

    int updateAddressGeneral(Map<String, String> map);
    int updateAddressSeller(Map<String, String> map);

}
