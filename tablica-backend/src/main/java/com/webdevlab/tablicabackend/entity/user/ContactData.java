package com.webdevlab.tablicabackend.entity.user;

import com.webdevlab.tablicabackend.dto.request.ChangeContactDataRequest;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ContactData {
    private String email;
    private String phone;

    public ContactData(ChangeContactDataRequest request) {
        this.email = request.getEmail();
        this.phone = request.getPhone();
    }
}
