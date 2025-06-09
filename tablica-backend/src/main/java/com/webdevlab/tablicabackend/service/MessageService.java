package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.MessageThreadDTO;
import com.webdevlab.tablicabackend.dto.request.CreateMessageThreadRequest;
import com.webdevlab.tablicabackend.entity.message.Message;
import com.webdevlab.tablicabackend.entity.message.MessageThread;
import com.webdevlab.tablicabackend.exception.message.MessageAccessDeniedException;
import com.webdevlab.tablicabackend.exception.message.ThreadNotFoundException;
import com.webdevlab.tablicabackend.repository.MessageRepository;
import com.webdevlab.tablicabackend.repository.MessageThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found: " + threadId));

        if (!thread.getParticipants().contains(userId)) {
            throw new MessageAccessDeniedException("Access denied");
        }

        return MessageThreadDTO.fromEntity(thread);
    }

    @Transactional
    public MessageThreadDTO createThread(CreateMessageThreadRequest request, String senderId) {

        List<String> participants = new ArrayList<>();
        participants.add(senderId);
        participants.add(request.getReceiverId());

        MessageThread thread = MessageThread.builder()
                .subject(request.getSubject())
                .participants(participants)
                .offerId(request.getOfferId())
                .messages(new ArrayList<>())
                .build();

        threadRepository.save(thread);

        Message message = Message.builder()
                .sender(senderId)
                .content(request.getContent())
                .isRead(false)
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
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found: " + threadId));

        if (!thread.getParticipants().contains(senderId)) {
            throw new MessageAccessDeniedException("Access denied");
        }

        Message message = Message.builder()
                .sender(senderId)
                .content(content)
                .isRead(false)
                .thread(thread)
                .build();

        messageRepository.save(message);

        thread.setLastMessage(message);
        thread.getMessages().add(message);
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