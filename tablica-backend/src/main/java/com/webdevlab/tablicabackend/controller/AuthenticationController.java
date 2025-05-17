package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.LoginResult;
import com.webdevlab.tablicabackend.dto.UserDTO;
import com.webdevlab.tablicabackend.dto.response.RegisterResponse;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.dto.request.LoginRequest;
import com.webdevlab.tablicabackend.dto.response.LoginResponse;
import com.webdevlab.tablicabackend.dto.request.RegisterRequest;
import com.webdevlab.tablicabackend.service.JwtService;
import com.webdevlab.tablicabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody final RegisterRequest registerRequest) {
        UserDTO user = userService.register(registerRequest);
        RegisterResponse response = RegisterResponse.builder()
                .user(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest authenticationRequest) {
        LoginResult loginResult = userService.login(authenticationRequest);
        LoginResponse response = LoginResponse.builder()
                .token(loginResult.token())
                .user(loginResult.user())
                .build();
        return ResponseEntity.ok(response);
    }

}
