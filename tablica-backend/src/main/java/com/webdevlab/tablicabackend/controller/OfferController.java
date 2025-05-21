package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.dto.OfferDTO;
import com.webdevlab.tablicabackend.dto.TagUsageDTO;
import com.webdevlab.tablicabackend.dto.request.CreateOfferRequest;
import com.webdevlab.tablicabackend.dto.request.UpdateOfferRequest;
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

    @PreAuthorize("@security.isEnabled(authentication)")
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
                    "Contact information for the offer and tags are optional. " +
                    "Returns the created offer details upon success. ")
    @PostMapping()
    public ResponseEntity<CreateOfferResponse> createNewOffer(@Valid @RequestBody CreateOfferRequest request,
                                                              @AuthenticationPrincipal User user) {
        OfferDTO offerDTO = offerService.createOffer(user, request);
        CreateOfferResponse response = CreateOfferResponse.builder()
                .offer(offerDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@security.isEnabled(authentication)")
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

    @PreAuthorize("@security.isEnabled(authentication)")
    @Operation(summary = "Get offer by id",
            description = "Retrieves the details of a specific offer by its id. " +
                    "If the offer is published, it is publicly accessible. " +
                    "If the offer is not published, access is restricted to the offer's author. " +
                    "Returns the offer's full details if access is granted.")
    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable String offerId,
                                                 @AuthenticationPrincipal User user) {
        OfferDTO offer = offerService.getOrderById(offerId, user);
        return ResponseEntity.ok(offer);
    }

    @PreAuthorize("hasRole('SELLER') and @security.isEnabled(authentication)")
    @Operation(summary = "Update an existing offer",
            description = "Allows an authenticated user with the SELLER role to update an existing offer they own. " +
                    "Only the author of the offer can perform this operation, and their account must be active (enabled). " +
                    "The offer must not be archived; archived offers cannot be modified. " +
                    "Fields such as title, description, status, contact information, and tags can be updated. " +
                    "Contact information for the offer and tags are optional. " +
                    "Returns the updated offer details upon success.")
    @PutMapping("/{offerId}")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable String offerId,
                                                @Valid @RequestBody UpdateOfferRequest request,
                                                @AuthenticationPrincipal User user) {
        OfferDTO offerDTO = offerService.updateOrderById(offerId, user, request);
        return ResponseEntity.ok(offerDTO);
    }

}
