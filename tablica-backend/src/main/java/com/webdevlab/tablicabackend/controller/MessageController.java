package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.MessageThreadDTO;
import com.webdevlab.tablicabackend.dto.request.CreateMessageRequest;
import com.webdevlab.tablicabackend.dto.request.CreateMessageThreadRequest;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
public class MessageController {

    private final MessageService messageService;

    @PreAuthorize("@security.isEnabled(authentication)")
    @GetMapping("/threads")
    public ResponseEntity<List<MessageThreadDTO>> getUserThreads(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getUserThreads(user.getId()));
    }

    @PreAuthorize("@security.isEnabled(authentication)")
    @GetMapping("/threads/{threadId}")
    public ResponseEntity<MessageThreadDTO> getThreadById(
            @PathVariable String threadId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getThreadById(threadId, user.getId()));
    }

    @PreAuthorize("@security.isEnabled(authentication)")
    @PostMapping("/threads")
    public ResponseEntity<MessageThreadDTO> createThread(
            @RequestBody CreateMessageThreadRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.createThread(request, user.getId()));
    }

    @PreAuthorize("@security.isEnabled(authentication)")
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestBody CreateMessageRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.sendMessage(
                request.getContent(),
                request.getThreadId(),
                user.getId()
        ));
    }
}