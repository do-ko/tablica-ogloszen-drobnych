package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.domain.LoginResult;
import com.webdevlab.tablicabackend.domain.RoleAddResult;
import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.dto.UserDTO;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.dto.request.ChangeContactDataRequest;
import com.webdevlab.tablicabackend.dto.request.ChangePasswordRequest;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.user.ContactData;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.user.UserAlreadyExistsException;
import com.webdevlab.tablicabackend.exception.user.UserNotFoundException;
import com.webdevlab.tablicabackend.exception.user.UserPasswordException;
import com.webdevlab.tablicabackend.exception.user.UserRoleException;
import com.webdevlab.tablicabackend.dto.request.LoginRequest;
import com.webdevlab.tablicabackend.dto.request.RegisterRequest;
import com.webdevlab.tablicabackend.repository.OfferRepository;
import com.webdevlab.tablicabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OfferRepository offerRepository;

    public UserDTO register(RegisterRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new UserAlreadyExistsException("User with this username already exists");
        });
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .isEnabled(true)
                .build();
        return new UserDTO(userRepository.save(user));
    }

    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        String token = jwtService.generateToken(user);

        return new LoginResult(token, new UserDTO(user));
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UserPasswordException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public RoleAddResult addRole(String userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRoles().contains(role)) {
            throw new UserRoleException("User already has this role");
        }

        user.getRoles().add(role);
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new RoleAddResult(token, new UserDTO(user));
    }

    public void deactivateAccount(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setEnabled(false);
        Set<Offer> offers = user.getOffers();
        offers.forEach(offer -> offer.setStatus(OfferStatus.ARCHIVE));
        userRepository.save(user);
        offerRepository.saveAll(offers);
    }

    public UserDTO changeContactData(String userId, ChangeContactDataRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ContactData contactData = new ContactData(request);
        user.setContactData(contactData);
        return new UserDTO(userRepository.save(user));
    }

    public String getUserNameById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getUsername();
    }
}
