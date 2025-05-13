package com.webdevlab.tablicabackend.payload;

import com.webdevlab.tablicabackend.entity.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String token;
    private User user;
}
