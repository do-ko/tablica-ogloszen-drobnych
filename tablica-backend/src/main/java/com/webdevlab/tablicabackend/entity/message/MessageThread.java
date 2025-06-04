package com.webdevlab.tablicabackend.entity.message;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageThread {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String subject;

    @ElementCollection
    private List<String> participants = new ArrayList<>();

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne
    private Message lastMessage;

    private String offerId;
}