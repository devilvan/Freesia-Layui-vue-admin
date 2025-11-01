package com.freesia.worldclock.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author Evad.Wu
 * @Description 时间转换 工具类
 * @date 2025-10-31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeZoneConverter {
    /**
     * 将UTC时间转换为指定时区的当地时间
     */
    public static LocalTime utcToLocalTime(LocalTime utcTime, LocalDate date, String timezone) {
        try {
            // 创建UTC时间的ZonedDateTime
            ZonedDateTime utcZoned = ZonedDateTime.of(date, utcTime, ZoneOffset.UTC);

            // 转换为目标时区
            ZonedDateTime localZoned = utcZoned.withZoneSameInstant(ZoneId.of(timezone));

            return localZoned.toLocalTime();
        } catch (Exception e) {
            System.err.println("时区转换错误: " + e.getMessage());
            return utcTime; // 如果转换失败，返回原始时间
        }
    }

    /**
     * 计算当地时间的日长（基于转换后的日出日落时间）
     */
    public static int calculateLocalDayLength(LocalTime sunriseLocal, LocalTime sunsetLocal) {
        if (sunriseLocal != null && sunsetLocal != null) {
            return (int) java.time.Duration.between(sunriseLocal, sunsetLocal).toMinutes();
        }
        return 0;
    }

    /**
     * 格式化时间为易读字符串
     */
    public static String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * 格式化日长
     */
    public static String formatDayLength(int minutes) {
        int hours = minutes / 60;
        int min = minutes % 60;
        return String.format("%d小时%d分钟", hours, min);
    }
}
