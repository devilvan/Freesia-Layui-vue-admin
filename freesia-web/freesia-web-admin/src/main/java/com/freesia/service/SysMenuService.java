package com.freesia.service;


import com.freesia.dto.RouterDto;
import com.freesia.dto.SysMenuDto;
import com.freesia.entity.FindAllMenuTreeEntity;
import com.freesia.entity.FindMenuListByUserIdEntity;
import com.freesia.entity.FindTreeMenuSelectEntity;
import com.freesia.entity.RouterEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 业务逻辑接口
 * @date 2023-08-17
 */
public interface SysMenuService {
    /**
     * 保存
     *
     * @param sysMenuDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysMenuDto saveUpdate(SysMenuDto sysMenuDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysMenuDto> saveUpdateBatch(List<SysMenuDto> list);

    /**
     * 根据用户ID查询对应的菜单权限
     *
     * @param id 用户ID
     * @return 菜单权限集合
     */
    Set<String> findMenuPermissionByUserId(Long id);

    /**
     * 获取该用户的菜单树信息
     *
     * @param userId 用户ID
     * @return 该用户的菜单树信息
     */
    List<SysMenuDto> findMenuTreeByUserId(Long userId);


    /**
     * 构建前端所需要的路由信息
     *
     * @param sysMenuDtoList 菜单列表
     * @return 前端路由所需要的路由信息
     */
    List<RouterDto> buildRouters(List<SysMenuDto> sysMenuDtoList);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param sysMenuDtoList 菜单列表
     * @return 路由列表
     */
    List<RouterEntity> buildMenus(List<SysMenuDto> sysMenuDtoList);


    /**
     * 查询所有菜单下拉树
     *
     * @param userId 用户ID
     * @return 菜单下拉树
     */
    List<FindAllMenuTreeEntity> findAllMenuTree(Long userId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 该角色已选择的菜单列表
     */
    List<Long> findSelectedMenuListByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param sysMenuDto 查询条件
     * @param userId     用户ID
     * @return 菜单列表
     */
    List<FindMenuListByUserIdEntity> findMenuListByUserId(SysMenuDto sysMenuDto, Long userId);

    /**
     * 查询菜单树下拉框集合
     *
     * @param userId   用户ID
     * @param menuType 菜单类型
     * @return 菜单树下拉框集合
     */
    List<FindTreeMenuSelectEntity> findTreeMenuSelect(Long userId, String menuType);

    /**
     * 根据父菜单ID查询父菜单数据
     *
     * @param parentId 父菜单ID
     * @return 父菜单数据
     */
    SysMenuDto findMenuByParentId(Long parentId);

    /**
     * 保存目录-菜单-按钮信息
     *
     * @param sysMenuDto 待保存的菜单数据
     * @return 保存后的菜单数据
     */
    SysMenuDto saveMenu(SysMenuDto sysMenuDto);

    /**
     * 删除目录、菜单、按钮、链接
     *
     * @param id 菜单ID
     */
    void deleteMenu(Long id);
}
