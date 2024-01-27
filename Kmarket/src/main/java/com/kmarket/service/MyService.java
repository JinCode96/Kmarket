package com.kmarket.service;

import com.kmarket.domain.Review;
import com.kmarket.dto.my.MyOrderHistory;
import com.kmarket.dto.product.MemberPointDTO;
import com.kmarket.repository.my.MyRepository;
import com.kmarket.repository.product.JpaReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyService {
    private final MyRepository myRepository;
    private final JpaReviewRepository jpaReviewRepository;

    public List<MyOrderHistory> selectOrderHistory(String uid) {
        return myRepository.selectOrderHistory(uid);
    }

    public Review saveReview(Review review) {
        return jpaReviewRepository.save(review);
    }

    public List<Review> findByUid(String uid) {
        return jpaReviewRepository.findTop10ByUidOrderByRegistrationDateDesc(uid);
    }

    public List<MemberPointDTO> selectMemberPoint(String uid) {
        return myRepository.selectMemberPoint(uid);
    }

    public int updateEmail(String email, String loginId) {
        return myRepository.updateEmail(email, loginId);
    }

    /**
     * 일반회원 휴대폰번호 수정
     */
    public int updatePhoneNumberGeneral(String phoneNumber, String loginId) {
        int result = myRepository.findSamePhoneNumberGeneral(phoneNumber, loginId);
        if (result == 0) {
            return myRepository.updatePhoneNumberGeneral(phoneNumber, loginId);
        } else {
            return 300;
        }
    }

    /**
     * 판매자회원 휴대폰번호 수정
     */
    public int updatePhoneNumberSeller(String phoneNumber, String loginId) {
        int result = myRepository.findSamePhoneNumberSeller(phoneNumber, loginId);
        if (result == 0) {
            return myRepository.updatePhoneNumberSeller(phoneNumber, loginId);
        } else {
            return 300;
        }
    }

    public int updateAddressGeneral(Map<String, String> map) {
        return myRepository.updateAddressGeneral(map);
    }
    public int updateAddressSeller(Map<String, String> map) {
        return myRepository.updateAddressSeller(map);
    }
}
