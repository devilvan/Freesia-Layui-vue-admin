package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.annotation.DataColumn;
import com.freesia.annotation.DataPermission;
import com.freesia.entity.FindPageSysUserByDeptEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.po.SysTenantPo;
import com.freesia.po.SysUserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Evad.Wu
 * @Description 用户信息表 持久层
 * @date 2023-08-12
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPo> {
    /**
     * 判断用户是否为管理员
     *
     * @param id 用户ID
     * @return flag
     */
    Boolean isAdmin(@Param("id") Long id);

    /**
     * 验证该用户是否已存在
     *
     * @param sysUserPo 待验证的用户信息
     * @return flag
     */
    boolean checkUserNameUnique(@Param("sysUserPo") SysUserPo sysUserPo);

    /**
     * 查询用户列表分页数据
     *
     * @param page             分页条件
     * @param sysUserPoWrapper 分页参数
     * @return 用户列表的分页对象
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "D.ID"),
            @DataColumn(key = "userName", value = "U.ID")
    })
    Page<FindPageSysUserListEntity> findPageSysUserList(@Param("page") Page<SysUserPo> page, @Param(Constants.WRAPPER) Wrapper<SysUserPo> sysUserPoWrapper);

    /**
     * 获取部门下的用户
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @return 部门下的用户的分页数据
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "D.ID"),
            @DataColumn(key = "userName", value = "U.ID")
    })
    Page<FindPageSysUserByDeptEntity> findPageSysUserByDept(@Param("page") Page<SysUserPo> page, @Param(Constants.WRAPPER) Wrapper<SysUserPo> wrapper);

    /**
     * 查询用户信息
     *
     * @param wrapper 查询条件
     * @return 用户信息
     */
    @DataPermission({
            @DataColumn(key = "userName", value = "U.ID")
    })
    SysUserPo findCurrentUserProfile(@Param(Constants.WRAPPER) Wrapper<SysUserPo> wrapper);

    /**
     * 根据租户ID查询已分配该租户的用户
     *
     * @param tenantId 租户ID
     * @param page     分页信息
     * @return 已分配该租户的用户
     */
    @DataPermission({
            @DataColumn(key = "userName", value = "U.ID")
    })
    Page<SysUserPo> findPageUserByTenantId(@Param("tenantId") Long tenantId, @Param("page") Page<SysUserPo> page);

    /**
     * 根据租户ID查询可分配该租户的用户
     *
     * @param sysTenantPo 租户信息
     * @param page        分页信息
     * @return 可分配该租户的用户
     */
    @DataPermission({
            @DataColumn(key = "userName", value = "U.ID")
    })
    Page<SysUserPo> findPageAllowAssignUserByTenantId(@Param("sysTenantPo") SysTenantPo sysTenantPo, @Param("page") Page<SysUserPo> page);
}
