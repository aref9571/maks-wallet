package io.github.aref9571.maks.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.aref9571.maks.auth.TokenService;
import io.github.aref9571.maks.auth.UserResponse;
import io.github.aref9571.maks.config.JwtConfig;
import io.github.aref9571.maks.config.JwtProperties;
import io.github.aref9571.maks.config.SecurityConfig;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(
        properties = {
            "maks.jwt.secret=" + UserControllerTest.SECRET,
            "maks.jwt.issuer=maks-api",
            "maks.jwt.access-token-lifetime=15m"
        })
class UserControllerTest {

    static final String SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private static final String OTHER_SECRET = "ZmVkY2JhOTg3NjU0MzIxMGZlZGNiYTk4NzY1NDMyMTA=";

    @Autowired private MockMvc mockMvc;

    @Autowired private JwtEncoder jwtEncoder;

    @Autowired private JwtProperties properties;

    @MockitoBean private UserService userService;

    private final User user =
            User.register("anna@example.com", "hash", "Anna Smith", Clock.systemUTC());

    @Test
    void returnsCurrentUserForValidToken() throws Exception {
        when(userService.getById(user.id()))
                .thenReturn(
                        new UserResponse(
                                user.id(),
                                "anna@example.com",
                                "Anna Smith",
                                "USER",
                                Instant.parse("2026-01-01T00:00:00Z")));
        String token = tokenWithClock(jwtEncoder, properties, Clock.systemUTC());

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("anna@example.com"));
    }

    @Test
    void rejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        Clock anHourAgo = Clock.fixed(Instant.now().minus(Duration.ofHours(1)), ZoneOffset.UTC);
        String token = tokenWithClock(jwtEncoder, properties, anHourAgo);

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() throws Exception {
        JwtConfig jwtConfig = new JwtConfig();
        JwtProperties otherProperties =
                new JwtProperties(OTHER_SECRET, "maks-api", Duration.ofMinutes(15));
        JwtEncoder otherEncoder = jwtConfig.jwtEncoder(jwtConfig.jwtSecretKey(otherProperties));
        String token = tokenWithClock(otherEncoder, otherProperties, Clock.systemUTC());

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    private String tokenWithClock(JwtEncoder encoder, JwtProperties tokenProperties, Clock clock) {
        return new TokenService(encoder, tokenProperties, clock).createAccessToken(user);
    }
}
