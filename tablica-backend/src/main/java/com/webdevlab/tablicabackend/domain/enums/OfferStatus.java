package com.webdevlab.tablicabackend.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Offers status")
public enum OfferStatus {
    WORK_IN_PROGRESS,
    PUBLISHED,
    ARCHIVE
}
