package com.webdevlab.tablicabackend.entity.user;

import jakarta.persistence.Embeddable;

@Embeddable
public class ContactData {
    private String email;
    private String phone;
}
