package com.webdevlab.tablicabackend;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.MessageThreadDTO;
import com.webdevlab.tablicabackend.dto.request.CreateMessageThreadRequest;
import com.webdevlab.tablicabackend.entity.message.Message;
import com.webdevlab.tablicabackend.entity.message.MessageThread;
import com.webdevlab.tablicabackend.repository.MessageRepository;
import com.webdevlab.tablicabackend.repository.MessageThreadRepository;
import com.webdevlab.tablicabackend.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageThreadRepository threadRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageService messageService;

    private String senderId;
    private String receiverId;
    private String threadId;
    private MessageThread defaultThread;

    @BeforeEach
    void setUp() {
        senderId = "user-1";
        receiverId = "user-2";
        threadId = "thread-1";

        defaultThread = MessageThread.builder()
                .id(threadId)
                .participants(List.of(senderId, receiverId))
                .messages(new ArrayList<>())
                .subject("Default Subject")
                .build();
    }

    @Test
    void givenValidRequest_whenCreateThread_thenThreadAndMessageAreSaved() {
        CreateMessageThreadRequest request = CreateMessageThreadRequest.builder()
                .subject("Test Subject")
                .content("Hello")
                .offerId("offer-1")
                .receiverId(receiverId)
                .build();

        ArgumentCaptor<MessageThread> threadCaptor = ArgumentCaptor.forClass(MessageThread.class);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        when(threadRepository.save(any(MessageThread.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageThreadDTO result = messageService.createThread(request, senderId);

        verify(threadRepository, times(2)).save(threadCaptor.capture());
        verify(messageRepository).save(messageCaptor.capture());
        verify(messagingTemplate).convertAndSendToUser(receiverId, "/queue/thread-updates", "all");
        verify(messagingTemplate).convertAndSendToUser(senderId, "/queue/thread-updates", "all");

        MessageThread savedThread = threadCaptor.getValue();
        Message savedMessage = messageCaptor.getValue();

        assertNotNull(result);
        assertEquals(2, savedThread.getParticipants().size());
        assertEquals("Test Subject", savedThread.getSubject());
        assertEquals("Hello", savedMessage.getContent());
        assertEquals(senderId, savedMessage.getSender());
        assertEquals(savedThread, savedMessage.getThread());
    }

    @Test
    void givenValidData_whenSendMessage_thenMessageSavedAndThreadUpdated() {
        when(threadRepository.findById(threadId)).thenReturn(Optional.of(defaultThread));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(threadRepository.save(any(MessageThread.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageDTO result = messageService.sendMessage("Hello again", threadId, senderId);

        verify(messageRepository).save(any(Message.class));
        verify(threadRepository).save(defaultThread);
        verify(messagingTemplate).convertAndSendToUser(receiverId, "/queue/thread-updates", threadId);
        verify(messagingTemplate).convertAndSendToUser(receiverId, "/queue/thread-updates", "all");
        verify(messagingTemplate).convertAndSendToUser(senderId, "/queue/thread-updates", threadId);

        assertNotNull(result);
        assertEquals("Hello again", result.getContent());
    }

    @Test
    void givenUserId_whenGetUserThreads_thenReturnMappedDTOs() {
        String userId = "user-123";
        MessageThread thread1 = MessageThread.builder()
                .id("t1")
                .subject("Test 1")
                .participants(List.of(userId))
                .messages(new ArrayList<>())
                .build();

        when(threadRepository.findThreadsByUserId(userId)).thenReturn(List.of(thread1));

        List<MessageThreadDTO> result = messageService.getUserThreads(userId);

        assertEquals(1, result.size());
        assertEquals("Test 1", result.getFirst().getSubject());
    }

    @Test
    void givenValidThreadIdAndUser_whenGetThreadById_thenReturnDTO() {
        when(threadRepository.findById(threadId)).thenReturn(Optional.of(defaultThread));

        MessageThreadDTO result = messageService.getThreadById(threadId, senderId);

        assertNotNull(result);
        assertEquals(threadId, result.getId());
    }

    @Test
    void givenInvalidAccessUser_whenGetThreadById_thenThrowsAccessDenied() {
        String userId = "user-999";
        MessageThread thread = MessageThread.builder()
                .id(threadId)
                .participants(List.of("someone-else"))
                .build();

        when(threadRepository.findById(threadId)).thenReturn(Optional.of(thread));

        assertThrows(RuntimeException.class, () -> messageService.getThreadById(threadId, userId));
    }
}
