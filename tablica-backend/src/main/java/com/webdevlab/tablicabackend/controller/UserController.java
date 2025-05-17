package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.RoleAddResult;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.response.RoleAddResponse;
import com.webdevlab.tablicabackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Add a role to the user",
            description = "Adds a new role to the specified user by their ID. " +
                    "Returns an updated JWT token and user details after the role is added. " +
                    "Only applicable roles will be accepted; an error is returned if the user already has the role.")
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
