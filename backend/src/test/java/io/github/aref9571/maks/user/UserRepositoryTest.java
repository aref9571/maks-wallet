package io.github.aref9571.maks.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aref9571.maks.TestcontainersConfiguration;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class UserRepositoryTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    private static final Clock LATER_CLOCK =
            Clock.fixed(Instant.parse("2026-01-02T00:00:00Z"), ZoneOffset.UTC);

    @Autowired private UserRepository userRepository;

    @Autowired private TestEntityManager entityManager;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void savesAndLoadsUserByEmail() {
        User user = User.register("anna@example.com", "hash", "Anna Smith", CLOCK);
        userRepository.saveAndFlush(user);
        entityManager.clear();

        Optional<User> found = userRepository.findByEmail("anna@example.com");

        assertThat(found).isPresent();
        assertThat(found.get()).isNotSameAs(user);
    }

    @Test
    void rejectsDuplicateEmail() {
        userRepository.saveAndFlush(User.register("anna@example.com", "hash", "Anna Smith", CLOCK));
        User duplicate = User.register("anna@example.com", "other-hash", "Anna Other", CLOCK);

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("users_email_uq");
    }

    @Test
    void rejectsUpperCaseEmail() {
        assertThatThrownBy(() ->
                        jdbcTemplate.update(
                                "INSERT INTO users (id, email, password_hash, full_name, role, enabled, created_at, updated_at) "
                                        + "VALUES (?, ?, 'hash', 'Anna Smith', 'USER', TRUE, now(), now())",
                                UUID.randomUUID(),
                                "Anna@Example.com"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("users_email_lower_ck");
    }

    @Test
    void updatesChangedFieldWithoutSave() {
        User user = User.register("anna@example.com", "hash", "Anna Smith", CLOCK);
        User managedUser = userRepository.saveAndFlush(user);

        managedUser.changeFullName("Anna Brown", LATER_CLOCK);
        entityManager.flush();

        String storedName =
                jdbcTemplate.queryForObject(
                        "SELECT full_name FROM users WHERE email = ?",
                        String.class,
                        "anna@example.com");
        assertThat(storedName).isEqualTo("Anna Brown");
    }
}
