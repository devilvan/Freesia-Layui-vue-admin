package com.freesia.worldclock.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Bliss.Wu
 * @Description 日出日落计算 工具类
 * @date 2025-10-31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SunriseSunsetCalculatorUtil {
    /**
     * 计算指定日期和位置的日出日落时间
     * 使用简化版的天文学算法
     */
    public static SunriseSunsetResult calculateSunriseSunset(
            BigDecimal latitude, BigDecimal longitude, LocalDate date, String timezone) {

        // 转换为时区时间
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime zonedDate = date.atStartOfDay(zoneId);

        // 计算儒略日
        double julianDate = calculateJulianDate(zonedDate);

        // 计算太阳位置
        double solarNoon = calculateSolarNoon(julianDate, longitude.doubleValue());
        double declination = calculateSolarDeclination(julianDate);

        // 计算日出日落时间（时角）
        double hourAngle = calculateHourAngle(latitude.doubleValue(), declination);

        // 转换为当地时间
        double sunrise = solarNoon - hourAngle;
        double sunset = solarNoon + hourAngle;

        // 转换为时间对象
        LocalTime sunriseTime = decimalToLocalTime(sunrise);
        LocalTime sunsetTime = decimalToLocalTime(sunset);

        return new SunriseSunsetResult(sunriseTime, sunsetTime);
    }

    /**
     * 计算儒略日
     */
    private static double calculateJulianDate(ZonedDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();

        if (month <= 2) {
            year -= 1;
            month += 12;
        }

        int a = year / 100;
        int b = 2 - a + a / 4;

        return Math.floor(365.25 * (year + 4716)) +
                Math.floor(30.6001 * (month + 1)) +
                day + b - 1524.5;
    }

    /**
     * 计算太阳正午时间
     */
    private static double calculateSolarNoon(double julianDate, double longitude) {
        // 简化计算，实际应用可能需要更复杂的算法
        double n = julianDate - 2451545.0 + 0.0008;
        double jStar = n - longitude / 360.0;
        double m = (357.5291 + 0.98560028 * jStar) % 360;
        double c = 1.9148 * Math.sin(Math.toRadians(m)) +
                0.0200 * Math.sin(Math.toRadians(2 * m)) +
                0.0003 * Math.sin(Math.toRadians(3 * m));
        double lambda = (m + c + 180 + 102.9372) % 360;
        double jTransit = 2451545.0 + jStar + 0.0053 * Math.sin(Math.toRadians(m)) -
                0.0069 * Math.sin(Math.toRadians(2 * lambda));

        return (jTransit - Math.floor(jTransit)) * 24;
    }

    /**
     * 计算太阳赤纬
     */
    private static double calculateSolarDeclination(double julianDate) {
        double n = julianDate - 2451545.0;
        double L = (280.460 + 0.9856474 * n) % 360;
        double g = (357.528 + 0.9856003 * n) % 360;

        double lambda = L + 1.915 * Math.sin(Math.toRadians(g)) +
                0.020 * Math.sin(Math.toRadians(2 * g));

        double epsilon = 23.439 - 0.0000004 * n;

        return Math.toDegrees(Math.asin(
                Math.sin(Math.toRadians(epsilon)) * Math.sin(Math.toRadians(lambda))
        ));
    }

    /**
     * 计算时角
     */
    private static double calculateHourAngle(double latitude, double declination) {
        double latRad = Math.toRadians(latitude);
        double decRad = Math.toRadians(declination);

        double hourAngle = Math.acos(
                (Math.cos(Math.toRadians(90.833)) / (Math.cos(latRad) * Math.cos(decRad))) -
                        Math.tan(latRad) * Math.tan(decRad)
        );

        return Math.toDegrees(hourAngle) / 15;
    }

    /**
     * 将小数时间转换为LocalTime
     */
    private static LocalTime decimalToLocalTime(double decimalHours) {
        int hours = (int) Math.floor(decimalHours);
        double decimalMinutes = (decimalHours - hours) * 60;
        int minutes = (int) Math.floor(decimalMinutes);
        int seconds = (int) Math.floor((decimalMinutes - minutes) * 60);

        // 处理负数或超过24小时的情况
        if (hours < 0) hours += 24;
        if (hours >= 24) hours -= 24;

        return LocalTime.of(hours, minutes, seconds);
    }

    public static class SunriseSunsetResult {
        private final LocalTime sunrise;
        private final LocalTime sunset;

        public SunriseSunsetResult(LocalTime sunrise, LocalTime sunset) {
            this.sunrise = sunrise;
            this.sunset = sunset;
        }

        public LocalTime getSunrise() {
            return sunrise;
        }

        public LocalTime getSunset() {
            return sunset;
        }
    }
}
