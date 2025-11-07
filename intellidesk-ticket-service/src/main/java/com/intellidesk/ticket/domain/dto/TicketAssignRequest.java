package com.intellidesk.ticket.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 工单分配请求
 *
 * @author IntelliDesk
 */
@Data
public class TicketAssignRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @NotNull(message = "工单ID不能为空")
    private Long ticketId;

    /**
     * 处理人ID
     */
    @NotNull(message = "处理人ID不能为空")
    private Long assigneeId;

    /**
     * 分配说明
     */
    private String remark;
}
