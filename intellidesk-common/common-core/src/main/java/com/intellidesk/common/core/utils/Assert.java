package com.intellidesk.common.core.utils;

import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 * 用于参数校验，校验失败抛出 BusinessException
 *
 * @author IntelliDesk
 */
public class Assert {

    /**
     * 断言为 true
     *
     * @param expression 表达式
     * @param message    错误消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言为 true
     *
     * @param expression 表达式
     * @param resultCode 响应码
     */
    public static void isTrue(boolean expression, ResultCode resultCode) {
        if (!expression) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言为 false
     *
     * @param expression 表达式
     * @param message    错误消息
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言对象不为空
     *
     * @param object  对象
     * @param message 错误消息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言对象不为空
     *
     * @param object     对象
     * @param resultCode 响应码
     */
    public static void notNull(Object object, ResultCode resultCode) {
        if (object == null) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 断言对象为空
     *
     * @param object  对象
     * @param message 错误消息
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言字符串不为空
     *
     * @param text    字符串
     * @param message 错误消息
     */
    public static void notEmpty(String text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言字符串不为空白
     *
     * @param text    字符串
     * @param message 错误消息
     */
    public static void notBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言集合不为空
     *
     * @param collection 集合
     * @param message    错误消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言 Map 不为空
     *
     * @param map     Map
     * @param message 错误消息
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言数组不为空
     *
     * @param array   数组
     * @param message 错误消息
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言两个对象相等
     *
     * @param obj1    对象1
     * @param obj2    对象2
     * @param message 错误消息
     */
    public static void equals(Object obj1, Object obj2, String message) {
        if (!Objects.equals(obj1, obj2)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言两个对象不相等
     *
     * @param obj1    对象1
     * @param obj2    对象2
     * @param message 错误消息
     */
    public static void notEquals(Object obj1, Object obj2, String message) {
        if (Objects.equals(obj1, obj2)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言数字大于 0
     *
     * @param number  数字
     * @param message 错误消息
     */
    public static void isPositive(Number number, String message) {
        if (number == null || number.longValue() <= 0) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言数字大于等于 0
     *
     * @param number  数字
     * @param message 错误消息
     */
    public static void isNonNegative(Number number, String message) {
        if (number == null || number.longValue() < 0) {
            throw new BusinessException(message);
        }
    }
}
