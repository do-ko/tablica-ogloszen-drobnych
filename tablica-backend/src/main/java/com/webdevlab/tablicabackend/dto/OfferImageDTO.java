package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferImageDTO {
    private String id;
    private String path;

    public OfferImageDTO(OfferImage image) {
        this.id = image.getId();

        String fullPath = image.getPath();
        int uploadsIndex = fullPath.indexOf("uploads");
        if (uploadsIndex != -1) {
            this.path = fullPath.substring(uploadsIndex);
        } else {
            this.path = fullPath;
        }
    }

    public static OfferImageDTO fromEntity(OfferImage image) {
        OfferImageDTO dto = new OfferImageDTO();
        dto.setId(image.getId());

        String fullPath = image.getPath();
        int uploadsIndex = fullPath.indexOf("uploads");
        if (uploadsIndex != -1) {
            dto.setPath(fullPath.substring(uploadsIndex));
        } else {
            dto.setPath(fullPath);
        }

        return dto;
    }
}