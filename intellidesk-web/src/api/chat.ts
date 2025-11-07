import request from './request'
import type {
  Conversation,
  Message,
  SendMessageRequest,
  ConversationQueryRequest,
  CreateConversationRequest,
  AssignAgentRequest,
  PageResult
} from '@/types/chat'

/**
 * 创建会话
 */
export const createConversation = (data: CreateConversationRequest) => {
  return request({
    url: '/chat/api/v1/conversations',
    method: 'post',
    params: {
      customerId: data.customerId,
      customerName: data.customerName,
      source: data.source
    }
  }).then((res: any) => res.data as number)
}

/**
 * 分配客服
 */
export const assignAgent = (data: AssignAgentRequest) => {
  return request({
    url: `/chat/api/v1/conversations/${data.conversationId}/assign`,
    method: 'put',
    params: { agentId: data.agentId }
  })
}

/**
 * 结束会话
 */
export const endConversation = (conversationId: number) => {
  return request({
    url: `/chat/api/v1/conversations/${conversationId}/end`,
    method: 'put'
  })
}

/**
 * 查询会话详情
 */
export const getConversationById = (conversationId: number) => {
  return request({
    url: `/chat/api/v1/conversations/${conversationId}`,
    method: 'get'
  }).then((res: any) => res.data as Conversation)
}

/**
 * 查询客户的会话列表
 */
export const getCustomerConversations = (customerId: number, pageNum: number = 1, pageSize: number = 10) => {
  return request({
    url: `/chat/api/v1/conversations/customer/${customerId}`,
    method: 'get',
    params: { pageNum, pageSize }
  }).then((res: any) => res.data as PageResult<Conversation>)
}

/**
 * 查询客服的会话列表
 */
export const getAgentConversations = (agentId: number, status?: number, pageNum: number = 1, pageSize: number = 10) => {
  return request({
    url: `/chat/api/v1/conversations/agent/${agentId}`,
    method: 'get',
    params: { status, pageNum, pageSize }
  }).then((res: any) => res.data as PageResult<Conversation>)
}

/**
 * 发送消息（HTTP方式）
 */
export const sendMessage = (data: SendMessageRequest) => {
  return request({
    url: '/chat/api/v1/messages',
    method: 'post',
    data
  }).then((res: any) => res.data as number)
}

/**
 * 查询会话的历史消息
 */
export const getConversationMessages = (conversationId: number, pageNum: number = 1, pageSize: number = 50) => {
  return request({
    url: `/chat/api/v1/messages/conversation/${conversationId}`,
    method: 'get',
    params: { pageNum, pageSize }
  }).then((res: any) => res.data as Message[])
}

/**
 * 标记消息为已读
 */
export const markMessageAsRead = (messageId: number, userId: number) => {
  return request({
    url: `/chat/api/v1/messages/${messageId}/read`,
    method: 'put',
    params: { userId }
  })
}
