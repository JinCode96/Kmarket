package com.kmarket.security;

import com.kmarket.domain.Members;
import com.kmarket.dto.member.UserDTO;
import com.kmarket.repository.member.MemberRepository;
import com.kmarket.security.provider.*;
import groovyjarjarpicocli.CommandLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * 카카오, 네이버 등 로그인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("oAuth2...");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, facebook ...

        if (provider.equals("google")) {
            log.info("구글 로그인 요청...");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("facebook")) {
            log.info("페이스북 로그인 요청...");
            oAuth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            log.info("네이버 로그인 요청...");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else if (provider.equals("kakao")) {
            log.info("카카오 로그인 요청...");
            oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String loginId = provider + "_" + providerId; // username 충돌 방지
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        String password = bCryptPasswordEncoder.encode("비밀번호"); // 크게 의미는 없는 로직

        // 회원 있는지 확인
        Optional<Members> optionalMembers = memberRepository.findByIdGeneral(loginId);
        Members member = null;

        if (optionalMembers.isEmpty()) { // 신규 회원가입 진행
            member = Members.builder()
                    .loginId(loginId)
                    .password(password)
                    .name(name)
                    .type("GENERAL")
                    .email(email)
                    .providerId(providerId)
                    .provider(provider)
                    .build();
            int result = memberRepository.saveGeneral(member);
            if (result == 1) {
                memberRepository.saveCommonUser(loginId, "GENERAL");
            }
        } else { // 이미 가입된 회원일 때
            member = optionalMembers.get();
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes()); // PrincipalDetails 반환
    }
}
