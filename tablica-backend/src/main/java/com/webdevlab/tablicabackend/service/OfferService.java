package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.dto.OfferDTO;
import com.webdevlab.tablicabackend.dto.TagUsageDTO;
import com.webdevlab.tablicabackend.dto.request.CreateOfferRequest;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import com.webdevlab.tablicabackend.entity.user.ContactData;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.offer.InvalidOfferStatusTransitionException;
import com.webdevlab.tablicabackend.exception.offer.OfferNotFoundException;
import com.webdevlab.tablicabackend.exception.offer.UnauthorizedOfferAccessException;
import com.webdevlab.tablicabackend.exception.user.UserNotFoundException;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import com.webdevlab.tablicabackend.repository.OfferTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferTagRepository offerTagRepository;

    public Page<TagUsageDTO> getAllTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return offerTagRepository.findAllTagsWithUsage(pageable);
    }

    public OfferDTO createOffer(User seller, CreateOfferRequest request) {
        Set<OfferTag> tags = request.getTags().stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(tag -> {
                    String capitalizedTag = capitalize(tag);
                    return offerTagRepository.findById(capitalizedTag).orElseGet(() -> offerTagRepository.save(OfferTag.builder().tag(capitalizedTag).build()));
                }).collect(Collectors.toSet());


        Offer.OfferBuilder offerBuilder = Offer.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .seller(seller)
                .status(request.getStatus())
                .tags(tags);

        if (request.isDiscloseSavedContactInformation()) {
            offerBuilder.contactData(seller.getContactData());
        } else if (request.getEmail() != null || request.getPhone() != null) {
            offerBuilder.contactData(new ContactData(request.getEmail(), request.getPhone()));
        }

        return new OfferDTO(offerRepository.save(offerBuilder.build()));
    }

    public Page<OfferDTO> getAllPublishedOffers(String keyword, Pageable pageable) {
        return offerRepository.searchOffersIncludingTags(OfferStatus.PUBLISHED, keyword, pageable).map(OfferDTO::new);
    }

    public OfferDTO changeOrderStatus(String offerId, User user, OfferStatus status) {
        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException("Offer not found"));
        if (!offer.getSeller().getId().equals(user.getId()))
            throw new UnauthorizedOfferAccessException("Only the author of this offer is allowed to change its status");

        if (status.equals(OfferStatus.WORK_IN_PROGRESS))
            throw new InvalidOfferStatusTransitionException("Changing status to WORK_IN_PROGRESS is not allowed. This status is only valid when initially creating an offer");

        if (status.equals(OfferStatus.PUBLISHED) && offer.getStatus().equals(OfferStatus.ARCHIVE))
            throw new InvalidOfferStatusTransitionException("Changing status of ARCHIVED offers is not allowed");

        offer.setStatus(status);
        return new OfferDTO(offerRepository.save(offer));
    }

    private String capitalize(String tag) {
        return tag.substring(0, 1).toUpperCase() + tag.substring(1).toLowerCase();
    }
}
