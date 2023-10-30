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
    private static final Date NOW = new Date();
    private static final Calendar CALENDAR = Calendar.getInstance();

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
        return NOW.after(passDay);
    }
}
