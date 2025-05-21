package com.webdevlab.tablicabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangeContactDataRequest {
    @Schema(description = "User's email address", example = "example@example.com")
    @Email(message = "Email must be in a correct format.")
    private String email;

    @Schema(description = "User's phone number in international format", example = "+48123456789")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in a valid international format (e.g., +48123456789).")
    private String phone;
}
