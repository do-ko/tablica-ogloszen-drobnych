package com.webdevlab.tablicabackend.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    private String username;
    private String password;
}
