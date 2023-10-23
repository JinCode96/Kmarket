package com.kmarket.dto.member;

import jakarta.persistence.Column;
import lombok.Data;

/**
 * 회원 아이디와 타입만으로 구성됨.
 */
@Data
public class UserDTO {
    private String loginId;
    private String type;
}
