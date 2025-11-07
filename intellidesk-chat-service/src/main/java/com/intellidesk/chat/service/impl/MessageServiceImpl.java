package com.intellidesk.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.intellidesk.chat.domain.dto.WebSocketMessage;
import com.intellidesk.chat.domain.entity.Conversation;
import com.intellidesk.chat.domain.entity.Message;
import com.intellidesk.chat.domain.vo.MessageVO;
import com.intellidesk.chat.enums.MessageType;
import com.intellidesk.chat.enums.SenderType;
import com.intellidesk.chat.mapper.ConversationMapper;
import com.intellidesk.chat.mapper.MessageMapper;
import com.intellidesk.chat.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveMessage(Long senderId, WebSocketMessage wsMessage) {
        log.info("保存消息: senderId={}, conversationId={}", senderId, wsMessage.getConversationId());

        // 创建消息
        Message message = Message.builder()
                .conversationId(wsMessage.getConversationId())
                .senderId(senderId)
                .senderName(wsMessage.getSenderName())
                .senderType(wsMessage.getSenderType())
                .receiverId(wsMessage.getReceiverId())
                .receiverName(wsMessage.getReceiverName())
                .messageType(wsMessage.getType())
                .content(wsMessage.getContent())
                .attachmentUrl(wsMessage.getAttachmentUrl())
                .isRead(false)
                .build();

        messageMapper.insert(message);
        log.info("消息保存成功: messageId={}", message.getId());

        // 更新会话的消息数量
        updateConversationMessageCount(wsMessage.getConversationId());

        return message.getId();
    }

    @Override
    public List<MessageVO> getConversationMessages(Long conversationId, Integer pageNum, Integer pageSize) {
        log.info("查询会话消息: conversationId={}", conversationId);

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId);
        wrapper.orderByAsc(Message::getCreateTime);

        // 简单分页（实际应使用Page对象）
        int offset = (pageNum - 1) * pageSize;
        wrapper.last("LIMIT " + offset + ", " + pageSize);

        List<Message> messages = messageMapper.selectList(wrapper);

        return messages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId, Long userId) {
        log.info("标记消息已读: messageId={}, userId={}", messageId, userId);

        Message message = new Message();
        message.setId(messageId);
        message.setIsRead(true);
        message.setReadTime(LocalDateTime.now());

        messageMapper.updateById(message);
    }

    /**
     * 更新会话的消息数量
     */
    private void updateConversationMessageCount(Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            Conversation update = new Conversation();
            update.setId(conversationId);
            update.setMessageCount(conversation.getMessageCount() + 1);
            conversationMapper.updateById(update);
        }
    }

    /**
     * 实体转VO
     */
    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        vo.setSenderTypeText(SenderType.getDesc(message.getSenderType()));
        vo.setMessageTypeText(MessageType.getDesc(message.getMessageType()));
        return vo;
    }
}
