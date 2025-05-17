package com.webdevlab.tablicabackend.dto.request;

import com.webdevlab.tablicabackend.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Set<Role> roles;
}
