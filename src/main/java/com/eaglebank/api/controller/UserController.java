package com.eaglebank.api.controller;

import com.eaglebank.api.dto.UserResponse;
import com.eaglebank.api.dto.UserSignupRequest;
import com.eaglebank.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserSignupRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id, Authentication auth) {
        UserResponse response = userService.getUser(id, auth.getName());
        return ResponseEntity.ok(response);
    }

}
