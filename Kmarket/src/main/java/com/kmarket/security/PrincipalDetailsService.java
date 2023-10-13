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
        log.info("loginProcess...");
        log.info("username={}", username);
        UserDTO user = memberRepository.findById(username).get();
        log.info("user={}", user);

        Members member = null;
        if (user != null) {
            log.info("1");
            if (user.getType().equals("GENERAL")) {
                member = memberRepository.findByIdGeneral(username).get();
                log.info("member={}", member);
            } else if (user.getType().equals("SELLER")) {
                member = memberRepository.findByIdSeller(username).get();
                log.info("member={}", member);
            }
        } else {
            return null;
        }
        return new PrincipalDetails(member);
    }
}
