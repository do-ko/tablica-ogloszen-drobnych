package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.LoginResult;
import com.webdevlab.tablicabackend.dto.UserDTO;
import com.webdevlab.tablicabackend.dto.request.ChangePasswordRequest;
import com.webdevlab.tablicabackend.dto.response.RegisterResponse;
import com.webdevlab.tablicabackend.dto.request.LoginRequest;
import com.webdevlab.tablicabackend.dto.response.LoginResponse;
import com.webdevlab.tablicabackend.dto.request.RegisterRequest;
import com.webdevlab.tablicabackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final UserService userService;

    @Operation(summary = "Register a new user",
            description = "Creates a new user account with a username, password, and assigned roles.")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody final RegisterRequest registerRequest) {
        UserDTO user = userService.register(registerRequest);
        RegisterResponse response = RegisterResponse.builder()
                .user(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login to the app",
            description = "Authenticates a user with a valid username and password. " +
                    "Returns a JWT access token and basic user information upon successful login.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest authenticationRequest) {
        LoginResult loginResult = userService.login(authenticationRequest);
        LoginResponse response = LoginResponse.builder()
                .token(loginResult.token())
                .user(loginResult.user())
                .build();
        return ResponseEntity.ok(response);
    }
}
