package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import com.webdevlab.tablicabackend.exception.offer.OfferNotFoundException;
import com.webdevlab.tablicabackend.repository.OfferImageRepository;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.webdevlab.tablicabackend.constants.ValidationConstants.OFFER_MAX_FILES;
import static com.webdevlab.tablicabackend.constants.ValidationConstants.OFFER_MAX_FILE_SIZE_BYTES;

@Service
@RequiredArgsConstructor
public class OfferImageService {

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "offers");

    public List<OfferImage> uploadOfferImages(List<MultipartFile> files) {
        validateFiles(files);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }

        List<OfferImage> savedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            try {
                file.transferTo(filePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Could not save file: " + file.getOriginalFilename(), e);
            }
            OfferImage image = OfferImage.builder()
                    .path(filePath.toString())
                    .build();
            savedImages.add(image);
        }
        return savedImages;
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > OFFER_MAX_FILES) {
            throw new IllegalArgumentException("Maximum number of files exceeded. Max: " + OFFER_MAX_FILES);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("One of the uploaded files is empty.");
            }

            if (file.getSize() > OFFER_MAX_FILE_SIZE_BYTES) {
                throw new IllegalArgumentException("File " + file.getOriginalFilename() + " is too large. Max size is 5MB.");
            }

            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Invalid file type: " + contentType + ". Only JPEG and PNG are allowed.");
            }
        }
    }

}
