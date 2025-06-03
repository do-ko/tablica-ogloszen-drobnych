package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.dto.OfferImageDTO;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.offer.OfferNotFoundException;
import com.webdevlab.tablicabackend.exception.offer.UnauthorizedOfferAccessException;
import com.webdevlab.tablicabackend.repository.OfferImageRepository;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final OfferImageRepository offerImageRepository;
    private final OfferRepository offerRepository;

    @Value("${app.upload.dir:${user.dir}/uploads}")
    private String uploadDirRoot;

    private Path getUploadDir() {
        return Paths.get(uploadDirRoot, "offers");
    }

    private String convertPathToRelativeUrl(String filePath) {
        int uploadsIndex = filePath.indexOf("uploads");
        if (uploadsIndex != -1) {
            return "/" + filePath.substring(uploadsIndex);
        }
        return filePath;
    }

    public List<OfferImageDTO> uploadOfferImages(List<MultipartFile> files, String offerId, User user) {
        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException("Offer not found"));
        if (!offer.getSeller().getId().equals(user.getId()))
            throw new UnauthorizedOfferAccessException("Only the author of this offer is allowed to add images");
        validateFiles(files);

        Path uploadDir = getUploadDir();
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
                    .offer(offer)
                    .build();
            savedImages.add(image);
        }
        List<OfferImage> allImages = new ArrayList<>(offer.getImages());
        allImages.addAll(savedImages);
        offerImageRepository.saveAll(allImages);
        offer.setImages(allImages);
        offerRepository.save(offer);

        return allImages.stream()
                .map(image -> {
                    OfferImageDTO dto = new OfferImageDTO(image);
                    dto.setPath(convertPathToRelativeUrl(image.getPath()));
                    return dto;
                })
                .toList();
    }

    @Transactional
    public void deleteOfferImages(String offerId, List<String> imageIds, User user) {
        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException("Offer not found"));
        if (!offer.getSeller().getId().equals(user.getId()))
            throw new UnauthorizedOfferAccessException("Only the author of this offer is allowed to delete its images");

        List<OfferImage> imagesToDelete = offer.getImages().stream()
                .filter(img -> imageIds.contains(img.getId()))
                .toList();

        for (OfferImage image : imagesToDelete) {
            try {
                Files.deleteIfExists(Paths.get(image.getPath()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + image.getPath(), e);
            }
        }
        offer.getImages().removeAll(imagesToDelete);

        offerImageRepository.deleteAll(imagesToDelete);

        offerRepository.save(offer);
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
