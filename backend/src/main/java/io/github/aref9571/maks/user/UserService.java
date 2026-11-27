package io.github.aref9571.maks.user;

import io.github.aref9571.maks.auth.UserResponse;
import io.github.aref9571.maks.common.error.ApiException;
import io.github.aref9571.maks.common.error.ErrorCode;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getById(UUID id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ApiException(ErrorCode.NOT_FOUND, "User not found."));
        return new UserResponse(
                user.id(), user.email(), user.fullName(), user.role().name(), user.createdAt());
    }
}
