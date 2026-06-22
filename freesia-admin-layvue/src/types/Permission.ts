export enum MenuPermission {
    /* 用户管理*/
    SYSTEM_USER_ADD = "system:user:add",
    SYSTEM_USER_EDIT = "system:user:edit",
    SYSTEM_USER_DELETE = "system:user:delete",
    SYSTEM_USER_INDEX = "system:user:index",
    SYSTEM_USER_ASSIGN_ROLE = "system:user:assignRole",
    SYSTEM_USER_ASSIGN_DEPT = "system:user:assignDept",
    SYSTEM_USER_IMPORT_USER = "system:user:importUser",

    /* 菜单管理*/
    SYSTEM_MENU_INDEX = "system:menu:index",
    SYSTEM_MENU_ADD_DIR = "system:menu:addDir",
    SYSTEM_MENU_ADD_MENU = "system:menu:addMenu",
    SYSTEM_MENU_ADD_BUTTON = "system:menu:addButton",
    SYSTEM_MENU_ADD_LINK = "system:menu:addLink",
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
    SYSTEM_DEPT_ADD = "system:dept:add",
    SYSTEM_DEPT_EDIT = "system:dept:edit",
    SYSTEM_DEPT_ENABLED = "system:dept:enabled",
    SYSTEM_DEPT_DELETE = "system:dept:delete",
    SYSTEM_DEPT_ASSIGN_ROLE = "system:dept:assignRole",

    /* 字典管理*/
    SYSTEM_DICT_INDEX = "system:dict:index",
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

    /* 通用图标管理*/
    COMMON_ICON_INDEX = "common:icon:index",
    COMMON_ICON_ADD = "common:icon:add",
    COMMON_ICON_EDIT = "common:icon:edit",
    COMMON_ICON_DELETE = "common:icon:delete",
    COMMON_ICON_UPLOAD_BATCH = "common:icon:uploadBatch",
    COMMON_ICON_TEMPLATE_HEADER_INDEX = "common:iconTemplate:index",
    COMMON_ICON_TEMPLATE_HEADER_ADD = "common:iconTemplate:add",
    COMMON_ICON_TEMPLATE_HEADER_EDIT = "common:iconTemplate:edit",
    COMMON_ICON_TEMPLATE_HEADER_DELETE = "common:iconTemplate:delete",
    COMMON_ICON_TEMPLATE_HEADER_SETUP_DETAIL = "common:iconTemplate:setupDetail",
    COMMON_ICON_TEMPLATE_DETAIL_ADD_GROUP = "common:iconTemplate:addGrouping",
    COMMON_ICON_TEMPLATE_DETAIL_ADD_ICON = "common:iconTemplate:addIcon",
    COMMON_ICON_TEMPLATE_DETAIL_ADD_MULTI_ICON = "common:iconTemplate:addMultiIcon",
}
