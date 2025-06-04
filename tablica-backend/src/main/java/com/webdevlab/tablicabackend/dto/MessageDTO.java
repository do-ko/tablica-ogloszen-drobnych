package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.message.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String id;
    private String sender;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String threadId;

    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .sender(message.getSender())
                .content(message.getContent())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .threadId(message.getThread().getId())
                .build();
    }
}