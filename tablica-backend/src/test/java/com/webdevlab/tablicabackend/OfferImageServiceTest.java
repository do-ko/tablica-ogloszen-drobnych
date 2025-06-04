package com.webdevlab.tablicabackend;

import com.webdevlab.tablicabackend.dto.OfferImageDTO;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.offer.OfferNotFoundException;
import com.webdevlab.tablicabackend.exception.offer.UnauthorizedOfferAccessException;
import com.webdevlab.tablicabackend.repository.OfferImageRepository;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import com.webdevlab.tablicabackend.service.OfferImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfferImageServiceTest {

    @Mock
    private OfferImageRepository offerImageRepository;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferImageService offerImageService;

    private User seller;
    private Offer offer;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        seller = User.builder().id("user-1").build();
        offer = Offer.builder().id("offer-1").seller(seller).images(new ArrayList<>()).build();

        Field uploadDirField = OfferImageService.class.getDeclaredField("uploadDirRoot");
        uploadDirField.setAccessible(true);
        uploadDirField.set(offerImageService, "src/test/resources/uploads");
    }

    @Test
    void uploadOfferImages_success() {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(offer));

        List<OfferImageDTO> result = offerImageService.uploadOfferImages(List.of(file), "offer-1", seller);

        assertEquals(1, result.size());
        verify(offerImageRepository).saveAll(any());
        verify(offerRepository).save(any());
    }

    @Test
    void uploadOfferImages_unauthorized() {
        User otherUser = User.builder().id("other").build();
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(offer));

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        assertThrows(UnauthorizedOfferAccessException.class, () -> offerImageService.uploadOfferImages(List.of(file), "offer-1", otherUser));
    }

    @Test
    void uploadOfferImages_offerNotFound() {
        when(offerRepository.findById("offer-1")).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        assertThrows(OfferNotFoundException.class, () -> offerImageService.uploadOfferImages(List.of(file), "offer-1", seller));
    }

    @Test
    void deleteOfferImages_success() throws IOException {
        OfferImage image = OfferImage.builder().id("img-1").path(Files.createTempFile("test", ".jpg").toString()).build();
        offer.getImages().add(image);

        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(offer));

        offerImageService.deleteOfferImages("offer-1", List.of("img-1"), seller);

        verify(offerImageRepository).deleteAll(any());
        verify(offerRepository).save(any());
    }

    @Test
    void deleteOfferImages_unauthorized() {
        User otherUser = User.builder().id("other").build();
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(offer));

        assertThrows(UnauthorizedOfferAccessException.class, () -> offerImageService.deleteOfferImages("offer-1", List.of("img-1"), otherUser));
    }

    @Test
    void deleteOfferImages_offerNotFound() {
        when(offerRepository.findById("offer-1")).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> offerImageService.deleteOfferImages("offer-1", List.of("img-1"), seller));
    }
}