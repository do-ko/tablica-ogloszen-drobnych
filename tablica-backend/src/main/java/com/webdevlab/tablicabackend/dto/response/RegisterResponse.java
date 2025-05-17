package com.webdevlab.tablicabackend.dto.response;

import com.webdevlab.tablicabackend.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterResponse {
    private UserDTO user;
}
