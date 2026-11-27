package io.github.aref9571.maks.auth;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aref9571.maks.config.JwtConfig;
import io.github.aref9571.maks.config.JwtProperties;
import io.github.aref9571.maks.user.User;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class TokenServiceTest {

    private static final Instant NOW = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final String SECRET =
            Base64.getEncoder()
                    .encodeToString(
                            "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));

    private final JwtProperties properties =
            new JwtProperties(SECRET, "maks-api", Duration.ofMinutes(15));
    private final JwtConfig jwtConfig = new JwtConfig();
    private final SecretKey key = jwtConfig.jwtSecretKey(properties);
    private final TokenService tokenService =
            new TokenService(jwtConfig.jwtEncoder(key), properties, CLOCK);
    private final JwtDecoder decoder = jwtConfig.jwtDecoder(key, properties);

    @Test
    void tokenCarriesIssuerSubjectRolesAndFifteenMinuteLifetime() {
        User user = User.register("anna@example.com", "hash", "Anna Smith", CLOCK);

        Jwt jwt = decoder.decode(tokenService.createAccessToken(user));

        assertThat(jwt.getClaimAsString("iss")).isEqualTo("maks-api");
        assertThat(jwt.getSubject()).isEqualTo(user.id().toString());
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("USER");
        assertThat(jwt.getIssuedAt()).isEqualTo(NOW);
        assertThat(jwt.getExpiresAt()).isEqualTo(NOW.plus(Duration.ofMinutes(15)));
        assertThat(jwt.getHeaders()).containsEntry("alg", "HS256");
    }
}
