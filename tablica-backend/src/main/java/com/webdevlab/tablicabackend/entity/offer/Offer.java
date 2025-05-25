package com.webdevlab.tablicabackend.entity.offer;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.entity.Auditable;
import com.webdevlab.tablicabackend.entity.user.ContactData;
import com.webdevlab.tablicabackend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "offer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = ValidationConstants.OFFER_TITLE_MAX_LENGTH)
    private String title;

    @Column(nullable = false, length = ValidationConstants.OFFER_DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @Embedded
    private ContactData contactData;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "offer_tags",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag"))
    private Set<OfferTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferImage> images = new ArrayList<>();
}
