package com.example.ibuy.dto;

public record LoginResponse (String token, String type) {
    public LoginResponse (String token) {
        this(token, "Bearer");
    }
}
