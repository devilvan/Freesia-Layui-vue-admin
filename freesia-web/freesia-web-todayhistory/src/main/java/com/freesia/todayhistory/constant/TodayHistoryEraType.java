package com.freesia.todayhistory.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 大事记时代分类。
 */
@Getter
@AllArgsConstructor
public enum TodayHistoryEraType {
    PRE_19TH("PRE_19TH", "19世纪以前"),
    NINETEENTH("NINETEENTH", "19世纪"),
    TWENTIETH("TWENTIETH", "20世纪"),
    TWENTY_FIRST("TWENTY_FIRST", "21世纪"),
    NONE("NONE", "无");

    private final String code;
    private final String label;

    public static TodayHistoryEraType fromCode(String code) {
        if (code == null) {
            return NONE;
        }
        return Arrays.stream(values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(NONE);
    }

    public static TodayHistoryEraType fromHeading(String heading) {
        if (heading == null) {
            return NONE;
        }
        String normalized = heading.replace(" ", "");
        if (normalized.contains("前19世纪") || normalized.contains("18世纪以前") || normalized.contains("18世紀以前") || normalized.contains("19世纪以前") || normalized.contains("19世紀以前") || normalized.contains("公元前")) {
            return PRE_19TH;
        }
        if (normalized.contains("21世纪") || normalized.contains("21世紀")) {
            return TWENTY_FIRST;
        }
        if (normalized.contains("20世纪") || normalized.contains("20世紀")) {
            return TWENTIETH;
        }
        if (normalized.contains("19世纪") || normalized.contains("19世紀")) {
            return NINETEENTH;
        }
        return NONE;
    }

    public static TodayHistoryEraType fromYear(Integer year) {
        if (year == null) {
            return NONE;
        }
        if (year <= 1800) {
            return PRE_19TH;
        }
        if (year <= 1900) {
            return NINETEENTH;
        }
        if (year <= 2000) {
            return TWENTIETH;
        }
        return TWENTY_FIRST;
    }
}
