package com.kmarket.security;

import com.kmarket.constant.MemberConst;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static com.kmarket.constant.MemberConst.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

/**
 * Security 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalDetailsService principalDetailsService;
    private final PrincipalOauth2UserService principalOauth2UserService; // 카카오, 네이버, 페이스북, 구글 로그인

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 자원에 대한 접근 설정
//                        .requestMatchers(PathRequest.toH2Console()).permitAll() // h2 db 콘솔 접근 권한 설정 (개발 및 테스트 목적, 나중엔 빼야함)
                        .requestMatchers(antMatcher("/admin/**")).hasAnyRole(SELLER_UPPER, ADMIN_UPPER) // 권한 필요
                        .requestMatchers(
                                antMatcher("/products/directOrder"), antMatcher("/products/cart"), antMatcher("/products/cartOrder"), antMatcher("/my/**")
                        ).authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/members/login").permitAll() // 기본 로그인 화면 설정
                        .loginProcessingUrl("/members/login") // 해당 url 로 post 요청
                        .failureUrl("/members/login?failCheck=true")
                        .defaultSuccessUrl("/")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
                )
                .rememberMe((remember) -> remember
                        .userDetailsService(principalDetailsService)
                )
                .oauth2Login(cust -> cust
                        .loginPage("/members/login") // OAuth2 로그인 페이지 설정
                        .userInfoEndpoint(oauth -> oauth.userService(principalOauth2UserService)) // 로그인 완료 후처리 필요.
                );

        return http.build();
    }

}
