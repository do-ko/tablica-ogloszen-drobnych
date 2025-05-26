package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import com.webdevlab.tablicabackend.entity.user.ContactData;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class OfferDTO {
    private String offerId;
    private String title;
    private String description;
    private OfferStatus status;
    private String sellerId;
    private ContactData contactData;
    private Set<String> tags;
    private List<OfferImageDTO> images;

    public OfferDTO(Offer offer) {
        this.offerId = offer.getId();
        this.title = offer.getTitle();
        this.description = offer.getDescription();
        this.status = offer.getStatus();
        this.sellerId = offer.getSeller().getId();
        this.contactData = offer.getContactData();
        this.tags = offer.getTags().stream().map(OfferTag::getTag).collect(Collectors.toSet());
        this.images = offer.getImages() == null ?
                Collections.emptyList() :
                offer.getImages().stream()
                        .map(OfferImageDTO::new)
                        .collect(Collectors.toList());
    }
}
