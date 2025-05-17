package com.webdevlab.tablicabackend.domain;

import com.webdevlab.tablicabackend.dto.UserDTO;

public record RoleAddResult(String token, UserDTO user) {}
