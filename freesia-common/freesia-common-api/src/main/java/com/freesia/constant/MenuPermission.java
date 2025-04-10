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
    public static final String SYSTEM_OPTION_INDEX = "system:option:index",
            PAGE_DOC_INDEX = "page:doc:index";

    /* 用户管理*/
    public static final String SYSTEM_USER_INDEX = "system:user:index",
            SYSTEM_USER_ADD = "system:user:add",
            SYSTEM_USER_EDIT = "system:user:edit",
            SYSTEM_USER_DELETE = "system:user:delete",
            SYSTEM_USER_ASSIGN_ROLE = "system:user:assignRole",
            SYSTEM_USER_ASSIGN_DEPT = "system:user:assignDept",
            SYSTEM_USER_IMPORT_USER = "system:user:importUser";

    /* 菜单管理*/
    public static final String SYSTEM_MENU_INDEX = "system:menu:index",
            SYSTEM_MENU_ADD_DIR = "system:menu:addDir",
            SYSTEM_MENU_ADD_MENU = "system:menu:addMenu",
            SYSTEM_MENU_ADD_BUTTON = "system:menu:addButton",
            SYSTEM_MENU_ADD_LINK = "system:menu:addLink",
            SYSTEM_MENU_EDIT = "system:menu:edit",
            SYSTEM_MENU_DELETE = "system:menu:delete";

    /* 角色管理*/
    public static final String SYSTEM_ROLE_INDEX = "system:role:index",
            SYSTEM_ROLE_MENU_EDIT = "system:role:menu:edit",
            SYSTEM_ROLE_ASSIGN_USER_EDIT = "system:role:assignUser:edit",
            SYSTEM_ROLE_ASSIGN_BUTTON_EDIT = "system:role:assignButton:edit",
            SYSTEM_ROLE_ASSIGN_DEPT = "system:role:assignDept",
            SYSTEM_ROLE_ADD = "system:role:add",
            SYSTEM_ROLE_EDIT = "system:role:edit",
            SYSTEM_ROLE_DELETE = "system:role:delete";

    /* 部门管理*/
    public static final String SYSTEM_DEPT_INDEX = "system:dept:index",
            SYSTEM_DEPT_ADD = "system:dept:add",
            SYSTEM_DEPT_EDIT = "system:dept:edit",
            SYSTEM_DEPT_ENABLED = "system:dept:enabled",
            SYSTEM_DEPT_DELETE = "system:dept:delete",
            SYSTEM_DEPT_ASSIGN_ROLE = "system:dept:assignRole";

    /* 字典管理*/
    public static final String SYSTEM_DICT_INDEX = "system:dict:index",
            SYSTEM_DICT_KEY_ADD = "system:dict:key:add",
            SYSTEM_DICT_KEY_EDIT = "system:dict:key:edit",
            SYSTEM_DICT_KEY_DELETE = "system:dict:key:delete",
            SYSTEM_DICT_VALUE_ADD = "system:dict:value:add",
            SYSTEM_DICT_VALUE_EDIT = "system:dict:value:edit",
            SYSTEM_DICT_VALUE_DELETE = "system:dict:value:delete",
            SYSTEM_DICT_VALUE_ENABLED = "system:dict:value:enabled",
            SYSTEM_DICT_VALUE_FLUSH_CACHE = "system:dict:value:flushCache",
            SYSTEM_DICT_VALUE_IMPORT = "system:dict:value:import";

    /* 租户管理*/
    public static final String SYSTEM_TENANT_INDEX = "system:tenant:index",
            SYSTEM_TENANT_ADD = "system:tenant:add",
            SYSTEM_TENANT_EDIT = "system:tenant:edit",
            SYSTEM_TENANT_DELETE = "system:tenant:delete",
            SYSTEM_TENANT_ASSIGN_USER = "system:tenant:assignUser";

    /* 文件管理*/
    public static final String SYSTEM_OSS_INDEX = "system:oss:index",
            SYSTEM_OSS_DELETE = "system:oss:delete",
            SYSTEM_OSS_DOWNLOAD = "system:oss:download";

    /* URL配置*/
    public static final String COMMON_URL_INDEX = "common:url:index",
            COMMON_URL_ADD = "common:url:add",
            COMMON_URL_EDIT = "common:url:edit",
            COMMON_URL_DELETE = "common:url:delete";

    /* 系统配置管理*/
    public static final String SYSTEM_CONFIG_INDEX = "system:config:index",
            SYSTEM_CONFIG_ADD = "system:config:add",
            SYSTEM_CONFIG_EDIT = "system:config:edit",
            SYSTEM_CONFIG_DELETE = "system:config:delete";
    /* 通用图标管理*/
    public static final String COMMON_ICON_INDEX = "common:icon:index",
            COMMON_ICON_ADD = "common:icon:add",
            COMMON_ICON_EDIT = "common:icon:edit",
            COMMON_ICON_DELETE = "common:icon:delete",
            COMMON_ICON_UPLOAD_BATCH = "common:icon:uploadBatch",
            COMMON_ICON_TEMPLATE_HEADER_INDEX = "common:iconTemplate:index",
            COMMON_ICON_TEMPLATE_HEADER_ADD = "common:iconTemplate:add",
            COMMON_ICON_TEMPLATE_HEADER_EDIT = "common:iconTemplate:edit",
            COMMON_ICON_TEMPLATE_HEADER_DELETE = "common:iconTemplate:delete";
}
