package com.webdevlab.tablicabackend.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordResponse {
    private String message;
}
