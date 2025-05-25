package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.message.Message;
import lombok.Data;

import java.time.Instant;

@Data
public class MessageDTO {
    private String senderId;
    private String receiverId;
    private String subject;
    private String content;
    private Instant sentAt;
    private boolean read;

    public MessageDTO(Message message) {
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.subject = message.getSubject();
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
        this.read = message.getRead();
    }
}
