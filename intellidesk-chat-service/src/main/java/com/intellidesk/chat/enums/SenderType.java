package com.intellidesk.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送者类型枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum SenderType {

    /**
     * 客户
     */
    CUSTOMER(1, "客户"),

    /**
     * 客服
     */
    AGENT(2, "客服"),

    /**
     * 系统
     */
    SYSTEM(3, "系统"),

    /**
     * AI机器人
     */
    AI_BOT(4, "AI机器人");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (SenderType type : values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return "";
    }
}
