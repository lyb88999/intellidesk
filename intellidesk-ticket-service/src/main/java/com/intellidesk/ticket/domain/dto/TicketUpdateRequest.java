package com.intellidesk.ticket.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新工单请求
 *
 * @author IntelliDesk
 */
@Data
public class TicketUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @NotNull(message = "工单ID不能为空")
    private Long id;

    /**
     * 工单标题
     */
    @NotBlank(message = "工单标题不能为空")
    private String title;

    /**
     * 工单描述
     */
    @NotBlank(message = "工单描述不能为空")
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 优先级: 1-低, 2-中, 3-高, 4-紧急
     */
    private Integer priority;

    /**
     * 标签
     */
    private String tags;

    /**
     * 截止时间
     */
    private LocalDateTime dueTime;
}
