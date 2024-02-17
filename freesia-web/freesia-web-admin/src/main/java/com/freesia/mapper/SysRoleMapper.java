package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.annotation.DataColumn;
import com.freesia.annotation.DataPermission;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.po.SysRolePo;
import com.freesia.po.SysUserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 角色信息表 持久层
 * @date 2023-08-12
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRolePo> {
    /**
     * 根据用户ID查询对应的角色权限
     *
     * @param id 用户ID
     * @return 角色权限集合
     */
    Set<SysRolePo> findRolePermissionStrByUserId(@Param("id") Long id);

    /**
     * 查询角色列表分页数据
     *
     * @param page    分页对象
     * @param wrapper 构建的SQL
     * @return 角色列表分页数据
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "D.ID"),
    })
    Page<FindPageSysRoleListEntity> findPageSysRoleList(@Param("page") Page<SysRolePo> page, @Param(Constants.WRAPPER) Wrapper<SysRolePo> wrapper);

    /**
     * 删除目录、菜单、按钮、链接
     *
     * @param idList 菜单ID集合
     */
    void deleteRoleMenu(@Param("idList") List<Long> idList);

    /**
     * 已分配该角色的用户列表
     *
     * @param wrapper   查询条件SQL
     * @param pageQuery 分页信息
     * @return 分页数据
     */
    Page<SysRolePo> findPageUserByRoleId(@Param(Constants.WRAPPER) Wrapper<SysRolePo> wrapper, @Param("page") Page<SysRolePo> pageQuery);

    /**
     * 查询未分配该角色的用户列表
     *
     * @param sysRolePo 查询条件
     * @param pageQuery 分页信息
     * @return 分页数据
     */
    Page<SysUserPo> findPageAllowAssignUserByRoleId(@Param("sysRolePo") SysRolePo sysRolePo, @Param("page") Page<SysRolePo> pageQuery);
}
