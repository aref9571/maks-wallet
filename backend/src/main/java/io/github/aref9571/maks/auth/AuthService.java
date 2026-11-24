package io.github.aref9571.maks.auth;

import io.github.aref9571.maks.common.error.ApiException;
import io.github.aref9571.maks.common.error.ErrorCode;
import io.github.aref9571.maks.user.User;
import io.github.aref9571.maks.user.UserRepository;
import java.time.Clock;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AuthService(
            UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw emailAlreadyRegistered();
        }
        String passwordHash = passwordEncoder.encode(request.password());
        User user = User.register(email, passwordHash, request.fullName().trim(), clock);
        try {
            return toResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw emailAlreadyRegistered();
        }
    }

    private static ApiException emailAlreadyRegistered() {
        return new ApiException(
                ErrorCode.EMAIL_ALREADY_REGISTERED, "An account with this email already exists.");
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(), user.email(), user.fullName(), user.role().name(), user.createdAt());
    }
}
