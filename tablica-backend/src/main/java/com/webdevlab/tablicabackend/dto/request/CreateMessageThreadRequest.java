package com.webdevlab.tablicabackend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMessageThreadRequest {
    private String receiverId;
    private String subject;
    private String content;
    private String offerId;
}