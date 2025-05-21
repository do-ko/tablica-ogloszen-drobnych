package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import lombok.Data;

@Data
public class OfferDTO {
    private String offerId;
    private String title;
    private String description;
    private OfferStatus status;
    private String sellerId;

    public OfferDTO(Offer offer) {
        this.offerId = offer.getId();
        this.title = offer.getTitle();
        this.description = offer.getDescription();
        this.status = offer.getStatus();
        this.sellerId = offer.getSeller().getId();
    }
}
