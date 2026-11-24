package io.github.aref9571.maks.auth;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id, String email, String fullName, String role, Instant createdAt) {}
