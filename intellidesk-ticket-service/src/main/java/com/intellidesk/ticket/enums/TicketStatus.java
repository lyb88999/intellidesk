package com.intellidesk.ticket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单状态枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum TicketStatus {

    /**
     * 待处理
     */
    PENDING(0, "待处理"),

    /**
     * 处理中
     */
    PROCESSING(1, "处理中"),

    /**
     * 待回复
     */
    WAITING_REPLY(2, "待回复"),

    /**
     * 已解决
     */
    RESOLVED(3, "已解决"),

    /**
     * 已关闭
     */
    CLOSED(4, "已关闭"),

    /**
     * 已取消
     */
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (TicketStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "";
    }
}
