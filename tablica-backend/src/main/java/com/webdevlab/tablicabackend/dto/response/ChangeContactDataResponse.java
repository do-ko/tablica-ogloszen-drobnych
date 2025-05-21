package com.webdevlab.tablicabackend.dto.response;

import com.webdevlab.tablicabackend.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeContactDataResponse {
    private UserDTO user;
}
