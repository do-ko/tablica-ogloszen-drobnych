package com.webdevlab.tablicabackend.dto.response;

import com.webdevlab.tablicabackend.dto.OfferDTO;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOfferResponse {
    private OfferDTO offer;
}
