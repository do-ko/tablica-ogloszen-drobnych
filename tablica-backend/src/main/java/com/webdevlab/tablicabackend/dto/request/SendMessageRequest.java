package com.webdevlab.tablicabackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageRequest {

    private String receiverId;

    @NotBlank(message = "Subject must not be blank")
    @Size(min = 1, max = 100, message = "Subject must be between 1 and 100 characters")
    private String subject;

    @NotBlank(message = "Content must not be blank")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;
}
