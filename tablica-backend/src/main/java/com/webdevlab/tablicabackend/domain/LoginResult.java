package com.webdevlab.tablicabackend.domain;

import com.webdevlab.tablicabackend.dto.UserDTO;

public record LoginResult(String token, UserDTO user) {}
