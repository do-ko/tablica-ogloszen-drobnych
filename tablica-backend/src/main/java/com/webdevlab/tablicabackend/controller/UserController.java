package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.RoleAddResult;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.UserDTO;
import com.webdevlab.tablicabackend.dto.request.ChangeContactDataRequest;
import com.webdevlab.tablicabackend.dto.request.ChangePasswordRequest;
import com.webdevlab.tablicabackend.dto.response.*;
import com.webdevlab.tablicabackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @PreAuthorize("@security.isSelf(#userId, authentication) and @security.isEnabled(authentication)")
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

    @PreAuthorize("@security.isSelf(#userId, authentication) and @security.isEnabled(authentication)")
    @Operation(summary = "Change user password",
            description = "Allows an authenticated user to change their own password by providing their current password and a new one. " +
                    "The current password must match the existing one in the system. " +
                    "Only the authenticated user can change their own password. " +
                    "Returns a success message upon successful update.")
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@PathVariable String userId,
                                                                 @Valid @RequestBody final ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(userId, changePasswordRequest);
        ChangePasswordResponse response = ChangePasswordResponse.builder()
                .message("Password updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@security.isSelf(#userId, authentication) and @security.isEnabled(authentication)")
    @Operation(summary = "Deactivate user account",
            description = "Allows an authenticated user to deactivate their own account. " +
                    "Only the currently authenticated user can deactivate their own account. " +
                    "Upon deactivation, the user's account will be marked as disabled and all associated offers will be archived. " +
                    "Returns a success message upon successful deactivation.")
    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<DeactivateAccountResponse> deactivateAccount(@PathVariable String userId) {
        userService.deactivateAccount(userId);
        DeactivateAccountResponse response = DeactivateAccountResponse.builder()
                .message("Account deactivated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SELLER') and @security.isSelf(#userId, authentication) and @security.isEnabled(authentication)")
    @Operation(summary = "Update seller's contact information",
            description = "Allows an authenticated user to update their own contact information, including email and phone number. " +
                    "This is typically used by sellers who want to change the contact data associated with their profile. " +
                    "The user must be the owner of the account to perform this operation. " +
                    "Returns the updated user data upon success.")
    @PutMapping("/{userId}/contactData")
    public ResponseEntity<ChangeContactDataResponse> changeContactData(@PathVariable String userId,
                                                                       @Valid @RequestBody ChangeContactDataRequest request) {
        UserDTO userDTO = userService.changeContactData(userId, request);
        ChangeContactDataResponse response = ChangeContactDataResponse.builder()
                .user(userDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@security.isSelf(#userId, authentication) and @security.isEnabled(authentication)")
    @Operation(summary = "Get user name by ID",
            description = "Retrieves the username of a user based on their ID. " +
                    "This endpoint is accessible to all authenticated users. " +
                    "Returns the username if the user with the specified ID exists.")
    @GetMapping("/{userId}/username")
    public ResponseEntity<GetUsernameResponse> getUserNameById(@PathVariable String userId) {
        String userName = userService.getUserNameById(userId);
        GetUsernameResponse response = GetUsernameResponse.builder()
                .username(userName)
                .build();
        return ResponseEntity.ok(response);
    }
}
