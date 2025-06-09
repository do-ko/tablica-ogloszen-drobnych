package com.webdevlab.tablicabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMessageRequest {

    @Schema(
            description = "The content of the message to be sent in the thread.",
            example = "Hi, I'm interested in your offer."
    )
    @NotBlank(message = "Message content must not be blank")
    private String content;

    @Schema(
            description = "The unique identifier of the thread to which this message belongs.",
            example = "thread-abc123"
    )
    @NotBlank(message = "Thread ID must not be blank")
    private String threadId;
}
