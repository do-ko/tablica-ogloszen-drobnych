package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.entity.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("security")
public class SecurityService {

    public boolean isSelf(String targetUserId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId().equals(targetUserId);
    }

    public boolean isEnabled(String targetUserId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.isEnabled();
    }
}
