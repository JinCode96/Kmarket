package com.kmarket.security;

import com.kmarket.domain.Members;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private Members members;
    private Map<String, Object> attributes; // oauth 유저

    public PrincipalDetails(Members members) {
        this.members = members;
    }
    public PrincipalDetails(Members members, Map<String, Object> attributes) {
        this.members = members;
        this.attributes = attributes;
    }

    /**
     * @return 계정의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new SimpleGrantedAuthority("ROLE_" + members.getType()));
        return collect;
    }

    /**
     * @return 계정의 비밀번호
     */
    @Override
    public String getPassword() {
        return members.getPassword();
    }

    /**
     * @return 계정의 고유한 값 (PK)
     */
    @Override
    public String getUsername() {
        return members.getLoginId();
    }

    /**
     * @return 계정의 만료 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료 안됨.
    }

    /**
     * @return 계정의 잠김 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠기지 않음
    }

    /**
     * @return 계정의 비밀번호 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 만료 안됨.
    }

    /**
     * @return 계정의 활성화 여부
     */
    @Override
    public boolean isEnabled() {
        // 1년이 지나면 휴면 계정으로 return false 할 수 있다.
        return true; // 활성화
    }

    // OAuth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2User
    @Override
    public String getName() {
        return null;
    }
}
