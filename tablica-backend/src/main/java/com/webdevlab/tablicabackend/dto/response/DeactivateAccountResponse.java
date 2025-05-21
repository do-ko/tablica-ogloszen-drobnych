package com.webdevlab.tablicabackend.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeactivateAccountResponse {
    private String message;
}
