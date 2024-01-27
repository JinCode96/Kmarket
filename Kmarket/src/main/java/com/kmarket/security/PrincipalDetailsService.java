package com.kmarket.security;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.UserDTO;
import com.kmarket.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.kmarket.constant.MemberConst.*;

/**
 * 실제 로그인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("로그인 요청...");

        UserDTO user = memberRepository.findById(username).orElse(null);
        Members member = null;

        if (user != null) {
            String userType = user.getType();

            if (GENERAL_UPPER.equals(userType)) {
                member = memberRepository.findByIdGeneral(username).orElse(null);
            } else if (SELLER_UPPER.equals(userType)) {
                member = memberRepository.findByIdSeller(username).orElse(null);
            }
            if (member != null) {
                return new PrincipalDetails(member);
            }
        }
        log.info("회원 없음...");
        throw new UsernameNotFoundException(username);
    }
}
