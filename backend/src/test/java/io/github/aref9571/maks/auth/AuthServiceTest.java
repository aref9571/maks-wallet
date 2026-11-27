package io.github.aref9571.maks.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.github.aref9571.maks.TestcontainersConfiguration;
import io.github.aref9571.maks.common.error.ApiException;
import io.github.aref9571.maks.common.error.ErrorCode;
import io.github.aref9571.maks.user.User;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, AuthService.class, AuthServiceTest.TestBeans.class})
class AuthServiceTest {

    @TestConfiguration
    static class TestBeans {

        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(4);
        }
    }

    @Autowired private AuthService authService;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private JdbcTemplate jdbcTemplate;

    @MockitoBean private TokenService tokenService;

    @Test
    void storesTrimmedLowerCaseEmailAndBcryptHash() {
        RegisterRequest request =
                new RegisterRequest(" Anna@Example.com ", "Anna Smith", "correct horse battery");

        UserResponse response = authService.register(request);

        String storedHash =
                jdbcTemplate.queryForObject(
                        "select password_hash from users where email = ?",
                        String.class,
                        "anna@example.com");
        assertThat(response.email()).isEqualTo("anna@example.com");
        assertThat(storedHash).isNotEqualTo("correct horse battery");
        assertThat(passwordEncoder.matches("correct horse battery", storedHash)).isTrue();
    }

    @Test
    void rejectsSecondRegistrationWithSameEmailInAnyCase() {
        authService.register(
                new RegisterRequest("anna@example.com", "Anna Smith", "correct horse battery"));
        RegisterRequest duplicate =
                new RegisterRequest(" ANNA@example.com", "Anna Other", "another long password");

        assertThatThrownBy(() -> authService.register(duplicate))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception ->
                                assertThat(exception.errorCode())
                                        .isEqualTo(ErrorCode.EMAIL_ALREADY_REGISTERED));
    }

    @Test
    void logsInWithCorrectPasswordAndReturnsToken() {
        when(tokenService.createAccessToken(any(User.class))).thenReturn("token-value");
        when(tokenService.accessTokenLifeTimeSeconds()).thenReturn(900L);
        authService.register(
                new RegisterRequest("anna@example.com", "Anna Smith", "correct horse battery"));

        LoginResponse response =
                authService.login(new LoginRequest(" Anna@Example.com ", "correct horse battery"));

        assertThat(response.accessToken()).isEqualTo("token-value");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
        assertThat(response.user().email()).isEqualTo("anna@example.com");
    }

    @Test
    void rejectsWrongPasswordWithInvalidCredentials() {
        authService.register(
                new RegisterRequest("anna@example.com", "Anna Smith", "correct horse battery"));

        assertInvalidCredentials(new LoginRequest("anna@example.com", "wrong password here"));
    }

    @Test
    void rejectsUnknownEmailWithTheSameInvalidCredentials() {
        assertInvalidCredentials(new LoginRequest("nobody@example.com", "wrong password here"));
    }

    private void assertInvalidCredentials(LoginRequest request) {
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception ->
                                assertThat(exception.errorCode())
                                        .isEqualTo(ErrorCode.INVALID_CREDENTIALS))
                .hasMessage("Email or password is incorrect.");
    }
}
