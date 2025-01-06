package com.freesia.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.CalendarUtil;
import com.freesia.constant.Constants;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.util.UString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Evad.Wu
 * @Description Controller的通用父类
 * @date 2025-01-03
 */
@Data
@Schema(description = "Controller的通用父类")
public class BaseController {
    /**
     * 构造默认时间范围
     *
     * @param days 间隔天数
     * @return [时间范围从, 时间范围到]
     */
    @SneakyThrows
    public Date[] defaultDateRange(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException(UMessage.message("field.days.invalid"));
        }
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 999);
        Calendar before = (Calendar) now.clone();
        before.set(Calendar.HOUR_OF_DAY, 0);
        before.set(Calendar.MINUTE, 0);
        before.set(Calendar.SECOND, 0);
        before.set(Calendar.MILLISECOND, 0);
        before.add(Calendar.DAY_OF_YEAR, -days);
        Date[] dates = new Date[2];
        dates[0] = before.getTime();
        dates[1] = now.getTime();
        return dates;
    }

    /**
     * 解析查询条件中时间范围的字段
     *
     * @param dateRageStr （字符串）时间范围字段
     * @return [时间范围从, 时间范围到]
     */
    public Date[] parseDateRange(String dateRageStr) {
        return parseDateRange(dateRageStr, UString.SEPARATOR);
    }

    /**
     * 解析查询条件中时间范围的字段
     *
     * @param dateRageStr （字符串）时间范围字段
     * @param separator   间隔符
     * @return [时间范围从, 时间范围到]
     */
    public Date[] parseDateRange(String dateRageStr, String separator) {
        return parseDateRange(dateRageStr, separator, Constants.SDF_YMDHMS);
    }

    /**
     * 解析查询条件中时间范围的字段
     *
     * @param dateRageStr （字符串）时间范围字段
     * @param separator   间隔符
     * @param sdf         日期格式化器
     * @return [时间范围从, 时间范围到]
     */
    @SneakyThrows
    public Date[] parseDateRange(String dateRageStr, String separator, SimpleDateFormat sdf) {
        if (UEmpty.isEmpty(dateRageStr)) {
            throw new IllegalArgumentException(UMessage.message("field.dateRange.not.empty"));
        }
        String[] range = Optional.ofNullable(dateRageStr)
                .map(dateRange -> dateRange.split(Convert.toStr(separator, UString.SEPARATOR)))
                .orElseGet(() -> new String[0]);
        if (range.length != 2) {
            throw new IllegalArgumentException(UMessage.message("dateRange.invalid"));
        }
        String dateFromStr = range[0];
        String dateToStr = range[1];
        if (UEmpty.isEmpty(dateFromStr)) {
            throw new IllegalArgumentException(UMessage.message("field.dateRangeFrom.not.empty"));
        }
        if (UEmpty.isEmpty(dateToStr)) {
            throw new IllegalArgumentException(UMessage.message("field.dateRangeTo.not.empty"));
        }
        Date dateFrom = sdf.parse(dateFromStr);
        Date dateTo = sdf.parse(dateToStr);
        Date[] dates = new Date[2];
        dates[0] = dateFrom;
        dates[1] = dateTo;
        return dates;
    }
}
