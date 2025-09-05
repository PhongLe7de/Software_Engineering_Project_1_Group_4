package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.response.UserDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import com.otp.whiteboard.enums.Status;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Nonnull
    public UserDto getUserProfilePicture(@NotBlank String displayName) {
        logger.debug("Search user with display name: {}", displayName);
        User user = userRepository.findUserByDisplayName(displayName)
                .orElseThrow(() -> new IllegalArgumentException("Display name already in use"));
        return new UserDto(user);
    }


}
