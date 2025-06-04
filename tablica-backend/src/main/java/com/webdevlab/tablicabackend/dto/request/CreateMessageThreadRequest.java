package com.webdevlab.tablicabackend.dto.request;

import lombok.Data;

@Data
public class CreateMessageThreadRequest {
    private String receiverId;
    private String subject;
    private String content;
    private String offerId;
}