package com.webdevlab.tablicabackend.payload;

import com.webdevlab.tablicabackend.entity.Role;
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
