package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.dto.MessageThreadDTO;
import com.webdevlab.tablicabackend.dto.request.CreateMessageRequest;
import com.webdevlab.tablicabackend.dto.request.CreateMessageThreadRequest;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/threads")
    public ResponseEntity<List<MessageThreadDTO>> getUserThreads(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getUserThreads(user.getId()));
    }

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<MessageThreadDTO> getThreadById(
            @PathVariable String threadId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getThreadById(threadId, user.getId()));
    }

    @PostMapping("/threads")
    public ResponseEntity<MessageThreadDTO> createThread(
            @RequestBody CreateMessageThreadRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.createThread(request, user.getId()));
    }

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