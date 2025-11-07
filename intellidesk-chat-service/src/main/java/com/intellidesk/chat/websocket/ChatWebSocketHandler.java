package com.intellidesk.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellidesk.chat.domain.dto.WebSocketMessage;
import com.intellidesk.chat.service.IConversationService;
import com.intellidesk.chat.service.IMessageService;
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
 * WebSocket 处理器
 *
 * @author IntelliDesk
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final IMessageService messageService;
    private final IConversationService conversationService;
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
                    .content("连接成功")
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

            log.info("收到消息: userId={}, type={}, content={}",
                    userId, wsMessage.getType(), wsMessage.getContent());

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
     * 处理聊天消息
     */
    private void handleChatMessage(Long senderId, WebSocketMessage wsMessage) {
        try {
            // 保存消息到数据库
            Long messageId = messageService.saveMessage(senderId, wsMessage);
            wsMessage.setMessageId(messageId);
            wsMessage.setTimestamp(System.currentTimeMillis());

            // 推送消息给接收者
            if (wsMessage.getReceiverId() != null) {
                WebSocketSession receiverSession = ONLINE_USERS.get(wsMessage.getReceiverId());
                if (receiverSession != null && receiverSession.isOpen()) {
                    sendMessage(receiverSession, wsMessage);
                    log.info("消息已推送: messageId={}, receiverId={}",
                            messageId, wsMessage.getReceiverId());
                } else {
                    log.info("接收者不在线: receiverId={}", wsMessage.getReceiverId());
                }
            }

            // 回复发送者消息发送成功
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
