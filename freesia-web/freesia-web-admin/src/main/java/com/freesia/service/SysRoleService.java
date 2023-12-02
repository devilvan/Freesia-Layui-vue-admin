package com.freesia.service;

import com.freesia.dto.SysRoleDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.po.SysRolePo;
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
    SysRolePo saveUpdate(SysRoleDto sysRoleDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysRolePo> saveUpdateBatch(List<SysRoleDto> list);

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

}
