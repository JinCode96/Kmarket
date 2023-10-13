package com.kmarket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 자원에 대한 접근 설정
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // h2 db 콘솔 접근 권한 설정 (개발 및 테스트 목적, 나중엔 빼야함)
                        .requestMatchers(antMatcher("/admin/**"), antMatcher("/my/**")).authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/member/loginForm").permitAll() // 기본 로그인 화면 설정
                        .loginProcessingUrl("/member/loginForm") // 해당 url 로 post 요청
                        .defaultSuccessUrl("/") // 시큐리티는 로그인 후 자동으로 가려던 웹 페이지로 이동시켜준다.
                );

                return http.build();
    }
}