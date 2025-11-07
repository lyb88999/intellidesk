package com.intellidesk.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.intellidesk.ticket.domain.entity.TicketLog;
import com.intellidesk.ticket.mapper.TicketLogMapper;
import com.intellidesk.ticket.service.ITicketLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工单日志服务实现
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketLogServiceImpl implements ITicketLogService {

    private final TicketLogMapper ticketLogMapper;

    @Override
    public void saveLog(TicketLog log) {
        ticketLogMapper.insert(log);
        log.info("工单日志记录成功: ticketId={}, operationType={}",
                log.getTicketId(), log.getOperationType());
    }

    @Override
    public List<TicketLog> getLogsByTicketId(Long ticketId) {
        LambdaQueryWrapper<TicketLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketLog::getTicketId, ticketId);
        wrapper.orderByDesc(TicketLog::getCreateTime);
        return ticketLogMapper.selectList(wrapper);
    }
}
