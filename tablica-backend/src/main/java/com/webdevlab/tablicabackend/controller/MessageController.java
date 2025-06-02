package com.webdevlab.tablicabackend.controller;

import com.webdevlab.tablicabackend.dto.MessageDTO;
import com.webdevlab.tablicabackend.entity.user.User;
import com.webdevlab.tablicabackend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/{userId}/messages")
@RequiredArgsConstructor
@Tag(name = "Messages")
public class MessageController {
    private final MessageService messageService;

    @PreAuthorize("@security.isEnabled(authentication)")
    @Operation(summary = "Get inbox",
            description = "Returns a paginated list of received messages for the authenticated user.")
    @GetMapping("/inbox")
    public ResponseEntity<Page<MessageDTO>> getInbox(@AuthenticationPrincipal User user,
                                                     @PageableDefault(sort = "sentAt", direction = Sort.Direction.DESC)
                                                     Pageable pageable) {
        Page<MessageDTO> inbox = messageService.getInbox(user, pageable);
        return ResponseEntity.ok(inbox);
    }

    @PreAuthorize("@security.isEnabled(authentication)")
    @Operation(summary = "Get sent messages",
            description = "Returns a paginated list of messages sent by the authenticated user.")
    @GetMapping("/sent")
    public ResponseEntity<Page<MessageDTO>> getSentMessages(@AuthenticationPrincipal User user,
                                                            @PageableDefault(sort = "sentAt", direction = Sort.Direction.DESC)
                                                            Pageable pageable) {
        Page<MessageDTO> sent = messageService.getSent(user, pageable);
        return ResponseEntity.ok(sent);
    }

    @PreAuthorize("@security.isEnabled(authentication)")
    @Operation(summary = "Read a specific message",
            description = "Reads a message received by the authenticated user. Marks it as read.")
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDTO> readMessage(@AuthenticationPrincipal User user,
                                                  @PathVariable String messageId) {
        MessageDTO message = messageService.readMessage(messageId, user);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("@security.isEnabled(authentication)")
    @Operation(summary = "Reply to a message",
            description = "Replies to an existing message. Automatically sets the subject as 'Re: [original subject]'.")
    @PostMapping("/{messageId}/reply")
    public ResponseEntity<MessageDTO> replyToMessage(@AuthenticationPrincipal User user,
                                                     @PathVariable String messageId,
                                                     @RequestBody String content) {
        MessageDTO reply = messageService.replyToMessage(user, messageId, content);
        return ResponseEntity.ok(reply);
    }
}
