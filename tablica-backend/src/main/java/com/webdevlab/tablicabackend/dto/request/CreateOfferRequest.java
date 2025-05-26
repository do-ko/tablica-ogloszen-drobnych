package com.webdevlab.tablicabackend.dto.request;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOfferRequest {
    @Schema(description = "The title of the offer. Should be concise and descriptive.", example = "Used Bicycle for Sale")
    @NotBlank(message = "Title must not be empty or contain only whitespaces")
    @Size(max = ValidationConstants.OFFER_TITLE_MAX_LENGTH,
            message = "Title must have at most {max} characters")
    private String title;

    @Schema(description = "A detailed description of the offer's content or condition.", example = "This is a gently used mountain bike, ideal for off-road trails.")
    @NotBlank(message = "Description must not be empty or contain only whitespaces")
    @Size(max = ValidationConstants.OFFER_DESCRIPTION_MAX_LENGTH,
            message = "Description must have at most {max} characters")
    private String description;

    @Schema(description = "The current status of the offer. Indicates whether the offer is active, archived, etc.",
            example = "WORK_IN_PROGRESS")
    private OfferStatus status;

    @Schema(description = "A set of tag names to categorize the offer. Each tag should match an existing tag in the system. " +
            "Tags help users filter and discover relevant offers.",
            example = "[\"Electronics\", \"Gaming\"]",
            nullable = true)
    private Set<String> tags;

    @Schema(description = "Optional custom email to associate with the offer if not using saved contact information. " +
            "Ignored if 'discloseSavedContactInformation' is true.",
            example = "custom.email@example.com",
            nullable = true)
    @Email(message = "Email must be in a correct format.")
    private String email;

    @Schema(description = "Optional custom phone number to associate with the offer if not using saved contact information. " +
            "Ignored if 'discloseSavedContactInformation' is true.",
            example = "+48123456789",
            nullable = true)
    @Pattern(regexp = "^$|^\\+?[1-9]\\d{1,14}$",
            message = "Phone number must be in a valid international format (e.g., +48123456789).")
    private String phone;
}
