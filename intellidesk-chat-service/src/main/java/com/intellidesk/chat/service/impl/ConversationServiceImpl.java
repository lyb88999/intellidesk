package com.intellidesk.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intellidesk.chat.domain.entity.Conversation;
import com.intellidesk.chat.domain.vo.ConversationVO;
import com.intellidesk.chat.enums.ConversationStatus;
import com.intellidesk.chat.mapper.ConversationMapper;
import com.intellidesk.chat.service.IConversationService;
import com.intellidesk.chat.util.ConversationNoGenerator;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.utils.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话服务实现
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements IConversationService {

    private final ConversationMapper conversationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConversation(Long customerId, String customerName, Integer source) {
        log.info("创建会话: customerId={}, customerName={}", customerId, customerName);

        // 生成会话编号
        String conversationNo = ConversationNoGenerator.generate();

        // 创建会话
        Conversation conversation = Conversation.builder()
                .conversationNo(conversationNo)
                .customerId(customerId)
                .customerName(customerName)
                .status(ConversationStatus.WAITING.getCode())
                .source(source != null ? source : 1)  // 默认Web
                .startTime(LocalDateTime.now())
                .messageCount(0)
                .convertedToTicket(false)
                .deleted(0)
                .build();

        conversationMapper.insert(conversation);
        log.info("会话创建成功: id={}, conversationNo={}", conversation.getId(), conversationNo);

        return conversation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignAgent(Long conversationId, Long agentId) {
        log.info("分配客服: conversationId={}, agentId={}", conversationId, agentId);

        // 查询会话
        Conversation conversation = conversationMapper.selectById(conversationId);
        Assert.notNull(conversation, ResultCode.CONVERSATION_NOT_FOUND, "会话不存在");

        // 计算等待时长
        long waitDuration = 0;
        if (conversation.getStartTime() != null) {
            waitDuration = Duration.between(conversation.getStartTime(), LocalDateTime.now()).getSeconds();
        }

        // 更新会话
        Conversation updateEntity = new Conversation();
        updateEntity.setId(conversationId);
        updateEntity.setAgentId(agentId);
        updateEntity.setStatus(ConversationStatus.ONGOING.getCode());
        updateEntity.setFirstResponseTime(LocalDateTime.now());
        updateEntity.setWaitDuration(waitDuration);

        conversationMapper.updateById(updateEntity);
        log.info("客服分配成功: conversationId={}, agentId={}", conversationId, agentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endConversation(Long conversationId) {
        log.info("结束会话: conversationId={}", conversationId);

        // 查询会话
        Conversation conversation = conversationMapper.selectById(conversationId);
        Assert.notNull(conversation, ResultCode.CONVERSATION_NOT_FOUND, "会话不存在");

        // 计算对话时长
        long chatDuration = 0;
        if (conversation.getFirstResponseTime() != null) {
            chatDuration = Duration.between(conversation.getFirstResponseTime(), LocalDateTime.now()).getSeconds();
        }

        // 更新会话状态
        Conversation updateEntity = new Conversation();
        updateEntity.setId(conversationId);
        updateEntity.setStatus(ConversationStatus.ENDED.getCode());
        updateEntity.setEndTime(LocalDateTime.now());
        updateEntity.setChatDuration(chatDuration);

        conversationMapper.updateById(updateEntity);
        log.info("会话已结束: conversationId={}", conversationId);
    }

    @Override
    public ConversationVO getConversationById(Long conversationId) {
        log.info("查询会话详情: conversationId={}", conversationId);
        Conversation conversation = conversationMapper.selectById(conversationId);
        Assert.notNull(conversation, ResultCode.CONVERSATION_NOT_FOUND, "会话不存在");
        return convertToVO(conversation);
    }

    @Override
    public PageResult<ConversationVO> getCustomerConversations(Long customerId, Integer pageNum, Integer pageSize) {
        log.info("查询客户会话列表: customerId={}", customerId);

        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getCustomerId, customerId);
        wrapper.eq(Conversation::getDeleted, 0);
        wrapper.orderByDesc(Conversation::getCreateTime);

        Page<Conversation> page = new Page<>(pageNum, pageSize);
        Page<Conversation> result = conversationMapper.selectPage(page, wrapper);

        List<ConversationVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), voList);
    }

    @Override
    public PageResult<ConversationVO> getAgentConversations(Long agentId, Integer status, Integer pageNum, Integer pageSize) {
        log.info("查询客服会话列表: agentId={}, status={}", agentId, status);

        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getAgentId, agentId);
        wrapper.eq(status != null, Conversation::getStatus, status);
        wrapper.eq(Conversation::getDeleted, 0);
        wrapper.orderByDesc(Conversation::getCreateTime);

        Page<Conversation> page = new Page<>(pageNum, pageSize);
        Page<Conversation> result = conversationMapper.selectPage(page, wrapper);

        List<ConversationVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), voList);
    }

    /**
     * 实体转VO
     */
    private ConversationVO convertToVO(Conversation conversation) {
        ConversationVO vo = new ConversationVO();
        BeanUtils.copyProperties(conversation, vo);
        vo.setStatusText(ConversationStatus.getDesc(conversation.getStatus()));
        return vo;
    }
}
