package com.freesia.todayhistory.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 历史上的今天条目类型。
 */
@Getter
@AllArgsConstructor
public enum TodayHistoryItemType {
    EVENT("EVENT", "大事记"),
    BIRTH("BIRTH", "出生"),
    DEATH("DEATH", "逝世"),
    HOLIDAY("HOLIDAY", "节假日和习俗"),
    UNKNOWN("UNKNOWN", "未知");

    private final String code;
    private final String label;

    public static TodayHistoryItemType fromHeading(String heading) {
        if (heading == null) {
            return UNKNOWN;
        }
        String normalized = heading.replace(" ", "");
        if (normalized.contains("出生")) {
            return BIRTH;
        }
        if (normalized.contains("逝世") || normalized.contains("死亡") || normalized.contains("辭世") || normalized.contains("辞世") || normalized.contains("去世")) {
            return DEATH;
        }
        if (normalized.contains("节假日") || normalized.contains("習俗") || normalized.contains("习俗") || normalized.contains("節假日")) {
            return HOLIDAY;
        }
        if (normalized.contains("大事记") || normalized.contains("大事紀") || normalized.contains("事件") || normalized.contains("历史") || normalized.contains("歷史")) {
            return EVENT;
        }
        return UNKNOWN;
    }

    public static TodayHistoryItemType fromCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        return Arrays.stream(values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

