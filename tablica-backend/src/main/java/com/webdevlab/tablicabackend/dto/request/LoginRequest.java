package com.webdevlab.tablicabackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    @NotBlank(message = "Username must not be empty or contain only whitespaces")
    @Size(max = 64, message = "Username must have at most {max} characters")
    private String username;

    @NotBlank(message = "Password must not be empty or contain only whitespaces")
    @Size(min = 12, max = 64, message = "Password has to have between {min} and {max} characters")
    private String password;
}
