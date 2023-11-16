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


@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("username={}", username);

        UserDTO user = memberRepository.findById(username).orElse(null);
        Members member = null;

        if (user != null) {
            String userType = user.getType();

            if ("GENERAL".equals(userType)) {
                member = memberRepository.findByIdGeneral(username).orElse(null);
            } else if ("SELLER".equals(userType)) {
                member = memberRepository.findByIdSeller(username).orElse(null);
            }
            if (member != null) {
//                log.info("member={}", member);
                return new PrincipalDetails(member);
            }
        }
//        log.info("회원 없음!!!");
        throw new UsernameNotFoundException(username);
    }
}
