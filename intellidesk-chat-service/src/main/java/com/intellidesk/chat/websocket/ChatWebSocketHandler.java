package com.intellidesk.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellidesk.chat.client.AiServiceClient;
import com.intellidesk.chat.client.dto.AiChatRequest;
import com.intellidesk.chat.client.dto.AiChatResponse;
import com.intellidesk.chat.config.ChatAiConfig;
import com.intellidesk.chat.domain.dto.WebSocketMessage;
import com.intellidesk.chat.service.IConversationService;
import com.intellidesk.chat.service.IMessageService;
import com.intellidesk.common.core.domain.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 处理器（集成AI自动回复）
 *
 * @author IntelliDesk
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final IMessageService messageService;
    private final IConversationService conversationService;
    private final AiServiceClient aiServiceClient;
    private final ChatAiConfig chatAiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 在线用户会话管理
     * Key: userId, Value: WebSocketSession
     */
    private static final Map<Long, WebSocketSession> ONLINE_USERS = new ConcurrentHashMap<>();

    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            ONLINE_USERS.put(userId, session);
            log.info("用户连接成功: userId={}, sessionId={}", userId, session.getId());

            // 发送连接成功消息
            WebSocketMessage welcomeMsg = WebSocketMessage.builder()
                    .type(10)  // 连接成功
                    .content("连接成功，AI助手已就绪")
                    .timestamp(System.currentTimeMillis())
                    .build();
            sendMessage(session, welcomeMsg);
        } else {
            log.warn("无效的连接，缺少用户ID: sessionId={}", session.getId());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    /**
     * 接收消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId == null) {
            return;
        }

        try {
            String payload = message.getPayload();
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);

            log.info("收到消息: userId={}, type={}, senderType={}, content={}",
                    userId, wsMessage.getType(), wsMessage.getSenderType(), wsMessage.getContent());

            // 处理不同类型的消息
            switch (wsMessage.getType()) {
                case 1:  // 文本消息
                case 2:  // 图片消息
                case 3:  // 文件消息
                case 4:  // 语音消息
                case 5:  // 视频消息
                    handleChatMessage(userId, wsMessage);
                    break;
                case 11: // 心跳
                    handleHeartbeat(session);
                    break;
                default:
                    log.warn("未知的消息类型: type={}", wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("处理消息失败: userId={}", userId, e);
            sendErrorMessage(session, "消息处理失败: " + e.getMessage());
        }
    }

    /**
     * 连接关闭后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            ONLINE_USERS.remove(userId);
            log.info("用户断开连接: userId={}, sessionId={}, status={}",
                    userId, session.getId(), status);
        }
    }

    /**
     * 处理错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = getUserIdFromSession(session);
        log.error("WebSocket传输错误: userId={}, sessionId={}",
                userId, session.getId(), exception);

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }

        if (userId != null) {
            ONLINE_USERS.remove(userId);
        }
    }

    /**
     * 处理聊天消息（集成AI）
     */
    private void handleChatMessage(Long senderId, WebSocketMessage wsMessage) {
        try {
            // 1. 保存用户消息到数据库
            Long messageId = messageService.saveMessage(senderId, wsMessage);
            wsMessage.setMessageId(messageId);
            wsMessage.setTimestamp(System.currentTimeMillis());

            // 2. 判断是否是客户消息，且需要AI自动回复
            boolean isCustomerMessage = wsMessage.getSenderType() != null && wsMessage.getSenderType() == 1;
            boolean shouldAiReply = chatAiConfig.getEnabled() && chatAiConfig.getAutoReplyCustomer() && isCustomerMessage;

            if (shouldAiReply && wsMessage.getType() == 1) {  // 只对文本消息AI回复
                // 3. 调用AI服务
                handleAiReply(senderId, wsMessage);
            } else {
                // 非客户消息或AI未启用，正常转发给接收者
                forwardMessageToReceiver(wsMessage);
            }

            // 4. 回复发送者消息发送成功
            WebSocketSession senderSession = ONLINE_USERS.get(senderId);
            if (senderSession != null && senderSession.isOpen()) {
                WebSocketMessage ackMsg = WebSocketMessage.builder()
                        .type(12)  // 系统消息
                        .messageId(messageId)
                        .content("消息已发送")
                        .timestamp(System.currentTimeMillis())
                        .build();
                sendMessage(senderSession, ackMsg);
            }
        } catch (Exception e) {
            log.error("处理聊天消息失败: senderId={}", senderId, e);
        }
    }

    /**
     * 处理AI自动回复
     */
    private void handleAiReply(Long customerId, WebSocketMessage customerMessage) {
        try {
            log.info("触发AI自动回复: customerId={}, conversationId={}", customerId, customerMessage.getConversationId());

            // 1. 调用AI服务
            AiChatRequest aiRequest = AiChatRequest.builder()
                    .userId(customerId)
                    .conversationId(customerMessage.getConversationId())
                    .message(customerMessage.getContent())
                    .useKnowledge(true)
                    .build();

            Result<AiChatResponse> result = aiServiceClient.chat(aiRequest);

            if (result != null && result.getData() != null) {
                AiChatResponse aiResponse = result.getData();
                log.info("AI回复成功: needHumanAgent={}, intent={}",
                        aiResponse.getNeedHumanAgent(),
                        aiResponse.getIntent() != null ? aiResponse.getIntent().getType() : "unknown");

                // 2. 构建AI回复消息
                WebSocketMessage aiMessage = WebSocketMessage.builder()
                        .type(1)  // 文本消息
                        .conversationId(customerMessage.getConversationId())
                        .senderId(0L)  // AI的senderId设为0
                        .senderName("AI助手")
                        .senderType(4)  // AI_BOT
                        .receiverId(customerId)
                        .receiverName(customerMessage.getSenderName())
                        .content(aiResponse.getContent())
                        .timestamp(System.currentTimeMillis())
                        .build();

                // 3. 保存AI消息到数据库
                Long aiMessageId = messageService.saveMessage(0L, aiMessage);
                aiMessage.setMessageId(aiMessageId);

                // 4. 发送AI回复给客户
                WebSocketSession customerSession = ONLINE_USERS.get(customerId);
                if (customerSession != null && customerSession.isOpen()) {
                    sendMessage(customerSession, aiMessage);
                    log.info("AI消息已发送给客户: customerId={}, messageId={}", customerId, aiMessageId);
                }

                // 5. 判断是否需要转人工
                if (aiResponse.getNeedHumanAgent() != null && aiResponse.getNeedHumanAgent()) {
                    handleTransferToHuman(customerId, customerMessage.getConversationId());
                }
            } else {
                log.warn("AI服务调用失败，无法获取回复");
            }

        } catch (Exception e) {
            log.error("AI自动回复失败: customerId={}", customerId, e);
            // AI失败时，可以选择转人工或者提示客户
        }
    }

    /**
     * 处理转人工逻辑
     */
    private void handleTransferToHuman(Long customerId, Long conversationId) {
        try {
            log.info("AI判断需要转人工: customerId={}, conversationId={}", customerId, conversationId);

            // 发送系统消息通知客户
            WebSocketMessage systemMsg = WebSocketMessage.builder()
                    .type(12)  // 系统消息
                    .content("正在为您转接人工客服，请稍候...")
                    .timestamp(System.currentTimeMillis())
                    .build();

            WebSocketSession customerSession = ONLINE_USERS.get(customerId);
            if (customerSession != null && customerSession.isOpen()) {
                sendMessage(customerSession, systemMsg);
            }

            // TODO: 实现自动分配客服逻辑
            // 这里可以调用conversationService.assignAgent()
            // 或者通知在线客服有新会话待接入
            log.info("TODO: 自动分配客服 - conversationId={}", conversationId);

        } catch (Exception e) {
            log.error("转人工失败: customerId={}", customerId, e);
        }
    }

    /**
     * 转发消息给接收者（非AI场景）
     */
    private void forwardMessageToReceiver(WebSocketMessage wsMessage) {
        if (wsMessage.getReceiverId() != null) {
            WebSocketSession receiverSession = ONLINE_USERS.get(wsMessage.getReceiverId());
            if (receiverSession != null && receiverSession.isOpen()) {
                sendMessage(receiverSession, wsMessage);
                log.info("消息已转发: messageId={}, receiverId={}",
                        wsMessage.getMessageId(), wsMessage.getReceiverId());
            } else {
                log.info("接收者不在线: receiverId={}", wsMessage.getReceiverId());
            }
        }
    }

    /**
     * 处理心跳
     */
    private void handleHeartbeat(WebSocketSession session) throws IOException {
        WebSocketMessage pongMsg = WebSocketMessage.builder()
                .type(11)  // 心跳响应
                .content("pong")
                .timestamp(System.currentTimeMillis())
                .build();
        sendMessage(session, pongMsg);
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("发送消息失败: sessionId={}", session.getId(), e);
        }
    }

    /**
     * 发送错误消息
     */
    private void sendErrorMessage(WebSocketSession session, String error) {
        WebSocketMessage errorMsg = WebSocketMessage.builder()
                .type(12)  // 系统消息
                .content("错误: " + error)
                .timestamp(System.currentTimeMillis())
                .build();
        sendMessage(session, errorMsg);
    }

    /**
     * 从Session中获取用户ID
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        try {
            // 从URI参数中获取用户ID
            // 例如: ws://localhost:8082/ws/chat?userId=123
            String query = session.getUri().getQuery();
            if (query != null && query.contains("userId=")) {
                String userId = query.split("userId=")[1].split("&")[0];
                return Long.parseLong(userId);
            }
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
        }
        return null;
    }

    /**
     * 推送消息给指定用户
     */
    public void pushMessageToUser(Long userId, WebSocketMessage message) {
        WebSocketSession session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            sendMessage(session, message);
        } else {
            log.info("用户不在线，无法推送消息: userId={}", userId);
        }
    }

    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return ONLINE_USERS.size();
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        WebSocketSession session = ONLINE_USERS.get(userId);
        return session != null && session.isOpen();
    }
}
