package com.webdevlab.tablicabackend.entity.message;

import com.webdevlab.tablicabackend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "message")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean read = false;

    @CreatedDate
    @Column(nullable = false)
    private Instant sentAt;

}
