package com.webdevlab.tablicabackend.entity.offer;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

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
}
