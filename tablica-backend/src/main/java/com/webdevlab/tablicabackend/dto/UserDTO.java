package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.user.ContactData;
import com.webdevlab.tablicabackend.domain.enums.Role;
import com.webdevlab.tablicabackend.entity.user.User;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class UserDTO {
    private String userId;
    private String userName;
    private ContactData contactData;
    private Set<Role> roles;

    public UserDTO(User user) {
        this.userId = user.getId();
        this.userName = user.getUsername();
        this.contactData = user.getContactData();
        this.roles = user.getRoles();
    }
}
