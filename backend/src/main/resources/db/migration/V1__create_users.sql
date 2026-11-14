CREATE TABLE users (
                       id             UUID         PRIMARY KEY,
                       email          VARCHAR(254) NOT NULL,
                       password_hash  VARCHAR(100) NOT NULL,
                       full_name      VARCHAR(100) NOT NULL,
                       role           VARCHAR(20)  NOT NULL,
                       enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at     TIMESTAMPTZ  NOT NULL,
                       updated_at     TIMESTAMPTZ  NOT NULL,
                       CONSTRAINT users_email_uq UNIQUE (email),
                       CONSTRAINT users_email_lower_ck CHECK (email = lower(email)),
                       CONSTRAINT users_role_ck CHECK (role IN ('USER', 'ADMIN'))
);
