package com.webdevlab.tablicabackend.dto.request;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMessageThreadRequest {
    @Schema(
            description = "The ID of the user who will receive the message.",
            example = "user-123"
    )
    @NotBlank(message = "Receiver ID must not be blank")
    private String receiverId;

    @Schema(
            description = "The subject of the message thread.",
            example = "Inquiry about your bicycle offer"
    )
    @NotBlank(message = "Subject must not be blank")
    private String subject;

    @Schema(
            description = "The content of the initial message in the thread.",
            example = "Hi, I’m interested in your bike. Is it still available?"
    )
    @NotBlank(message = "Message content must not be blank")
    @Size(max = ValidationConstants.MESSAGE_MAX_LENGTH, message = "Content must have at most {max} characters")
    private String content;

    @Schema(
            description = "The ID of the offer this thread is related to.",
            example = "offer-abc123"
    )
    @NotBlank(message = "Offer ID must not be blank")
    private String offerId;
}