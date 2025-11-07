package com.intellidesk.ticket.service;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.ticket.domain.dto.*;
import com.intellidesk.ticket.domain.vo.TicketVO;

/**
 * 工单服务接口
 *
 * @author IntelliDesk
 */
public interface ITicketService {

    /**
     * 创建工单
     *
     * @param request 创建请求
     * @return 工单ID
     */
    Long createTicket(TicketCreateRequest request);

    /**
     * 更新工单
     *
     * @param request 更新请求
     */
    void updateTicket(TicketUpdateRequest request);

    /**
     * 删除工单
     *
     * @param id 工单ID
     */
    void deleteTicket(Long id);

    /**
     * 根据ID查询工单
     *
     * @param id 工单ID
     * @return 工单信息
     */
    TicketVO getTicketById(Long id);

    /**
     * 查询工单列表（分页）
     *
     * @param request 查询请求
     * @return 工单列表
     */
    PageResult<TicketVO> getTicketList(TicketQueryRequest request);

    /**
     * 分配工单
     *
     * @param request 分配请求
     * @param operatorId 操作人ID
     */
    void assignTicket(TicketAssignRequest request, Long operatorId);

    /**
     * 关闭工单
     *
     * @param id 工单ID
     * @param operatorId 操作人ID
     */
    void closeTicket(Long id, Long operatorId);

    /**
     * 解决工单
     *
     * @param id 工单ID
     * @param operatorId 操作人ID
     */
    void resolveTicket(Long id, Long operatorId);
}
