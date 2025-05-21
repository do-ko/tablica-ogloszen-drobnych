package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.dto.OfferDTO;
import com.webdevlab.tablicabackend.dto.TagUsageDTO;
import com.webdevlab.tablicabackend.dto.request.CreateOfferRequest;
import com.webdevlab.tablicabackend.dto.response.CreateOfferResponse;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offer")
@RequiredArgsConstructor
@Tag(name = "Offer")
public class OfferController {
    private final OfferService offerService;

    @Operation(summary = "Get all tags sorted by usage",
            description = "Retrieves a paginated list of all offer tags, sorted by how frequently each tag is used in published offers. " +
                    "Supports pagination parameters such as page number and page size. " +
                    "Sorting is always applied automatically by usage count in descending order, and custom sort parameters will be ignored. " +
                    "Returns a page of tag data including total elements and current page index.")
    @GetMapping("/tag")
    public ResponseEntity<Page<TagUsageDTO>> getAllTags(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(offerService.getAllTags(page, size));
    }

    @PreAuthorize("hasRole('SELLER') and @security.isEnabled(authentication)")
    @Operation(summary = "Create a new offer",
            description = "Allows an authenticated user with the SELLER role to create a new offer. " +
                    "The offer must include a title, description, and status (preferably WORK_IN_PROGRESS or PUBLISHED). " +
                    "The authenticated user must be the one creating the offer and must have an active (enabled) account. " +
                    "Returns the created offer details upon success. " +
                    "Contact information for the offer can either be pulled from the user's saved profile or provided directly in the request. " +
                    "If the 'discloseSavedContactInformation' flag is set to true, any custom email or phone provided will be ignored.")
    @PostMapping()
    public ResponseEntity<CreateOfferResponse> createNewOffer(@Valid @RequestBody CreateOfferRequest request,
                                                              @AuthenticationPrincipal User user) {
        OfferDTO offerDTO = offerService.createOffer(user, request);
        CreateOfferResponse response = CreateOfferResponse.builder()
                .offer(offerDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all published offers",
            description = "Retrieves a paginated list of all published offers that match the given keyword. " +
                    "The keyword is searched case-insensitively in the offer title, description, and associated tags. " +
                    "Supports pagination and sorting using standard Pageable parameters such as page, size, and sort. " +
                    "Returns a page of offer data with metadata like total pages and current page index.")
    @GetMapping()
    public ResponseEntity<Page<OfferDTO>> getAllPublishedOffers(@RequestParam(defaultValue = "") String keyword, Pageable pageable) {
        Page<OfferDTO> offers = offerService.getAllPublishedOffers(keyword, pageable);
        return ResponseEntity.ok(offers);
    }

    @PreAuthorize("hasRole('SELLER') and @security.isEnabled(authentication)")
    @Operation(summary = "Change the status of an offer",
            description = "Allows an authenticated user with the SELLER role to change the status of one of their own offers. " +
                    "The user must be the owner of the offer and have an active (enabled) account. " +
                    "The new status is provided in the request body. " +
                    "Returns the updated offer details upon success.")
    @PutMapping("/{offerId}/status")
    public ResponseEntity<OfferDTO> changeOfferStatus(@PathVariable String offerId,
                                                      @RequestParam OfferStatus status,
                                                      @AuthenticationPrincipal User user) {
        OfferDTO offer = offerService.changeOrderStatus(offerId, user, status);
        return ResponseEntity.ok(offer);
    }
}
