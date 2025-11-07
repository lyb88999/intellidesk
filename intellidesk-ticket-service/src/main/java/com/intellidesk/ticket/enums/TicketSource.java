package com.intellidesk.ticket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单来源枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum TicketSource {

    /**
     * Web
     */
    WEB(1, "Web"),

    /**
     * 移动端
     */
    MOBILE(2, "移动端"),

    /**
     * 邮件
     */
    EMAIL(3, "邮件"),

    /**
     * API
     */
    API(4, "API"),

    /**
     * 会话转工单
     */
    CHAT_TO_TICKET(5, "会话转工单");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (TicketSource source : values()) {
            if (source.getCode().equals(code)) {
                return source.getDesc();
            }
        }
        return "";
    }
}
