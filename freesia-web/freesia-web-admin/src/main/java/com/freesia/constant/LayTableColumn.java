package com.freesia.constant;

import cn.hutool.core.lang.func.Func;
import com.freesia.dto.SysColumnMiddleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

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
    USER("User", "用户管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "userName", "用户名",
                "nickName", "昵称",
                "avatar", "头像",
                "accountStatus", "状态",
                "deptName", "部门",
                "email", "邮箱",
                "gender", "性别",
                "remark", "备注"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 用户管理-分配角色
     */
    USER_ASSIGN_ROLE("UserAssignRole", "用户管理-分配角色", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "角色名称", "roleName",
                "角色键名", "roleKey",
                "状态", "status",
                "数据范围", "dataScope",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 租户管理
     */
    TENANT("Tenant", "租户管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "租户编码", "code",
                "租户名称", "name",
                "租户类型", "type",
                "租户状态", "status",
                "联系人姓名", "contactName",
                "联系人电话", "contactTel",
                "联系人邮箱", "contactEmail",
                "租户地址", "address",
                "租户备注", "remark",
                "营业时间", "businessHoursFrom"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 租户管理-分配角色
     */
    TENANT_ASSIGN_USER("TenantAssignUser", "租户管理-分配用户", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "用户名称", "userName",
                "用户昵称", "nickName",
                "用户类型", "userType",
                "状态", "accountStatus",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    ROLE("Role", "角色管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "角色名称", "roleName",
                "角色权限编码", "roleKey",
                "数据范围", "dataScope",
                "状态", "status",
                "创建时间", "createTime",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 角色管理-分配用户
     */
    ROLE_ASSIGN_USER("RoleAssignUser", "角色管理-分配用户", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "用户名称", "userName",
                "用户昵称", "nickName",
                "用户类型", "userType",
                "状态", "accountStatus",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 角色管理-分配按钮权限
     */
    ROLE_ASSIGN_BUTTON("RoleAssignButton", "角色管理-分配按钮权限", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "菜单名称", "menuName",
                "排序", "orderNum",
                "菜单类型", "menuType",
                "启用", "status",
                "权限标识", "perms",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 文件管理
     */
    OSS("Oss", "文件管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "文件路径", "fileName",
                "原名", "originalName",
                "预览", "url",
                "文件类型", "fileSuffix",
                "文件大小", "fileSize",
                "服务商", "service",
                "上传人", "creator",
                "上传时间", "createTime"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 菜单管理
     */
    MENU("Menu", "菜单管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "菜单名称", "menuName",
                "路由地址", "path",
                "排序", "orderNum",
                "菜单类型", "menuType",
                "组件路径", "component",
                "权限标识", "perms",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 日志管理-操作日志
     */
    OPTION("Option", "日志管理-操作日志", (headerId) -> {
        Map<String, String> columnTilteNameMap = new HashMap<>(16);
        columnTilteNameMap.put("用户名", "operatorName");
        columnTilteNameMap.put("部门名称", "deptName");
        columnTilteNameMap.put("IP地址", "ipAddress");
        columnTilteNameMap.put("请求地址", "url");
        columnTilteNameMap.put("操作结果", "80px");
        columnTilteNameMap.put("所属模块", "module");
        columnTilteNameMap.put("子模块", "subModule");
        columnTilteNameMap.put("操作类型", "type");
        columnTilteNameMap.put("操作系统", "300px");
        columnTilteNameMap.put("浏览器", "browser");
        columnTilteNameMap.put("操作时间", "operateTime");
        columnTilteNameMap.put("地点", "location");
        columnTilteNameMap.put("备注", "remark");
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 字典管理
     */
    DICT("Dict", "字典管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "字典值名", "valueName",
                "字典值", "value",
                "状态", "status",
                "排序", "orderNum",
                "是否默认", "isDefault",
                "创建时间", "createTime",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 部门管理
     */
    DEPT("Dept", "部门管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "用户名", "userName",
                "用户昵称", "nickName",
                "性别", "gender",
                "部门名称", "deptName",
                "创建时间", "createTime",
                "状态", "accountStatus"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 用户管理-分配角色
     */
    DEPT_ASSIGN_ROLE("DeptAssignRole", "部门管理-分配角色", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "角色名称", "roleName",
                "角色键名", "roleKey",
                "状态", "status",
                "数据范围", "dataScope",
                "备注", "remark"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 系统配置管理
     */
    CONFIG("Config", "系统配置管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "参数名称", "configName",
                "参数键", "configKey",
                "参数值", "configValue",
                "系统内置", "buildIn",
                "创建时间", "createTime"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 系统列头管理
     */
    SYS_COLUMN_HEADER("SysColumnHeader", "系统列头管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = Map.of(
                "组件ID", "componentId",
                "组件名", "name",
                "表格高度", "height",
                "表格最大高度", "maxHeight",
                "初始化分页大小", "initPageSize",
                "是否启用", "enabled",
                "是否允许单元格列宽拖动", "resizeFlag",
                "是否允许根据内容自动计算列宽", "autoColsWidthFlag",
                "是否启用默认工具栏", "defaultToolBarFlag"
        );
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    /**
     * 系统列明细管理
     */
    SYS_COLUMN_DETAIL("SysColumnDetail", "系统列明细管理", (headerId) -> {
        Map<String, String> columnTilteNameMap = new HashMap<>(16);
        columnTilteNameMap.put("用户ID", "userId");
        columnTilteNameMap.put("系统列头ID", "headerId");
        columnTilteNameMap.put("列名", "title");
        columnTilteNameMap.put("是否启用", "enabled");
        columnTilteNameMap.put("是否固定", "fixed");
        columnTilteNameMap.put("是否过长省略", "ellipsisTooltip");
        columnTilteNameMap.put("列宽", "width");
        columnTilteNameMap.put("最小列宽", "minWidth");
        columnTilteNameMap.put("最大列宽", "maxWidth");
        columnTilteNameMap.put("排序号", "orderNum");
        columnTilteNameMap.put("是否排序", "sorted");
        return buildSysColumnMiddleDtoList(headerId, columnTilteNameMap);
    }),
    ;

    private static List<SysColumnMiddleDto> buildSysColumnMiddleDtoList(Long headerId, Map<String, String> columnTilteNameMap) {
        List<SysColumnMiddleDto> sysColumnMiddleDtoList = new ArrayList<>();
        columnTilteNameMap.forEach((key, value) -> {
            SysColumnMiddleDto sysColumnMiddleDto = new SysColumnMiddleDto();
            sysColumnMiddleDto.setHeaderId(headerId);
            sysColumnMiddleDto.setTitle(key);
            sysColumnMiddleDto.setName(value);
            sysColumnMiddleDto.setEnabled(true);
            sysColumnMiddleDtoList.add(sysColumnMiddleDto);
        });
        return sysColumnMiddleDtoList;
    }


    /**
     * 编码
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 系统列中间表实体 函数式接口
     */
    private final Function<Long, List<SysColumnMiddleDto>> function;
}
