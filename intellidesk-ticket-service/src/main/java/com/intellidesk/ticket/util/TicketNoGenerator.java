package com.intellidesk.ticket.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工单编号生成器
 * 格式: TK + 年月日 + 6位自增序号
 * 示例: TK20250107000001
 *
 * @author IntelliDesk
 */
public class TicketNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static volatile String lastDate = "";

    /**
     * 生成工单编号
     *
     * @return 工单编号
     */
    public static synchronized String generate() {
        String currentDate = LocalDateTime.now().format(DATE_FORMATTER);

        // 如果日期变化，重置计数器
        if (!currentDate.equals(lastDate)) {
            COUNTER.set(0);
            lastDate = currentDate;
        }

        int number = COUNTER.incrementAndGet();
        return String.format("TK%s%06d", currentDate, number);
    }
}
