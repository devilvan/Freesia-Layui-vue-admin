package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 前端lay-tale自定义列 枚举
 * @date 2026-03-21
 */
@Getter
@AllArgsConstructor
public enum LayTableColumn {
    /**
     * 用户管理
     */
    USER("User", "用户管理"),
    /**
     * 用户管理-分配角色
     */
    USER_ASSIGN_ROLE("UserAssignRole", "用户管理-分配角色"),
    /**
     * 租户管理
     */
    TENANT("Tenant", "租户管理"),
    /**
     * 租户管理-分配角色
     */
    TENANT_ASSIGN_USER("TenantAssignUser", "租户管理-分配用户"),
    /**
     * 角色管理
     */
    ROLE("Role", "角色管理"),
    /**
     * 角色管理-分配用户
     */
    ROLE_ASSIGN_USER("RoleAssignUser", "角色管理-分配用户"),
    /**
     * 角色管理-分配按钮权限
     */
    ROLE_ASSIGN_BUTTON("RoleAssignButton", "角色管理-分配按钮权限"),
    /**
     * 文件管理
     */
    OSS("Oss", "文件管理"),
    /**
     * 菜单管理
     */
    MENU("Menu", "菜单管理"),
    /**
     * 日志管理-登录日志
     */
    LOGIN("Login", "日志管理-登录日志"),
    /**
     * 日志管理-操作日志
     */
    OPTION("Option", "日志管理-操作日志"),
    /**
     * 字典管理
     */
    DICT("Dict", "字典管理"),
    /**
     * 部门管理
     */
    DEPT("Dept", "部门管理"),
    /**
     * 用户管理-分配角色
     */
    DEPT_ASSIGN_ROLE("DeptAssignRole", "部门管理-分配角色"),
    /**
     * 系统配置管理
     */
    CONFIG("Config", "系统配置管理"),
    /**
     * 系统列头管理
     */
    SYS_COLUMN_HEADER("SysColumnHeader", "系统列头管理"),
    /**
     * 系统列明细管理
     */
    SYS_COLUMN_DETAIL("SysColumnDetail", "系统列明细管理"),
    ;


    /**
     * 编码
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
    }
