package com.kmarket.api;

import lombok.*;

/**
 * http api json 통신을 위한 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private int status;
}
