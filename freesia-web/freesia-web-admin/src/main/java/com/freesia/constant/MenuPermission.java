package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 菜单权限 静态类
 * @date 2023-09-24
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuPermission {
    public static final String SYSTEM_USER_INDEX = "system:user:index";
    public static final String SYSTEM_ROLE_INDEX = "system:role:index";
    public static final String SYSTEM_ORGANIZATION_INDEX = "system:organization:index";
    public static final String SYSTEM_MENU_INDEX = "system:menu:index";
    public static final String SYSTEM_DICT_INDEX = "system:dict:index";
    public static final String SYSTEM_LOGIN_INDEX = "system:login:index";
    public static final String SYSTEM_OPTION_INDEX = "system:option:index";
    public static final String SYSTEM_CONFIG_INDEX = "system:config:index";
    public static final String PAGE_DOC_INDEX = "page:doc:index";
    public static final String SYSTEM_ROLE_MENU_EDIT = "system:role:menu:edit";
    /**
     * 给用户分配角色
     */
    public static final String ASSIGN_ROLE = "system:user:role:edit";

}
