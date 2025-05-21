package com.webdevlab.tablicabackend.entity.offer;

import com.webdevlab.tablicabackend.constants.ValidationConstants;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offerTag")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferTag {
    @Id
    @Column(nullable = false, unique = true, length = ValidationConstants.OFFER_TAG_MAX_LENGTH)
    private String tag;
}
