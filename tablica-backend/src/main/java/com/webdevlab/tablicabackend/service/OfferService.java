package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.dto.OfferDTO;
import com.webdevlab.tablicabackend.dto.request.CreateOfferRequest;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.user.UserNotFoundException;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import com.webdevlab.tablicabackend.repository.OfferTagRepository;
import com.webdevlab.tablicabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferTagRepository offerTagRepository;
    private final UserRepository userRepository;

    public Page<String> getAllTags(Pageable pageable) {
        return offerTagRepository.findAll(pageable).map(OfferTag::getTag);
    }

    public OfferDTO createOffer(CreateOfferRequest request) {
        User seller = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<OfferTag> tags = request.getTags().stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(tag -> {
                    String capitalizedTag = capitalize(tag);
                    return offerTagRepository.findById(capitalizedTag).orElseGet(() -> offerTagRepository.save(OfferTag.builder().tag(capitalizedTag).build()));
                }).collect(Collectors.toSet());

        Offer offer = Offer.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .seller(seller)
                .status(request.getStatus())
                .tags(tags)
                .build();

        return new OfferDTO(offerRepository.save(offer));
    }

    private String capitalize(String tag) {
        return tag.substring(0, 1).toUpperCase() + tag.substring(1).toLowerCase();
    }
}
