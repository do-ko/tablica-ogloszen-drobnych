package com.webdevlab.tablicabackend.dto.response;

import com.webdevlab.tablicabackend.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RoleAddResponse {
    private String token;
    private UserDTO user;
}
