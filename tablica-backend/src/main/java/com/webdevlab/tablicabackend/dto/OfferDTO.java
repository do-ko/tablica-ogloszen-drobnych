package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class OfferDTO {
    private String offerId;
    private String title;
    private String description;
    private OfferStatus status;
    private String sellerId;
    private Set<String> tags;

    public OfferDTO(Offer offer) {
        this.offerId = offer.getId();
        this.title = offer.getTitle();
        this.description = offer.getDescription();
        this.status = offer.getStatus();
        this.sellerId = offer.getSeller().getId();
        this.tags = offer.getTags().stream().map(OfferTag::getTag).collect(Collectors.toSet());
    }
}
