package io.github.aref9571.maks.common.error;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ErrorTestController {

    @GetMapping("/test/api-exception")
    void throwApiException() {
        throw new ApiException(ErrorCode.NOT_FOUND, "user 42 does not exist");
    }

    @PostMapping("/test/validation")
    void validation(@Valid @RequestBody TestRequest request) {}

    record TestRequest(@NotBlank String name, @Email String email) {}

    @GetMapping("/test/accounts/{id}")
    void getAccount(@PathVariable UUID id) {}

    @GetMapping("/test/boom")
    void boom() {
        throw new IllegalStateException("secret internal detail");
    }
}
