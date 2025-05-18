package com.webdevlab.tablicabackend;

import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import io.jsonwebtoken.Claims;

import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", Base64.getEncoder().encodeToString("my-256-bit-secret-should-be-long-enough!".getBytes()));
    }

    @Test
    void givenUserDetails_whenGenerateToken_thenTokenContainsUsernameAndAuthorities() {
        // Given
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("pass")
                .roles(Set.of(Role.BUYER))
                .build();

        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void givenValidToken_whenIsTokenValid_thenReturnsTrue() {
        // Given
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("pass")
                .roles(Set.of(Role.BUYER))
                .build();

        String token = jwtService.generateToken(userDetails);

        // When & Then
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void givenToken_whenExtractClaim_thenReturnsExpectedClaim() {
        // Given
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("pass")
                .roles(Set.of(Role.BUYER))
                .build();

        String token = jwtService.generateToken(userDetails);

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals("testuser", subject);
    }
}
