package com.eaglebank.api.service;

import com.eaglebank.api.dto.UserResponse;
import com.eaglebank.api.dto.UserSignupRequest;
import com.eaglebank.api.exception.BadRequestException;
import com.eaglebank.api.exception.ConflictException;
import com.eaglebank.api.exception.ForbiddenException;
import com.eaglebank.api.exception.UserNotFoundException;
import com.eaglebank.api.model.User;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BankAccountRepository bankAccountRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserSignupRequest request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getName() == null) {
            throw new BadRequestException("Missing required fields");
        }

        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase());
        user.setName(request.getName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }

    public UserResponse getUser(String id, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getEmail().equals(currentUserEmail)) {
            throw new ForbiddenException("Cannot access another user");
        }

        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }


}
