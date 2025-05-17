package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.RoleAddResult;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.request.ChangePasswordRequest;
import com.webdevlab.tablicabackend.dto.response.ChangePasswordResponse;
import com.webdevlab.tablicabackend.dto.response.RoleAddResponse;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @PreAuthorize("@security.isSelf(#userId, authentication)")
    @Operation(summary = "Add a role to the user",
            description = "Adds a new role to the specified user by their ID. Only the authenticated user can modify their own roles. " +
                    "Returns an updated JWT token and user details after the role is added. " +
                    "Only applicable roles will be accepted; an error is returned if the user already has the role or if the request is unauthorized.")
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

    @PreAuthorize("@security.isSelf(#userId, authentication)")
    @Operation(summary = "Change user password",
            description = "Allows an authenticated user to change their own password by providing their current password and a new one. " +
                    "The current password must match the existing one in the system. " +
                    "Only the authenticated user can change their own password. " +
                    "Returns a success message upon successful update.")
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@PathVariable String userId,
                                                                 @Valid @RequestBody final ChangePasswordRequest changePasswordRequest,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(userId, changePasswordRequest);
        ChangePasswordResponse response = ChangePasswordResponse.builder()
                .message("Password updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

}
