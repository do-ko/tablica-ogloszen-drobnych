package com.webdevlab.tablicabackend.service;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.request.SendMessageRequest;
import com.webdevlab.tablicabackend.entity.message.Message;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.message.MessageNotFoundException;
import com.webdevlab.tablicabackend.exception.message.SelfMessagingNotAllowedException;
import com.webdevlab.tablicabackend.exception.message.UnauthorizedMessageAccessException;
import com.webdevlab.tablicabackend.exception.user.UserNotFoundException;
import com.webdevlab.tablicabackend.repository.MessageRepository;
import com.webdevlab.tablicabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageDTO sendMessage(User sender, SendMessageRequest request) {
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (sender.getId().equals(request.getReceiverId())) {
            throw new SelfMessagingNotAllowedException("Cannot send a message to yourself.");
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .subject(request.getSubject())
                .content(request.getContent())
                .read(false)
                .sentAt(Instant.now())
                .build();

        Message saved = messageRepository.save(message);
        return new MessageDTO(saved);
    }

    public Page<MessageDTO> getInbox(User receiver, Pageable pageable) {
        return messageRepository.findByReceiverOrderBySentAtDesc(receiver, pageable)
                .map(MessageDTO::new);
    }

    public Page<MessageDTO> getSent(User sender, Pageable pageable) {
        return messageRepository.findBySenderOrderBySentAtDesc(sender, pageable)
                .map(MessageDTO::new);
    }

    public MessageDTO readMessage(String messageId, User user) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
        if (!message.getReceiver().getId().equals(user.getId())) {
            throw new UnauthorizedMessageAccessException("You are not authorized to read this message");
        }

        if (!message.getRead()) {
            message.setRead(true);
            messageRepository.save(message);
        }

        return new MessageDTO(message);
    }

    public MessageDTO replyToMessage(User sender, String originalMessageId, String content) {
        Message original = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        SendMessageRequest replyRequest = SendMessageRequest.builder()
                .receiverId(original.getSender().getId())
                .subject("Re: " + original.getSubject())
                .content(content)
                .build();

        return sendMessage(sender, replyRequest);
    }
}
