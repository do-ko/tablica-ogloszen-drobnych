package com.webdevlab.tablicabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    @Schema(description = "The user's unique username used to authenticate.", example = "johndoe")
    @NotBlank(message = "Username must not be empty or contain only whitespaces")
    @Size(max = 64, message = "Username must have at most {max} characters")
    private String username;

    @Schema(description = "The user's password. Must be between 12 and 64 characters.", example = "Password123!")
    @NotBlank(message = "Password must not be empty or contain only whitespaces")
    @Size(min = 12, max = 64, message = "Password has to have between {min} and {max} characters")
    private String password;
}
