package com.intellidesk.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.ticket.domain.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单 Mapper 接口
 *
 * @author IntelliDesk
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
