package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 返回值枚举类
 * @date 2022-07-16
 */
@Getter
@AllArgsConstructor
@SuppressWarnings(value = "unused")
public enum ResultCode {
    /**
     * success-1
     */
    SUCCESS_1(true, 1),
    /**
     * falied-0
     */
    FAILED_O(false, 0),
    /**
     * failed_1
     */
    FAILED_1(false, 1),
    /**
     * falied-0
     */
    SUCCES_O(true, 0),
    /**
     * success-200
     */
    SUCCESS_200(true, 200),
    /**
     * failed-400
     */
    FAILED_400(false, 400),
    /**
     * failed-500
     */
    FAILED_500(false, 500),
    ;

    private final boolean success;
    private final Integer code;
}
