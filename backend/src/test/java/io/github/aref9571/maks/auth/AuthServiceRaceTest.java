package io.github.aref9571.maks.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.github.aref9571.maks.common.error.ApiException;
import io.github.aref9571.maks.common.error.ErrorCode;
import io.github.aref9571.maks.user.User;
import io.github.aref9571.maks.user.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceRaceTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private TokenService tokenService;

    @Test
    void returnsConflictWhenUniqueConstraintFailsAfterTheEmailCheck() {
        AuthService authService =
                new AuthService(userRepository, passwordEncoder, CLOCK, tokenService);
        when(userRepository.existsByEmail("anna@example.com")).thenReturn(false);
        when(passwordEncoder.encode("correct horse battery")).thenReturn("hash");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("users_email_uq"));
        RegisterRequest request =
                new RegisterRequest("anna@example.com", "Anna Smith", "correct horse battery");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception ->
                                assertThat(exception.errorCode())
                                        .isEqualTo(ErrorCode.EMAIL_ALREADY_REGISTERED));
    }
}
