package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.entity.User;
import com.webdevlab.tablicabackend.payload.LoginRequest;
import com.webdevlab.tablicabackend.payload.LoginResponse;
import com.webdevlab.tablicabackend.payload.RegisterRequest;
import com.webdevlab.tablicabackend.service.JwtService;
import com.webdevlab.tablicabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody final RegisterRequest registerRequest) {
        User user = userService.register(registerRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest authenticationRequest) {
        User user = userService.login(authenticationRequest);
        String token = jwtService.generateToken(user);
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .user(user)
                .build();
        return ResponseEntity.ok(response);
    }
}
