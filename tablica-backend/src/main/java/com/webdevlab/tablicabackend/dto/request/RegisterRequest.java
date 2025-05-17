package com.webdevlab.tablicabackend.dto.request;

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
    @NotBlank(message = "Username must not be empty or contain only whitespaces")
    @Size(max = 64, message = "Username must have at most {max} characters")
    private String username;

    @NotBlank(message = "Password must not be empty or contain only whitespaces")
    @Size(min = 12, max = 64, message = "Password has to have between {min} and {max} characters")
    private String password;

    @Schema(description = "Set of roles to assign to the user",
            example = "[\"BUYER\"]",
            allowableValues = {"BUYER", "SELLER"})
    @NotEmpty(message = "Roles set must not be empty")
    private Set<Role> roles;
}
