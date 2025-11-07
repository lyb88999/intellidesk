package com.intellidesk.chat.controller;

import com.intellidesk.chat.domain.dto.SendMessageRequest;
import com.intellidesk.chat.domain.dto.WebSocketMessage;
import com.intellidesk.chat.domain.vo.MessageVO;
import com.intellidesk.chat.service.IMessageService;
import com.intellidesk.common.core.domain.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;

    /**
     * 发送消息（通过HTTP接口，也可以通过WebSocket）
     */
    @PostMapping
    public Result<Long> sendMessage(@RequestBody SendMessageRequest request) {
        log.info("发送消息请求: senderId={}, conversationId={}", request.getSenderId(), request.getConversationId());

        WebSocketMessage wsMessage = WebSocketMessage.builder()
                .conversationId(request.getConversationId())
                .senderName(request.getSenderName())
                .senderType(request.getSenderType())
                .receiverId(request.getReceiverId())
                .receiverName(request.getReceiverName())
                .type(request.getType())
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .timestamp(System.currentTimeMillis())
                .build();

        Long messageId = messageService.saveMessage(request.getSenderId(), wsMessage);
        return Result.success(messageId);
    }

    /**
     * 查询会话的历史消息
     */
    @GetMapping("/conversation/{conversationId}")
    public Result<List<MessageVO>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "50") Integer pageSize) {
        log.info("查询会话消息: conversationId={}, pageNum={}, pageSize={}", conversationId, pageNum, pageSize);
        List<MessageVO> messages = messageService.getConversationMessages(conversationId, pageNum, pageSize);
        return Result.success(messages);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public Result<Void> markAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        log.info("标记消息已读: messageId={}, userId={}", messageId, userId);
        messageService.markAsRead(messageId, userId);
        return Result.success();
    }
}
