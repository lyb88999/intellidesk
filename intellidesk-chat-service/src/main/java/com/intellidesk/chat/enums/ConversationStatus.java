package com.intellidesk.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话状态枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum ConversationStatus {

    /**
     * 等待中
     */
    WAITING(0, "等待中"),

    /**
     * 进行中
     */
    ONGOING(1, "进行中"),

    /**
     * 已结束
     */
    ENDED(2, "已结束");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (ConversationStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "";
    }
}
