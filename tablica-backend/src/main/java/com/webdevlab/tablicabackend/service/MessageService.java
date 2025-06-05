package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.MessageThreadDTO;
import com.webdevlab.tablicabackend.dto.request.CreateMessageThreadRequest;
import com.webdevlab.tablicabackend.entity.message.Message;
import com.webdevlab.tablicabackend.entity.message.MessageThread;
import com.webdevlab.tablicabackend.repository.MessageRepository;
import com.webdevlab.tablicabackend.repository.MessageThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageThreadRepository threadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<MessageThreadDTO> getUserThreads(String userId) {
        return threadRepository.findThreadsByUserId(userId).stream()
                .map(MessageThreadDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageThreadDTO getThreadById(String threadId, String userId) {
        MessageThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        if (!thread.getParticipants().contains(userId)) {
            throw new RuntimeException("Access denied");
        }

        return MessageThreadDTO.fromEntity(thread);
    }

    @Transactional
    public MessageThreadDTO createThread(CreateMessageThreadRequest request, String senderId) {
        LocalDateTime now = LocalDateTime.now();

        List<String> participants = new ArrayList<>();
        participants.add(senderId);
        participants.add(request.getReceiverId());

        MessageThread thread = MessageThread.builder()
                .subject(request.getSubject())
                .participants(participants)
                .createdAt(now)
                .updatedAt(now)
                .offerId(request.getOfferId())
                .messages(new ArrayList<>())
                .build();

        threadRepository.save(thread);

        Message message = Message.builder()
                .sender(senderId)
                .content(request.getContent())
                .isRead(false)
                .createdAt(now)
                .thread(thread)
                .build();

        messageRepository.save(message);

        thread.setLastMessage(message);
        thread.getMessages().add(message);
        threadRepository.save(thread);

        notifyThreadUpdate(request.getReceiverId(), "all");
        notifyThreadUpdate(senderId, "all");

        return MessageThreadDTO.fromEntity(thread);
    }

    @Transactional
    public MessageDTO sendMessage(String content, String threadId, String senderId) {
        MessageThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        if (!thread.getParticipants().contains(senderId)) {
            throw new RuntimeException("Access denied");
        }

        LocalDateTime now = LocalDateTime.now();

        Message message = Message.builder()
                .sender(senderId)
                .content(content)
                .isRead(false)
                .createdAt(now)
                .thread(thread)
                .build();

        messageRepository.save(message);

        thread.setLastMessage(message);
        thread.getMessages().add(message);
        thread.setUpdatedAt(now);
        threadRepository.save(thread);

        for (String participantId : thread.getParticipants()) {
            if (!participantId.equals(senderId)) {
                notifyThreadUpdate(participantId, threadId);
                notifyThreadUpdate(participantId, "all");
            }
        }

        return MessageDTO.fromEntity(message);
    }

    private void notifyThreadUpdate(String userId, String threadId) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/thread-updates",
                threadId
        );
    }
}