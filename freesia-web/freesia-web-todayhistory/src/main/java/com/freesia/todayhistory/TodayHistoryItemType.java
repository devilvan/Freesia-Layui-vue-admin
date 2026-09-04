package com.freesia.todayhistory;

import java.util.Arrays;

/**
 * 历史上的今天条目类型。
 */
public enum TodayHistoryItemType {
    EVENT("EVENT", "大事记"),
    BIRTH("BIRTH", "出生"),
    DEATH("DEATH", "逝世"),
    HOLIDAY("HOLIDAY", "节假日和习俗"),
    UNKNOWN("UNKNOWN", "未知");

    private final String code;
    private final String label;

    TodayHistoryItemType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static TodayHistoryItemType fromHeading(String heading) {
        if (heading == null) {
            return UNKNOWN;
        }
        String normalized = heading.replace(" ", "");
        if (normalized.contains("出生")) {
            return BIRTH;
        }
        if (normalized.contains("逝世") || normalized.contains("死亡")) {
            return DEATH;
        }
        if (normalized.contains("节假日") || normalized.contains("习俗")) {
            return HOLIDAY;
        }
        if (normalized.contains("大事记") || normalized.contains("事件") || normalized.contains("历史")) {
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

