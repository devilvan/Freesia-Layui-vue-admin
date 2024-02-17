package com.freesia.constant;

import com.freesia.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 用户类型 枚举类
 * @date 2023-08-22
 */
@Getter
@AllArgsConstructor
public enum UserType {
    /**
     * PC端用户
     */
    SYS_USER("sys_user"),
    /**
     * APP端用户
     */
    APP_USER("app_user");

    private final String userType;

    /**
     * 根据Key获取枚举对象
     *
     * @param key 键
     * @return 枚举对象
     */
    public static UserType getInstanceByKey(String key) {
        UserType[] values = UserType.values();
        for (UserType userType : values) {
            if (userType.getUserType().equals(key)) {
                return userType;
            }
        }
        throw new UserException("user.type.not.found", key);
    }
}
