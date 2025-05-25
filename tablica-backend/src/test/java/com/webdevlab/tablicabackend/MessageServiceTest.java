package com.webdevlab.tablicabackend;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.request.SendMessageRequest;
import com.webdevlab.tablicabackend.entity.message.Message;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.exception.message.MessageNotFoundException;
import com.webdevlab.tablicabackend.exception.message.UnauthorizedMessageAccessException;
import com.webdevlab.tablicabackend.exception.user.UserNotFoundException;
import com.webdevlab.tablicabackend.repository.MessageRepository;
import com.webdevlab.tablicabackend.repository.UserRepository;
import com.webdevlab.tablicabackend.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private final User sender = User.builder()
            .id("sender-id")
            .username("senderUser")
            .build();

    private final User receiver = User.builder()
            .id("receiver-id")
            .username("receiverUser")
            .build();

    @Test
    void givenValidRequest_whenSendMessage_thenMessageIsSavedAndReturned() {
        // Given
        SendMessageRequest request = SendMessageRequest.builder()
                .receiverId("receiver-id")
                .subject("Hello")
                .content("This is a message.")
                .build();

        when(userRepository.findById("receiver-id")).thenReturn(Optional.of(receiver));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MessageDTO result = messageService.sendMessage(sender, request);

        // Then
        verify(messageRepository).save(messageCaptor.capture());
        Message saved = messageCaptor.getValue();

        assertNotNull(result);
        assertEquals("Hello", result.getSubject());
        assertEquals("This is a message.", result.getContent());
        assertEquals(sender.getId(), saved.getSender().getId());
        assertEquals(receiver.getId(), saved.getReceiver().getId());
    }

    @Test
    void givenNonexistentReceiver_whenSendMessage_thenThrowsUserNotFoundException() {
        // Given
        SendMessageRequest request = SendMessageRequest.builder()
                .receiverId("unknown-id")
                .subject("Test")
                .content("Test message")
                .build();

        when(userRepository.findById("unknown-id")).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> {
            messageService.sendMessage(sender, request);
        });
    }

    @Test
    void givenMessageIdAndUser_whenReadMessage_thenMessageReturnedAndMarkedAsRead() {
        // Given
        Message message = Message.builder()
                .id("msg-id")
                .sender(sender)
                .receiver(receiver)
                .subject("Hi")
                .content("Hello again.")
                .read(false)
                .sentAt(Instant.now())
                .build();

        when(messageRepository.findById("msg-id")).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // When
        MessageDTO result = messageService.readMessage("msg-id", receiver);

        // Then
        assertNotNull(result);
        assertTrue(result.isRead());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void givenMessageId_whenReadMessageNotOwnedByUser_thenThrowsUnauthorizedMessageAccessException() {
        // Given
        Message message = Message.builder()
                .id("msg-id")
                .receiver(sender) // message sent to someone else
                .build();

        when(messageRepository.findById("msg-id")).thenReturn(Optional.of(message));

        // Then
        assertThrows(UnauthorizedMessageAccessException.class, () -> {
            messageService.readMessage("msg-id", receiver);
        });
    }

    @Test
    void givenValidOriginalMessage_whenReplyToMessage_thenCreatesReplyMessage() {
        // Given
        Message original = Message.builder()
                .id("original-id")
                .sender(receiver)
                .receiver(sender)
                .subject("Original subject")
                .content("Original content")
                .build();

        when(messageRepository.findById("original-id")).thenReturn(Optional.of(original));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation -> invocation.<Message>getArgument(0));
        // When
        MessageDTO reply = messageService.replyToMessage(sender, "original-id", "Reply content");

        // Then
        assertNotNull(reply);
        assertEquals("Re: Original subject", reply.getSubject());
        assertEquals("Reply content", reply.getContent());
    }

    @Test
    void givenInvalidMessageId_whenReplyToMessage_thenThrowsMessageNotFoundException() {
        when(messageRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> {
            messageService.replyToMessage(sender, "invalid-id", "Reply content");
        });
    }
}
