/**
 * 会话信息
 */
export interface Conversation {
  id: number
  conversationNo: string
  customerId: number
  customerName: string
  agentId?: number
  agentName?: string
  status: number
  statusText?: string
  source: number
  startTime: string
  endTime?: string
  firstResponseTime?: string
  waitDuration?: number
  chatDuration?: number
  messageCount: number
  satisfaction?: number
  convertedToTicket: boolean
  createTime?: string
  updateTime?: string
}

/**
 * 消息信息
 */
export interface Message {
  id: number
  conversationId: number
  senderId: number
  senderName: string
  senderType: number
  senderTypeText?: string
  receiverId: number
  receiverName: string
  messageType: number
  messageTypeText?: string
  content: string
  attachmentUrl?: string
  isRead: boolean
  readTime?: string
  createTime: string
}

/**
 * WebSocket消息
 */
export interface WebSocketMessage {
  type: number  // 1-文本 2-图片 3-文件 4-语音 5-视频 10-连接 11-心跳 12-系统
  conversationId?: number
  senderId?: number
  senderName?: string
  senderType?: number
  receiverId?: number
  receiverName?: string
  content?: string
  attachmentUrl?: string
  timestamp: number
  messageId?: number
}

/**
 * 发送消息请求
 */
export interface SendMessageRequest {
  conversationId: number
  senderId: number
  senderName: string
  senderType: number
  receiverId: number
  receiverName: string
  type: number
  content: string
  attachmentUrl?: string
}

/**
 * 会话查询请求
 */
export interface ConversationQueryRequest {
  customerId?: number
  agentId?: number
  status?: number
  pageNum: number
  pageSize: number
}

/**
 * 创建会话请求
 */
export interface CreateConversationRequest {
  customerId: number
  customerName: string
  source?: number
}

/**
 * 分配客服请求
 */
export interface AssignAgentRequest {
  conversationId: number
  agentId: number
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  total: number
  data: T[]
}
