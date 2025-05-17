package com.webdevlab.tablicabackend.dto.request;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import com.webdevlab.tablicabackend.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class RegisterRequest {
    @Schema(description = "Unique username for the new user", example = "johndoe")
    @NotBlank(message = "Username must not be empty or contain only whitespaces")
    @Size(max = ValidationConstants.USER_USERNAME_MAX_LENGTH,
            message = "Username must have at most {max} characters")
    private String username;

    @Schema(description = "Password with at least 12 characters", example = "Password123!")
    @NotBlank(message = "Password must not be empty or contain only whitespaces")
    @Size(min = ValidationConstants.USER_PASSWORD_MIN_LENGTH,
            max = ValidationConstants.USER_PASSWORD_MAX_LENGTH,
            message = "Password has to have between {min} and {max} characters")
    private String password;

    @Schema(description = "Set of roles to assign to the user",
            example = "[\"BUYER\"]",
            allowableValues = {"BUYER", "SELLER"})
    @NotEmpty(message = "Roles set must not be empty")
    private Set<Role> roles;
}
