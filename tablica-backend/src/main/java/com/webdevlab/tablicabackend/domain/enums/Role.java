package com.webdevlab.tablicabackend.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User role in the system")
public enum Role {
    SELLER,
    BUYER
}
