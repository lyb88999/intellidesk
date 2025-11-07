package com.intellidesk.chat.service;

import com.intellidesk.chat.domain.vo.ConversationVO;
import com.intellidesk.common.core.domain.PageResult;

/**
 * 会话服务接口
 *
 * @author IntelliDesk
 */
public interface IConversationService {

    /**
     * 创建会话
     *
     * @param customerId 客户ID
     * @param customerName 客户姓名
     * @param source 来源
     * @return 会话ID
     */
    Long createConversation(Long customerId, String customerName, Integer source);

    /**
     * 分配客服
     *
     * @param conversationId 会话ID
     * @param agentId 客服ID
     */
    void assignAgent(Long conversationId, Long agentId);

    /**
     * 结束会话
     *
     * @param conversationId 会话ID
     */
    void endConversation(Long conversationId);

    /**
     * 查询会话详情
     *
     * @param conversationId 会话ID
     * @return 会话信息
     */
    ConversationVO getConversationById(Long conversationId);

    /**
     * 查询客户的会话列表
     *
     * @param customerId 客户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 会话列表
     */
    PageResult<ConversationVO> getCustomerConversations(Long customerId, Integer pageNum, Integer pageSize);

    /**
     * 查询客服的会话列表
     *
     * @param agentId 客服ID
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 会话列表
     */
    PageResult<ConversationVO> getAgentConversations(Long agentId, Integer status, Integer pageNum, Integer pageSize);
}
