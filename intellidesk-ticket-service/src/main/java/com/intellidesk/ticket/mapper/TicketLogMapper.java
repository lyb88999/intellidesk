package com.intellidesk.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.ticket.domain.entity.TicketLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单日志 Mapper 接口
 *
 * @author IntelliDesk
 */
@Mapper
public interface TicketLogMapper extends BaseMapper<TicketLog> {
}
