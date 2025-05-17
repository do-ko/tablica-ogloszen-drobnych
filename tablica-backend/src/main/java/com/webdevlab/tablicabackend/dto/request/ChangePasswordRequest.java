package com.webdevlab.tablicabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordRequest {
    @Schema(description = "The user's old password. Must be between 12 and 64 characters.", example = "Password123!")
    @NotBlank(message = "Password must not be empty or contain only whitespaces")
    @Size(min = 12, max = 64, message = "Password has to have between {min} and {max} characters")
    private String oldPassword;

    @Schema(description = "The user's new password. Must be between 12 and 64 characters.", example = "NewPassword123!")
    @NotBlank(message = "Password must not be empty or contain only whitespaces")
    @Size(min = 12, max = 64, message = "Password has to have between {min} and {max} characters")
    private String newPassword;
}
