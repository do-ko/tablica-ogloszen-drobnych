package com.webdevlab.tablicabackend;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.OfferDTO;
import com.webdevlab.tablicabackend.dto.request.CreateOfferRequest;
import com.webdevlab.tablicabackend.dto.request.UpdateOfferRequest;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.offer.InvalidOfferStatusTransitionException;
import com.webdevlab.tablicabackend.exception.offer.OfferModificationNotAllowedException;
import com.webdevlab.tablicabackend.exception.offer.UnauthorizedOfferAccessException;
import com.webdevlab.tablicabackend.exception.user.UnauthorizedUserAccessException;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import com.webdevlab.tablicabackend.repository.OfferTagRepository;
import com.webdevlab.tablicabackend.repository.UserRepository;
import com.webdevlab.tablicabackend.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock private OfferRepository offerRepository;
    @Mock private OfferTagRepository offerTagRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private OfferService offerService;

    private User seller;
    private Offer offer;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setId("seller1");

        offer = Offer.builder()
                .id("offer123")
                .title("Tytuł")
                .description("Opis")
                .tags(new HashSet<>())
                .status(OfferStatus.WORK_IN_PROGRESS)
                .seller(seller)
                .build();
    }

    @Test
    void shouldCreateOfferWithTagsAndContactData() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setTitle("Tytuł");
        request.setDescription("Opis");
        request.setStatus(OfferStatus.WORK_IN_PROGRESS);
        request.setTags(Set.of("tag1", "tag2"));
        request.setEmail("email@example.com");
        request.setPhone("123456789");

        when(offerTagRepository.findById("Tag1")).thenReturn(Optional.empty());
        when(offerTagRepository.findById("Tag2")).thenReturn(Optional.empty());
        when(offerTagRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(offerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OfferDTO result = offerService.createOffer(seller, request);

        assertEquals("Tytuł", result.getTitle());
        assertEquals("Opis", result.getDescription());
        assertEquals("seller1", result.getSellerId());
        assertEquals(2, result.getTags().size());
        assertNotNull(result.getContactData());
    }

    @Test
    void shouldChangeOfferStatusWhenUserIsSellerAndTransitionIsValid() {
        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));
        when(offerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OfferDTO result = offerService.changeOrderStatus("offer123", seller, OfferStatus.PUBLISHED);

        assertEquals(OfferStatus.PUBLISHED, result.getStatus());
    }

    @Test
    void shouldThrowWhenUnauthorizedUserTriesToChangeOfferStatus() {
        User other = new User();
        other.setId("other");

        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));

        assertThrows(UnauthorizedOfferAccessException.class,
                () -> offerService.changeOrderStatus("offer123", other, OfferStatus.PUBLISHED));
    }

    @Test
    void shouldThrowWhenTryingToChangeStatusToWorkInProgress() {
        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));

        assertThrows(InvalidOfferStatusTransitionException.class,
                () -> offerService.changeOrderStatus("offer123", seller, OfferStatus.WORK_IN_PROGRESS));
    }

    @Test
    void shouldReturnPublishedOffersPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(offerRepository.searchOffers(eq(OfferStatus.PUBLISHED), isNull(), anyString(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(offer)));

        Page<OfferDTO> result = offerService.getAllPublishedOffers("keyword", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowWhenUnauthorizedUserAccessesUnpublishedOffer() {
        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));

        User unauthorized = new User();
        unauthorized.setId("otherUser");

        assertThrows(UnauthorizedOfferAccessException.class,
                () -> offerService.getOrderById("offer123", unauthorized));
    }

    @Test
    void shouldReturnOfferIfUserIsAuthorOrPublished() {
        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));
        OfferDTO dto = offerService.getOrderById("offer123", seller);
        assertEquals("Tytuł", dto.getTitle());
    }

    @Test
    void shouldUpdateOfferWithNewData() {
        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setTitle("Nowy tytuł");
        request.setDescription("Nowy opis");
        request.setTags(Set.of("tag3"));
        request.setEmail("new@example.com");
        request.setPhone("000111222");

        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));
        when(offerTagRepository.findById("Tag3")).thenReturn(Optional.empty());
        when(offerTagRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(offerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OfferDTO dto = offerService.updateOrderById("offer123", seller, request);

        assertEquals("Nowy tytuł", dto.getTitle());
    }

    @Test
    void shouldThrowWhenUnauthorizedUserEditsOffer() {
        User other = new User();
        other.setId("other");

        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));

        UpdateOfferRequest request = new UpdateOfferRequest();
        assertThrows(UnauthorizedOfferAccessException.class,
                () -> offerService.updateOrderById("offer123", other, request));
    }

    @Test
    void shouldThrowWhenEditingArchivedOffer() {
        offer.setStatus(OfferStatus.ARCHIVE);

        when(offerRepository.findById("offer123")).thenReturn(Optional.of(offer));

        assertThrows(OfferModificationNotAllowedException.class,
                () -> offerService.updateOrderById("offer123", seller, new UpdateOfferRequest()));
    }

    @Test
    void shouldReturnUserOffersIfSelf() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById("seller1")).thenReturn(Optional.of(seller));
        when(offerRepository.searchOffers(null, seller, "", pageable)).thenReturn(new PageImpl<>(List.of(offer)));

        Page<OfferDTO> result = offerService.getUsersOffers("seller1", seller, "", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldAllowBuyerToSeeOthersOffers() {
        User buyer = new User();
        buyer.setId("buyer1");
        buyer.setRoles(Set.of(Role.BUYER));

        when(userRepository.findById("seller1")).thenReturn(Optional.of(seller));
        when(offerRepository.searchOffers(OfferStatus.PUBLISHED, seller, "", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(offer)));

        Page<OfferDTO> result = offerService.getUsersOffers("seller1", buyer, "", PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowIfUnauthorizedUserViewsOthersOffers() {
        User unauthorized = new User();
        unauthorized.setId("unauth");
        unauthorized.setRoles(Set.of(Role.SELLER));

        when(userRepository.findById("seller1")).thenReturn(Optional.of(seller));

        assertThrows(UnauthorizedUserAccessException.class,
                () -> offerService.getUsersOffers("seller1", unauthorized, "", PageRequest.of(0, 10)));
    }
}
