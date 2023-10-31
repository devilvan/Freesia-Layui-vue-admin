package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 菜单类型
 * @date 2023-08-11
 */
@Getter
@AllArgsConstructor
public enum MenuType {
    /**
     * 菜单
     */
    DIR("D"),
    /**
     * 目录
     */
    MENU("M"),
    /**
     * 按钮
     */
    BUTTON("B"),
    /**
     * 链接
     */
    LINK("L");

    /**
     * 菜单类型
     */
    private final String type;

    /**
     * 根据菜单类型获取枚举对象
     *
     * @param type 菜单类型
     * @return 枚举对象
     */
    public static MenuType getInstanceByType(String type) {
        MenuType[] values = MenuType.values();
        for (MenuType menuType : values) {
            if (menuType.type.equalsIgnoreCase(type)) {
                return menuType;
            }
        }
        return null;
    }
}
