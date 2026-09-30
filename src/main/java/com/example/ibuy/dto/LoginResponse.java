package com.example.ibuy.dto;

public record LoginResponse (String token, String refreshToken) {
    public LoginResponse (String token) {
        this(token, null);
    }
}
