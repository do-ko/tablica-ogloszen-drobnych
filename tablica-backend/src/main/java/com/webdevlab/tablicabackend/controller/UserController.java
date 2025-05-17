package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.RoleAddResult;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.response.RoleAddResponse;
import com.webdevlab.tablicabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/{userId}/addRole")
    public ResponseEntity<RoleAddResponse> addRoleToUser(@PathVariable String userId,
                                                         @RequestParam Role role) {
        RoleAddResult roleAddResult = userService.addRole(userId, role);
        RoleAddResponse response = RoleAddResponse.builder()
                .token(roleAddResult.token())
                .user(roleAddResult.user())
                .build();
        return ResponseEntity.ok(response);
    }
}
