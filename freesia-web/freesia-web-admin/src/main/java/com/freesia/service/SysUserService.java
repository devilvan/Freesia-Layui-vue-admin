package com.freesia.service;


import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindPageSysUserByDeptEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.entity.FindUserRolesByUserIdEntity;
import com.freesia.po.SysUserPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 用户信息表 业务逻辑接口
 * @date 2023-08-14
 */
public interface SysUserService {
    /**
     * 保存
     *
     * @param sysUserDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysUserDto saveUpdate(SysUserDto sysUserDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysUserDto> saveUpdateBatch(List<SysUserDto> list);

    /**
     * 根据用户名查询用户信息，判断用户是否存在、是否停用
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUserPo findOneByUsername(String username);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUserPo findByUsername(String username);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserDto findUserById(Long userId);

    /**
     * 根据用户ID集合查询用户
     *
     * @param userIdList 用户ID集合
     * @return 用户信息
     */
    List<SysUserDto> findUserByIdList(List<Long> userIdList);

    /**
     * 验证该用户是否已存在
     *
     * @param sysUserDto 待验证的用户信息
     * @return flag
     */
    boolean checkUserNameUnique(SysUserDto sysUserDto);

    /**
     * 保存注册的用户对象
     *
     * @param sysUserDto 用户对象
     * @return flag
     */
    boolean register(SysUserDto sysUserDto);

    /**
     * 查询用户列表分页数据
     *
     * @param sysUserDto 查询条件
     * @param pageQuery  分页参数
     * @return 用户列表的分页对象
     */
    TableResult<FindPageSysUserListEntity> findPageSysUserList(SysUserDto sysUserDto, PageQuery pageQuery);


    /**
     * 获取部门下的用户
     *
     * @param sysUserDto 查询参数
     * @param pageQuery  分页参数
     * @return 部门下的用户的分页数据
     */
    TableResult<FindPageSysUserByDeptEntity> findPageSysUserByDept(SysUserDto sysUserDto, PageQuery pageQuery);

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserDto findCurrentUserProfile(Long userId);

    /**
     * 保存用户信息
     *
     * @param sysUserDto 用户信息
     */
    void saveUserInfo(SysUserDto sysUserDto);

    /**
     * 根据用户ID查询【分配用户】加载数据
     *
     * @param userId 用户ID
     * @return 用户与已分配角色的信息
     */
    FindUserRolesByUserIdEntity findUserRolesByUserId(Long userId);

    /**
     * 给用户分配角色
     *
     * @param userId         被分配角色的用户ID
     * @param afterRoleIdSet 分配后该用户对应的角色ID
     */
    void assignRole(Long userId, Set<Long> afterRoleIdSet);

    /**
     * 根据租户ID查询已分配该租户的用户
     *
     * @param tenantId  租户ID
     * @param pageQuery 分页信息
     * @return 已分配该租户的用户
     */
    TableResult<SysUserDto> findPageUserByTenantId(Long tenantId, PageQuery pageQuery);

    /**
     * 根据租户ID查询可分配该租户的用户
     *
     * @param sysTenantDto 租户信息
     * @param pageQuery    分页信息
     * @return 可分配该租户的用户
     */
    TableResult<SysUserDto> findPageAllowAssignUserByTenantId(SysTenantDto sysTenantDto, PageQuery pageQuery);

    /**
     * 根据用户ID查询是否为管理员
     *
     * @param id 用户ID
     * @return 是否为管理员
     */
    Boolean isAdmin(Long id);

    /**
     * 根据用户名集合查询是否与已存在的用户重合
     *
     * @param distinctUserNameList 用户名集合
     * @return 已存在的用户
     */
    List<SysUserDto> findDistinctUserNameList(List<String> distinctUserNameList);

    /**
     * 删除用户
     *
     * @param idList 用户ID
     * @return 删除用户的信息
     */
    List<SysUserDto> deleteUser(List<Long> idList);

    /**
     * 给用户分配部门
     *
     * @param userIdList 用户ID
     * @param deptId     部门ID
     * @return 形式返回
     */
    Map<String, Object> assignDept(List<Long> userIdList, Long deptId);

    /**
     * 用户头像更新
     *
     * @param avatar 头像地址
     */
    void avatarUpdate(String avatar);

    /**
     * 获取用户列表分页（不过滤数据权限）
     *
     * @param sysUserDto 查询条件
     * @param pageQuery  分页参数
     * @return 用户列表的分页对象
     */
    TableResult<FindPageSysUserListEntity> findPageSysUserWithoutDataScope(SysUserDto sysUserDto, PageQuery pageQuery);

    /**
     * 根据用户ID查询用户列表
     *
     * @param idList id集合
     * @return 用户列表
     */
    List<FindPageSysUserListEntity> findListSysUserById(List<Long> idList);
}
