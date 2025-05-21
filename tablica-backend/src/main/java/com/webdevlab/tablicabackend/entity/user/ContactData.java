package com.webdevlab.tablicabackend.entity.user;

import com.webdevlab.tablicabackend.dto.request.ChangeContactDataRequest;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactData {
    private String email;
    private String phone;

    public ContactData(ChangeContactDataRequest request) {
        this.email = request.getEmail();
        this.phone = request.getPhone();
    }
}
