package com.kmarket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalDetailsService principalDetailsService;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 자원에 대한 접근 설정
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // h2 db 콘솔 접근 권한 설정 (개발 및 테스트 목적, 나중엔 빼야함)
                        .requestMatchers(antMatcher("/admin/**")).hasAnyRole("SELLER", "ADMIN") // 권한 필요
                        .anyRequest().permitAll() // 그 외 다른 요청 모두 통과
                )
                .formLogin(form -> form
                        .loginPage("/member/login").permitAll() // 기본 로그인 화면 설정
                        .loginProcessingUrl("/member/login") // 해당 url 로 post 요청
                        .failureUrl("/member/login?failCheck=true")
                        .defaultSuccessUrl("/") // 시큐리티는 로그인 후 자동으로 가려던 웹 페이지로 이동시켜준다.
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
                )
                .rememberMe((remember) -> remember
                        .userDetailsService(principalDetailsService)
                )
                .oauth2Login(cust -> cust
                        .loginPage("/member/login") // OAuth2 로그인 페이지 설정
                        .userInfoEndpoint(oauth -> oauth.userService(principalOauth2UserService)) // 로그인 완료 후처리 필요. tip. 코드 X (엑세스 토큰 + 사용자 프로필 정보 O)
                );

        return http.build();
    }

}
