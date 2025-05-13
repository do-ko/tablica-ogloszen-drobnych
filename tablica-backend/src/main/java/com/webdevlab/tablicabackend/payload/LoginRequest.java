package com.webdevlab.tablicabackend.payload;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    private String username;
    private String password;
}
