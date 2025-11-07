package com.intellidesk.chat.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会话编号生成器
 * 格式: CS + 年月日 + 6位自增序号
 * 示例: CS20250107000001
 *
 * @author IntelliDesk
 */
public class ConversationNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static volatile String lastDate = "";

    /**
     * 生成会话编号
     *
     * @return 会话编号
     */
    public static synchronized String generate() {
        String currentDate = LocalDateTime.now().format(DATE_FORMATTER);

        // 如果日期变化，重置计数器
        if (!currentDate.equals(lastDate)) {
            COUNTER.set(0);
            lastDate = currentDate;
        }

        int number = COUNTER.incrementAndGet();
        return String.format("CS%s%06d", currentDate, number);
    }
}
