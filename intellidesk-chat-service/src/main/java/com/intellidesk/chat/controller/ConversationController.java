package com.intellidesk.chat.controller;

import com.intellidesk.chat.domain.vo.ConversationVO;
import com.intellidesk.chat.service.IConversationService;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 会话控制器
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final IConversationService conversationService;

    /**
     * 创建会话
     */
    @PostMapping
    public Result<Long> createConversation(
            @RequestParam Long customerId,
            @RequestParam String customerName,
            @RequestParam(required = false) Integer source) {
        log.info("创建会话请求: customerId={}, customerName={}", customerId, customerName);
        Long conversationId = conversationService.createConversation(customerId, customerName, source);
        return Result.success(conversationId);
    }

    /**
     * 分配客服
     */
    @PutMapping("/{conversationId}/assign")
    public Result<Void> assignAgent(
            @PathVariable Long conversationId,
            @RequestParam Long agentId) {
        log.info("分配客服请求: conversationId={}, agentId={}", conversationId, agentId);
        conversationService.assignAgent(conversationId, agentId);
        return Result.success();
    }

    /**
     * 结束会话
     */
    @PutMapping("/{conversationId}/end")
    public Result<Void> endConversation(@PathVariable Long conversationId) {
        log.info("结束会话请求: conversationId={}", conversationId);
        conversationService.endConversation(conversationId);
        return Result.success();
    }

    /**
     * 查询会话详情
     */
    @GetMapping("/{conversationId}")
    public Result<ConversationVO> getConversationById(@PathVariable Long conversationId) {
        log.info("查询会话详情: conversationId={}", conversationId);
        ConversationVO conversation = conversationService.getConversationById(conversationId);
        return Result.success(conversation);
    }

    /**
     * 查询客户的会话列表
     */
    @GetMapping("/customer/{customerId}")
    public Result<PageResult<ConversationVO>> getCustomerConversations(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询客户会话列表: customerId={}, pageNum={}, pageSize={}", customerId, pageNum, pageSize);
        PageResult<ConversationVO> result = conversationService.getCustomerConversations(customerId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 查询客服的会话列表
     */
    @GetMapping("/agent/{agentId}")
    public Result<PageResult<ConversationVO>> getAgentConversations(
            @PathVariable Long agentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询客服会话列表: agentId={}, status={}, pageNum={}, pageSize={}", agentId, status, pageNum, pageSize);
        PageResult<ConversationVO> result = conversationService.getAgentConversations(agentId, status, pageNum, pageSize);
        return Result.success(result);
    }
}
