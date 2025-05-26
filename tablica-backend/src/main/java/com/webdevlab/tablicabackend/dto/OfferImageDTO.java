package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OfferImageDTO {
    private String id;
    private String path;

    public OfferImageDTO(OfferImage image) {
        this.id = image.getId();
        this.path = image.getPath();
    }

    public static OfferImageDTO fromEntity(OfferImage image) {
        return OfferImageDTO.builder()
                .id(image.getId())
                .path(image.getPath()) // lub generowany publiczny URL
                .build();
    }
}
