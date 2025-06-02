package com.freesia.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 日期操作 工具类
 * @date 2022-08-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCalendar {
    private static final Calendar CALENDAR = Calendar.getInstance();

    /**
     * 获取日历实例
     *
     * @return 日历实例
     */
    public static Calendar getInstance() {
        return CALENDAR;
    }

    /**
     * 构建输入的日期所在的当月日期数据（[开始时间, 结束时间]）
     *
     * @param date 日期
     * @return 日期所在的当月日期数据（[开始时间, 结束时间]）
     */
    public static Date[] buildCurrentYearDateRange(Date date) {
        CALENDAR.setTime(date);
        // 获取本月第一天
        CALENDAR.set(Calendar.MONTH, Calendar.JANUARY);
        CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);
        CALENDAR.set(Calendar.MILLISECOND, 0);
        Date startDate = CALENDAR.getTime();
        // 获取本月最后一天
        CALENDAR.set(Calendar.MONTH, Calendar.DECEMBER);
        CALENDAR.set(Calendar.DAY_OF_MONTH, 31);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 23);
        CALENDAR.set(Calendar.MINUTE, 59);
        CALENDAR.set(Calendar.SECOND, 59);
        CALENDAR.set(Calendar.MILLISECOND, 999);
        Date endDate = CALENDAR.getTime();
        return new Date[]{startDate, endDate};
    }

    /**
     * 构建输入的日期所在的当月日期数据（[开始时间, 结束时间]）
     *
     * @param date 日期
     * @return 日期所在的当月日期数据（[开始时间, 结束时间]）
     */
    public static Date[] buildCurrentMonthDateRange(Date date) {
        CALENDAR.setTime(date);
        // 获取本月第一天
        CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);
        CALENDAR.set(Calendar.MILLISECOND, 0);
        Date startDate = CALENDAR.getTime();
        // 获取本月最后一天
        CALENDAR.set(Calendar.DAY_OF_MONTH, CALENDAR.getActualMaximum(Calendar.DAY_OF_MONTH));
        CALENDAR.set(Calendar.HOUR_OF_DAY, 23);
        CALENDAR.set(Calendar.MINUTE, 59);
        CALENDAR.set(Calendar.SECOND, 59);
        CALENDAR.set(Calendar.MILLISECOND, 999);
        Date endDate = CALENDAR.getTime();
        return new Date[]{startDate, endDate};
    }

    /**
     * 构建输入的日期所在的当周日期数据（[开始时间, 结束时间]）
     *
     * @param date 日期
     * @return 日期所在的当周日期数据（[开始时间, 结束时间]）
     */
    public static Date[] buildCurrentWeekDateRange(Date date) {
        // 使用Calendar类操作日期
        CALENDAR.setTime(date);
        // 设置一周的第一天为周一（根据需求可以改为SUNDAY）
        CALENDAR.setFirstDayOfWeek(Calendar.MONDAY);
        // 获取本周的周一
        CALENDAR.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);
        CALENDAR.set(Calendar.MILLISECOND, 0);
        Date startOfWeek = CALENDAR.getTime();
        // 获取本周的周日
        CALENDAR.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 23);
        CALENDAR.set(Calendar.MINUTE, 59);
        CALENDAR.set(Calendar.SECOND, 59);
        CALENDAR.set(Calendar.MILLISECOND, 999);
        Date endOfWeek = CALENDAR.getTime();
        return new Date[]{startOfWeek, endOfWeek};
    }

    /**
     * 对输入的日期增加/减少天数
     *
     * @param date 日期
     * @param day  天数（正数返回未来日期、负数返回过去日期）
     * @return 修改后的日期
     */
    public static Date addDay(Date date, int day) {
        if (ObjectUtil.isNull(date) || day == 0) {
            return date;
        }
        CALENDAR.setTime(date);
        CALENDAR.set(Calendar.DAY_OF_YEAR, day);
        return CALENDAR.getTime();
    }

    /**
     * 校验前端传入的时间格式是否合法
     *
     * @param from 从
     * @param to   到
     * @return flag
     */
    public static boolean checkValidDate(Date from, Date to) {
        if (ObjectUtil.isNull(from) || ObjectUtil.isNull(to)) {
            return false;
        }
        // 如果to的日期在from以前，则false
        return !to.before(from);
    }

    /**
     * 判断输入的日期是否超过范围
     *
     * @param date     待判断的时间
     * @param pastDays 日期范围
     * @return 是-超过 否-未超过
     */
    public static boolean isPassDay(Date date, int pastDays) {
        CALENDAR.setTime(date);
        CALENDAR.add(Calendar.DAY_OF_MONTH, pastDays);
        Date passDay = CALENDAR.getTime();
        return new Date().after(passDay);
    }
}
