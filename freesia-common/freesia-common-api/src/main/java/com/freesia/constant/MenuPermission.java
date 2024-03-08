package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 菜单权限 静态类
 * @date 2023-09-24
 */
@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuPermission {
    public static final String SYSTEM_OPTION_INDEX = "system:option:index";
    public static final String PAGE_DOC_INDEX = "page:doc:index";

    /* 用户管理*/
    public static final String SYSTEM_USER_INDEX = "system:user:index";
    public static final String SYSTEM_USER_ADD = "system:user:add";
    public static final String SYSTEM_USER_EDIT = "system:user:edit";
    public static final String SYSTEM_USER_DELETE = "system:user:delete";
    public static final String SYSTEM_USER_ASSIGN_ROLE = "system:user:assignRole";
    public static final String SYSTEM_USER_IMPORT_USER = "system:user:importUser";

    /* 菜单管理*/
    public static final String SYSTEM_MENU_INDEX = "system:menu:index";
    public static final String SYSTEM_MENU_ADD_DIR = "system:menu:addDir";
    public static final String SYSTEM_MENU_ADD_MENU = "system:menu:addMenu";
    public static final String SYSTEM_MENU_ADD_BUTTON = "system:menu:addButton";
    public static final String SYSTEM_MENU_ADD_LINK = "system:menu:addLink";
    public static final String SYSTEM_MENU_EDIT = "system:menu:edit";
    public static final String SYSTEM_MENU_DELETE = "system:menu:delete";

    /* 角色管理*/
    public static final String SYSTEM_ROLE_INDEX = "system:role:index";
    public static final String SYSTEM_ROLE_MENU_EDIT = "system:role:menu:edit";
    public static final String SYSTEM_ROLE_ASSIGN_USER_EDIT = "system:role:assignUser:edit";
    public static final String SYSTEM_ROLE_ASSIGN_BUTTON_EDIT = "system:role:assignButton:edit";
    public static final String SYSTEM_ROLE_EDIT = "system:role:edit";
    public static final String SYSTEM_ROLE_DELETE = "system:role:delete";
    public static final String SYSTEM_ROLE_ADD = "system:role:add";

    /* 部门管理*/
    public static final String SYSTEM_DEPT_INDEX = "system:dept:index";

    /* 字典管理*/
    public static final String SYSTEM_DICT_INDEX = "system:dict:index";
    public static final String SYSTEM_DICT_KEY_ADD = "system:dict:key:add";
    public static final String SYSTEM_DICT_KEY_EDIT = "system:dict:key:edit";
    public static final String SYSTEM_DICT_KEY_DELETE = "system:dict:key:delete";
    public static final String SYSTEM_DICT_VALUE_ADD = "system:dict:value:add";
    public static final String SYSTEM_DICT_VALUE_EDIT = "system:dict:value:edit";
    public static final String SYSTEM_DICT_VALUE_DELETE = "system:dict:value:delete";
    public static final String SYSTEM_DICT_VALUE_ENABLED = "system:dict:value:enabled";
    public static final String SYSTEM_DICT_VALUE_FLUSH_CACHE = "system:dict:value:flushCache";

    /* 租户管理*/
    public static final String SYSTEM_TENANT_INDEX = "system:tenant:index";
    public static final String SYSTEM_TENANT_ADD = "system:tenant:add";
    public static final String SYSTEM_TENANT_EDIT = "system:tenant:edit";
    public static final String SYSTEM_TENANT_DELETE = "system:tenant:delete";
    public static final String SYSTEM_TENANT_ASSIGN_USER = "system:tenant:assignUser";

    /* 文件管理*/
    public static final String SYSTEM_OSS_INDEX = "system:oss:index";
    public static final String SYSTEM_OSS_DELETE = "system:oss:delete";
    public static final String SYSTEM_OSS_UPLOAD = "system:oss:upload";
    public static final String SYSTEM_OSS_DOWNLOAD = "system:oss:download";

    /* URL配置*/
    public static final String COMMON_URL_INDEX = "common:url:index";
    public static final String COMMON_URL_ADD = "common:url:add";
    public static final String COMMON_URL_EDIT = "common:url:edit";
    public static final String COMMON_URL_DELETE = "common:url:delete";

    /* 系统配置管理*/
    public static final String SYSTEM_CONFIG_INDEX = "system:config:index";
    public static final String SYSTEM_CONFIG_ADD = "system:config:add";
    public static final String SYSTEM_CONFIG_EDIT = "system:config:edit";
    public static final String SYSTEM_CONFIG_DELETE = "system:config:delete";
}
