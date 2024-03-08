export enum MenuPermission {
    /* 用户管理*/
    SYSTEM_USER_ADD = "system:user:add",
    SYSTEM_USER_EDIT = "system:user:edit",
    SYSTEM_USER_DELETE = "system:user:delete",
    SYSTEM_USER_INDEX = "system:user:index",
    SYSTEM_USER_ASSIGN_ROLE = "system:user:assignRole",
    SYSTEM_USER_IMPORT_USER = "system:user:importUser",

    /* 菜单管理*/
    SYSTEM_MENU_ADD_DIR = "system:menu_addDir",
    SYSTEM_MENU_ADD_MENU = "system:menu_addMenu",
    SYSTEM_MENU_ADD_BUTTON = "system:menu_addButton",
    SYSTEM_MENU_ADD_LINK = "system:menu_addLink",
    SYSTEM_MENU_EDIT = "system:menu:edit",
    SYSTEM_MENU_DELETE = "system:menu:delete",

    /* 角色管理*/
    SYSTEM_ROLE_MENU_EDIT = "system:role:menu:edit",
    SYSTEM_ROLE_ASSIGN_USER_EDIT = "system:role:assignUser:edit",
    SYSTEM_ROLE_ASSIGN_BUTTON_EDIT = "system:role:assignButton:edit",
    SYSTEM_ROLE_EDIT = "system:role:edit",
    SYSTEM_ROLE_DELETE = "system:role:delete",
    SYSTEM_ROLE_ADD = "system:role:add",

    /* 部门管理*/

    /* 字典管理*/
    SYSTEM_DICT_KEY_ADD = "system:dict:key:add",
    SYSTEM_DICT_KEY_EDIT = "system:dict:key:edit",
    SYSTEM_DICT_KEY_DELETE = "system:dict:key:delete",
    SYSTEM_DICT_VALUE_ADD = "system:dict:value:add",
    SYSTEM_DICT_VALUE_EDIT = "system:dict:value:edit",
    SYSTEM_DICT_VALUE_DELETE = "system:dict:value:delete",
    SYSTEM_DICT_VALUE_ENABLED = "system:dict:value:enabled",
    SYSTEM_DICT_VALUE_FLUSH_CACHE = "system:dict:value:flushCache",

    /* 租户管理*/
    SYSTEM_TENANT_ADD = "system:tenant:add",
    SYSTEM_TENANT_EDIT = "system:tenant:edit",
    SYSTEM_TENANT_DELETE = "system:tenant:delete",
    SYSTEM_TENANT_ASSIGN_USER = "system:tenant:assignUser",

    /* 文件管理*/
    SYSTEM_OSS_INDEX = "system:oss:index",
    SYSTEM_OSS_DELETE = "system:oss:delete",
    SYSTEM_OSS_UPLOAD = "system:oss:upload",
    SYSTEM_OSS_DOWNLOAD = "system:oss:download",

    /* URL配置*/
    COMMON_URL_INDEX = "common:url:index",
    COMMON_URL_ADD = "common:url:add",
    COMMON_URL_EDIT = "common:url:edit",
    COMMON_URL_DELETE = "common:url:delete",
}
