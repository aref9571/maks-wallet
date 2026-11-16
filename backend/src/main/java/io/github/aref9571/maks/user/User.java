package io.github.aref9571.maks.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    private String email;

    private String passwordHash;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    private Instant createdAt;

    private Instant updatedAt;

    protected User() {
    }

    public static User register(String email, String passwordHash, String fullName, Clock clock) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.email = email;
        user.passwordHash = passwordHash;
        user.fullName = fullName;
        user.role = Role.USER;
        user.enabled = true;
        Instant now = clock.instant();
        user.createdAt = now;
        user.updatedAt = now;
        return user;
    }

    public void changeFullName(String newFullName, Clock clock) {
        this.fullName = newFullName;
        this.updatedAt = clock.instant();
    }
}
