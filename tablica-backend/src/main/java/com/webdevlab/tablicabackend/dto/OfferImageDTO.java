package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfferImageDTO {
    private String path;

    public OfferImageDTO(OfferImage image) {
        this.path = image.getPath();
    }
}
