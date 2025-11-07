package com.intellidesk.ticket.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 工单查询请求
 *
 * @author IntelliDesk
 */
@Data
public class TicketQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单编号（模糊查询）
     */
    private String ticketNo;

    /**
     * 工单标题（模糊查询）
     */
    private String title;

    /**
     * 状态: 0-待处理, 1-处理中, 2-待回复, 3-已解决, 4-已关闭, 5-已取消
     */
    private Integer status;

    /**
     * 优先级: 1-低, 2-中, 3-高, 4-紧急
     */
    private Integer priority;

    /**
     * 提交人ID
     */
    private Long submitterId;

    /**
     * 处理人ID
     */
    private Long assigneeId;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
