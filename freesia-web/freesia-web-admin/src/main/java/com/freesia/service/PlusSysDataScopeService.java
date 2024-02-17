package com.freesia.service;

/**
 * @author Evad.Wu
 * @Description Mybatis Plus 数据权限 业务逻辑接口
 * @date 2023-09-06
 */
public interface PlusSysDataScopeService {
    /**
     * 获取角色自定义权限
     *
     * @param roleId 角色ID
     * @return 部门id组
     */
    String getRoleCustom(Long roleId);

    /**
     * 获取部门及以下权限
     *
     * @param deptId 部门ID
     * @return 部门ID组
     */
    String getDeptAndChild(Long deptId);

}
