package com.webdevlab.tablicabackend.dto;

import com.webdevlab.tablicabackend.entity.message.MessageThread;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageThreadDTO {
    private String id;
    private String subject;
    private List<String> participants;
    private List<MessageDTO> messages;
    private MessageDTO lastMessage;
    private Instant createdAt;
    private Instant updatedAt;
    private String offerId;

    public static MessageThreadDTO fromEntity(MessageThread thread) {
        return MessageThreadDTO.builder()
                .id(thread.getId())
                .subject(thread.getSubject())
                .participants(thread.getParticipants())
                .messages(thread.getMessages().stream()
                        .map(MessageDTO::fromEntity)
                        .collect(Collectors.toList()))
                .lastMessage(thread.getLastMessage() != null ?
                        MessageDTO.fromEntity(thread.getLastMessage()) : null)
                .createdAt(thread.getCreatedAt())
                .updatedAt(thread.getLastModifiedDate())
                .offerId(thread.getOfferId())
                .build();
    }
}