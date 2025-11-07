/**
 * 工单信息
 */
export interface Ticket {
  id: number
  ticketNo: string
  title: string
  description: string
  categoryId?: number
  categoryName?: string
  priority: number
  priorityText?: string
  status: number
  statusText?: string
  source: number
  sourceText?: string
  submitterId: number
  submitterName: string
  assigneeId?: number
  assigneeName?: string
  attachmentUrls?: string
  resolution?: string
  tags?: string
  rating?: number
  ratingComment?: string
  createTime?: string
  updateTime?: string
  firstResponseTime?: string
  resolvedTime?: string
  closedTime?: string
}

/**
 * 工单日志
 */
export interface TicketLog {
  id: number
  ticketId: number
  operatorId: number
  operatorName: string
  operationType: string
  operationContent: string
  createTime: string
}

/**
 * 工单查询请求
 */
export interface TicketQueryRequest {
  ticketNo?: string
  title?: string
  status?: number
  priority?: number
  submitterId?: number
  assigneeId?: number
  pageNum: number
  pageSize: number
}

/**
 * 工单创建请求
 */
export interface TicketCreateRequest {
  title: string
  description: string
  categoryId?: number
  priority: number
  source: number
  attachmentUrls?: string
  tags?: string
}

/**
 * 工单更新请求
 */
export interface TicketUpdateRequest {
  id: number
  title: string
  description: string
  categoryId?: number
  priority: number
  tags?: string
}

/**
 * 工单分配请求
 */
export interface TicketAssignRequest {
  ticketId: number
  assigneeId: number
}

/**
 * 工单解决请求
 */
export interface TicketResolveRequest {
  ticketId: number
  resolution: string
}

/**
 * 工单评价请求
 */
export interface TicketRateRequest {
  ticketId: number
  rating: number
  ratingComment?: string
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  total: number
  data: T[]
}
