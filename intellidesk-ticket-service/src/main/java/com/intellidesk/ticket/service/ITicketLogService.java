package com.intellidesk.ticket.service;

import com.intellidesk.ticket.domain.entity.TicketLog;

import java.util.List;

/**
 * 工单日志服务接口
 *
 * @author IntelliDesk
 */
public interface ITicketLogService {

    /**
     * 记录工单日志
     *
     * @param log 日志信息
     */
    void saveLog(TicketLog log);

    /**
     * 查询工单日志列表
     *
     * @param ticketId 工单ID
     * @return 日志列表
     */
    List<TicketLog> getLogsByTicketId(Long ticketId);
}
