package com.webdevlab.tablicabackend.dto.request;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOfferRequest {
    @Schema(description = "The id of the user creating the offer.", example = "37fcfb5c-49e2-4326-99c4-90a34a91da8e")
    @NotBlank(message = "User id must not be empty or contain only whitespaces")
    private String userId;

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

    @Schema(
            description = "The current status of the offer. Indicates whether the offer is active, archived, etc.",
            example = "WORK_IN_PROGRESS"
    )
    private OfferStatus status;
}
