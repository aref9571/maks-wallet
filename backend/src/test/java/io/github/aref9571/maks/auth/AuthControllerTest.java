package io.github.aref9571.maks.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.aref9571.maks.common.error.ApiException;
import io.github.aref9571.maks.common.error.ErrorCode;
import io.github.aref9571.maks.config.SecurityConfig;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthService authService;

    @Test
    void returnsCreatedWithUserBodyAndNoLogin() throws Exception {
        UserResponse response =
                new UserResponse(
                        UUID.fromString("6b1f0000-0000-0000-0000-000000000001"),
                        "anna@example.com",
                        "Anna Smith",
                        "USER",
                        Instant.parse("2026-01-01T00:00:00Z"));
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"anna@example.com\",\"fullName\":\"Anna"
                                                + " Smith\",\"password\":\"correct horse battery\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void rejectsInvalidBodyWithFieldErrorsAndNeverCallsService() throws Exception {
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"not-an-email\",\"fullName\":\"   \","
                                                + "\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.length()").value(3));

        verifyNoInteractions(authService);
    }

    @Test
    void returnsConflictWhenEmailIsAlreadyRegistered() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(
                        new ApiException(
                                ErrorCode.EMAIL_ALREADY_REGISTERED,
                                "An account with this email already exists."));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"anna@example.com\",\"fullName\":\"Anna"
                                                + " Smith\",\"password\":\"correct horse battery\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_REGISTERED"));
    }

    @Test
    void acceptsEmailWithSurroundingSpaces() throws Exception {
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\" Anna@Example.com \",\"fullName\":\"Anna"
                                                + " Smith\",\"password\":\"correct horse battery\"}"))
                .andExpect(status().isCreated());
    }
}
