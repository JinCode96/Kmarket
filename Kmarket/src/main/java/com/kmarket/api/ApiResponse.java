package com.kmarket.api;

import lombok.*;

/**
 * http api json 통신을 위한 객체
 */
@Getter
@Setter
@ToString
public class ApiResponse {
    private String message;
    private int status;

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(int status) {
        this.status = status;
    }
}
