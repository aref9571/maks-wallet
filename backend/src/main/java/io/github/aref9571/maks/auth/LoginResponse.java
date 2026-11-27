package io.github.aref9571.maks.auth;

public record LoginResponse(
        String accessToken, String tokenType, long expiresIn, UserResponse user) {}
