package com.webdevlab.tablicabackend.dto.request;

import lombok.Data;

@Data
public class CreateMessageRequest {
    private String content;
    private String threadId;
}