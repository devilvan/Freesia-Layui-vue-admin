package com.freesia.service;

import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindDeptRolesByRoleIdEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 角色信息表 业务逻辑接口
 * @date 2023-08-17
 */
public interface SysRoleService {
    /**
     * 保存
     *
     * @param sysRoleDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysRoleDto saveUpdate(SysRoleDto sysRoleDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysRoleDto> saveUpdateBatch(List<SysRoleDto> list);

    /**
     * 根据用户ID查询对应的角色权限
     *
     * @param id 用户ID
     * @return 角色权限集合
     */
    Set<String> findRolePermissionStrByUserId(@Param("id") Long id);

    /**
     * 查询角色列表分页数据
     *
     * @param sysRoleDto 查询参数
     * @param pageQuery  分页参数
     * @return 角色列表分页数据
     */
    TableResult<FindPageSysRoleListEntity> findPageSysRoleList(SysRoleDto sysRoleDto, PageQuery pageQuery);


    /**
     * 保存角色对应的菜单信息
     *
     * @param menuIdList 选中菜单ID
     * @param roleId     角色ID
     * @param dataScope  数据范围
     */
    void saveRoleMenuPrivilege(List<Long> menuIdList, Long roleId, String dataScope);

    /**
     * 查询所有角色
     *
     * @return 所有角色集合
     */
    List<FindAllRolesEntity> findAllRoles();

    /**
     * 查询用户信息和已分配该角色的用户列表
     *
     * @param sysRoleDto 查询信息
     * @param pageQuery  分页信息
     * @return 分页数据
     */
    TableResult<SysUserDto> findPageUserByRoleId(SysRoleDto sysRoleDto, PageQuery pageQuery);

    /**
     * 条件查询角色
     *
     * @param sysRoleDto 角色ID
     * @return 角色
     */
    SysRoleDto findOne(SysRoleDto sysRoleDto);

    /**
     * 查询未分配该角色的用户列表
     *
     * @param sysRoleDto 查询信息
     * @param pageQuery  分页信息
     * @return 分页数据
     */
    TableResult<SysUserDto> findPageAllowAssignUserByRoleId(SysRoleDto sysRoleDto, PageQuery pageQuery);

    /**
     * 分配角色
     *
     * @param roleId     角色ID
     * @param userIdList 用户列表
     */
    void assignUser(Long roleId, List<Long> userIdList);

    /**
     * 取消分配角色
     *
     * @param roleId     角色ID
     * @param userIdList 用户列表
     */
    void cancelAssignUser(Long roleId, List<Long> userIdList);

    /**
     * 给角色分配部门
     *
     * @param deptIdSet 待分配的部门ID集合
     * @param roleId    角色ID
     */
    void assignDept(Long roleId, Set<Long> deptIdSet);

    /**
     * 根据角色ID查询【分配部门】加载数据
     *
     * @param roleId 角色ID
     * @return 角色与已分配部门的信息
     */
    FindDeptRolesByRoleIdEntity findDeptRolesByRoleId(Long roleId);

    /**
     * 删除角色
     *
     * @param sysRoleDto 入参
     */
    void deleteRole(SysRoleDto sysRoleDto);

    /**
     * 初始化默认角色
     */
    void buildInitDefaultSysRole();

    /**
     * 查询默认角色
     *
     * @return 默认角色
     */
    SysRoleDto findCacheDefaultRole();

    /**
     * 初始化角色菜单权限
     *
     * @param roleId 角色ID
     */
    void saveInitRoleMenu(Long roleId);
}
