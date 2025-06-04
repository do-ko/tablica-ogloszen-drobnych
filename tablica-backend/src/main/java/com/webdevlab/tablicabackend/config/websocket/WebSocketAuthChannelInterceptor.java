package com.webdevlab.tablicabackend.config.websocket;

import com.sun.security.auth.UserPrincipal;
import com.webdevlab.tablicabackend.entity.user.User; // Import Twojej klasy User
import com.webdevlab.tablicabackend.service.JwtService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor != null ? accessor.getCommand() : null)) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                System.out.println("Brak nagłówka Authorization");
            } else {
                String token = authHeaders.getFirst().replace("Bearer ", "");
                String username = jwtService.extractUsername(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    if (userDetails instanceof User user) {
                        String userId = user.getId();

                        UserPrincipal userPrincipal = new UserPrincipal(userId);
                        accessor.setUser(userPrincipal);

                    } else {
                        System.out.println("UserDetails nie jest instancją User");
                    }
                } else {
                    System.out.println("Token nieważny");
                }
            }
        }

        return message;
    }
}