package com.intellidesk.common.core.constant;

/**
 * 通用常量
 *
 * @author IntelliDesk
 */
public interface CommonConstants {

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 400;

    /**
     * 删除标记 - 已删除
     */
    Integer DELETED = 1;

    /**
     * 删除标记 - 未删除
     */
    Integer NOT_DELETED = 0;

    /**
     * 状态 - 正常
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 状态 - 禁用
     */
    Integer STATUS_DISABLED = 0;

    /**
     * 是
     */
    String YES = "Y";

    /**
     * 否
     */
    String NO = "N";

    /**
     * UTF-8 编码
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 编码
     */
    String GBK = "GBK";

    /**
     * 默认日期格式
     */
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认时区
     */
    String DEFAULT_TIMEZONE = "Asia/Shanghai";
}
