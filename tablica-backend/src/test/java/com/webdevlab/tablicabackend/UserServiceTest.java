package com.webdevlab.tablicabackend;

import com.webdevlab.tablicabackend.domain.LoginResult;
import com.webdevlab.tablicabackend.domain.RoleAddResult;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.UserDTO;
import com.webdevlab.tablicabackend.dto.request.ChangePasswordRequest;
import com.webdevlab.tablicabackend.dto.request.LoginRequest;
import com.webdevlab.tablicabackend.dto.request.RegisterRequest;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.user.UserAlreadyExistsException;
import com.webdevlab.tablicabackend.exception.user.UserNotFoundException;
import com.webdevlab.tablicabackend.exception.user.UserPasswordException;
import com.webdevlab.tablicabackend.exception.user.UserRoleException;
import com.webdevlab.tablicabackend.repository.UserRepository;
import com.webdevlab.tablicabackend.service.JwtService;
import com.webdevlab.tablicabackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserService userService;


    //    REGISTER

    @Test
    void givenValidRegisterRequest_whenRegister_thenUserIsSavedAndUserDTOReturned() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .password("Password123!")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id("generated-id")
                .username("testuser")
                .password("hashedPassword")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserDTO result = userService.register(request);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals(Set.of(Role.BUYER), result.getRoles());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowsUserAlreadyExistsException() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .password("irrelevant")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.findByUsername("existinguser"))
                .thenReturn(Optional.of(mock(User.class)));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenValidRequest_whenRegister_thenPasswordIsEncoded() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("rawPassword")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        User userWithEncodedPassword = User.builder()
                .username("newuser")
                .password("encodedPassword")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userWithEncodedPassword);

        // When
        userService.register(request);

        // Then
        verify(passwordEncoder).encode("rawPassword");
    }

    //    LOGIN

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnsLoginResult() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("Password123!")
                .build();

        User user = User.builder()
                .id("123")
                .username("testuser")
                .password("encodedPassword")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);

        when(jwtService.generateToken(user)).thenReturn("mock-jwt-token");

        // When
        LoginResult result = userService.login(request);

        // Then
        assertNotNull(result);
        assertEquals("mock-jwt-token", result.token());
        assertEquals("testuser", result.user().getUserName());
    }

    @Test
    void givenNonexistentUsername_whenLogin_thenThrowsUserNotFoundException() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("unknownuser")
                .password("password")
                .build();

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.login(request));

        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    //    CHANGE PASSWORD

    @Test
    void givenValidOldPassword_whenChangePassword_thenPasswordIsUpdated() {
        // Given
        String userId = "user123";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPass")
                .newPassword("newPass")
                .build();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .password("encodedOldPass")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        // When
        userService.changePassword(userId, request);

        // Then
        assertEquals("encodedNewPass", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void givenNonexistentUserId_whenChangePassword_thenThrowsUserNotFoundException() {
        // Given
        String userId = "missingUser";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPass")
                .newPassword("newPass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.changePassword(userId, request));
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenInvalidOldPassword_whenChangePassword_thenThrowsUserPasswordException() {
        // Given
        String userId = "user123";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("wrongOldPass")
                .newPassword("newPass")
                .build();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .password("encodedOldPass")
                .roles(Set.of(Role.BUYER))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPass", "encodedOldPass")).thenReturn(false);

        // When & Then
        assertThrows(UserPasswordException.class, () -> userService.changePassword(userId, request));
        verify(userRepository, never()).save(any());
    }

    //    ADD ROLE

    @Test
    void givenValidUserWithoutRole_whenAddRole_thenRoleIsAddedAndTokenReturned() {
        // Given
        String userId = "user123";
        Role newRole = Role.SELLER;

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .roles(new HashSet<>(List.of(Role.BUYER)))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        // When
        RoleAddResult result = userService.addRole(userId, newRole);

        // Then
        assertTrue(result.user().getRoles().contains(newRole));
        assertEquals("mocked-jwt-token", result.token());
        verify(userRepository).save(user);
    }

    @Test
    void givenNonexistentUserId_whenAddRole_thenThrowsUserNotFoundException() {
        // Given
        String userId = "nonexistent";
        Role role = Role.SELLER;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.addRole(userId, role));
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void givenUserAlreadyHasRole_whenAddRole_thenThrowsUserRoleException() {
        // Given
        String userId = "user123";
        Role role = Role.BUYER;

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .roles(new HashSet<>(Set.of(Role.BUYER)))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UserRoleException.class, () -> userService.addRole(userId, role));
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

}
