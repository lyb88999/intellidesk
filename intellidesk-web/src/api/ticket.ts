import request from './request'
import type {
  Ticket,
  TicketLog,
  TicketQueryRequest,
  TicketCreateRequest,
  TicketUpdateRequest,
  TicketAssignRequest,
  TicketResolveRequest,
  TicketRateRequest,
  PageResult
} from '@/types/ticket'

/**
 * 查询工单列表
 */
export const getTicketList = (params: TicketQueryRequest) => {
  return request({
    url: '/ticket/api/v1/tickets',
    method: 'get',
    params
  }).then((res: any) => res.data as PageResult<Ticket>)
}

/**
 * 查询工单详情
 */
export const getTicketById = (id: number) => {
  return request({
    url: `/ticket/api/v1/tickets/${id}`,
    method: 'get'
  }).then((res: any) => res.data as Ticket)
}

/**
 * 创建工单
 */
export const createTicket = (data: TicketCreateRequest) => {
  return request({
    url: '/ticket/api/v1/tickets',
    method: 'post',
    data
  }).then((res: any) => res.data as number)
}

/**
 * 更新工单
 */
export const updateTicket = (data: TicketUpdateRequest) => {
  return request({
    url: `/ticket/api/v1/tickets/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除工单
 */
export const deleteTicket = (id: number) => {
  return request({
    url: `/ticket/api/v1/tickets/${id}`,
    method: 'delete'
  })
}

/**
 * 分配工单
 */
export const assignTicket = (data: TicketAssignRequest) => {
  return request({
    url: `/ticket/api/v1/tickets/${data.ticketId}/assign`,
    method: 'put',
    params: { assigneeId: data.assigneeId }
  })
}

/**
 * 解决工单
 */
export const resolveTicket = (data: TicketResolveRequest) => {
  return request({
    url: `/ticket/api/v1/tickets/${data.ticketId}/resolve`,
    method: 'put',
    params: { resolution: data.resolution }
  })
}

/**
 * 关闭工单
 */
export const closeTicket = (id: number) => {
  return request({
    url: `/ticket/api/v1/tickets/${id}/close`,
    method: 'put'
  })
}

/**
 * 评价工单
 */
export const rateTicket = (data: TicketRateRequest) => {
  return request({
    url: `/ticket/api/v1/tickets/${data.ticketId}/rate`,
    method: 'put',
    data: {
      rating: data.rating,
      ratingComment: data.ratingComment
    }
  })
}

/**
 * 查询工单日志
 */
export const getTicketLogs = (ticketId: number) => {
  return request({
    url: `/ticket/api/v1/tickets/${ticketId}/logs`,
    method: 'get'
  }).then((res: any) => res.data as TicketLog[])
}
