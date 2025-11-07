package com.intellidesk.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum MessageType {

    /**
     * 文本
     */
    TEXT(1, "文本"),

    /**
     * 图片
     */
    IMAGE(2, "图片"),

    /**
     * 文件
     */
    FILE(3, "文件"),

    /**
     * 语音
     */
    VOICE(4, "语音"),

    /**
     * 视频
     */
    VIDEO(5, "视频"),

    /**
     * 系统消息
     */
    SYSTEM(6, "系统消息");

    private final Integer code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (MessageType type : values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return "";
    }
}
