package com.intellidesk.ticket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单优先级枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum TicketPriority {

    /**
     * 低
     */
    LOW(1, "低"),

    /**
     * 中
     */
    MEDIUM(2, "中"),

    /**
     * 高
     */
    HIGH(3, "高"),

    /**
     * 紧急
     */
    URGENT(4, "紧急");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (TicketPriority priority : values()) {
            if (priority.getCode().equals(code)) {
                return priority.getDesc();
            }
        }
        return "";
    }
}
