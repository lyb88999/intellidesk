package com.intellidesk.chat.service;

import com.intellidesk.chat.domain.dto.WebSocketMessage;
import com.intellidesk.chat.domain.vo.MessageVO;

import java.util.List;

/**
 * 消息服务接口
 *
 * @author IntelliDesk
 */
public interface IMessageService {

    /**
     * 保存消息
     *
     * @param senderId 发送者ID
     * @param wsMessage WebSocket消息
     * @return 消息ID
     */
    Long saveMessage(Long senderId, WebSocketMessage wsMessage);

    /**
     * 查询会话的历史消息
     *
     * @param conversationId 会话ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 消息列表
     */
    List<MessageVO> getConversationMessages(Long conversationId, Integer pageNum, Integer pageSize);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     */
    void markAsRead(Long messageId, Long userId);
}
