package io.github.aref9571.maks.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email, @NotBlank String password) {
    public LoginRequest {
        if (email != null) {
            email = email.trim();
        }
    }
}
