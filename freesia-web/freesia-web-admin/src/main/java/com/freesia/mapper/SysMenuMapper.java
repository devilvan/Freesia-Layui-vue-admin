package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.MenuType;
import com.freesia.entity.FindMenuListByUserIdEntity;
import com.freesia.entity.FindTreeMenuSelectEntity;
import com.freesia.po.SysMenuPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 持久层
 * @date 2023-08-12
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuPo> {
    /**
     * 根据用户ID查询对应的菜单权限
     *
     * @param id 用户ID
     * @return 菜单权限集合
     */
    Set<String> findMenuPermissionByUserId(@Param("id") Long id);

    /**
     * 管理员可访问所有目录与菜单
     *
     * @return 目录与菜单集合
     */
    default List<SysMenuPo> findAllDirAndMenu() {
        LambdaQueryWrapper<SysMenuPo> query = new LambdaQueryWrapper<SysMenuPo>()
                .in(SysMenuPo::getMenuType, MenuType.DIR.getType(), MenuType.MENU.getType())
                .eq(SysMenuPo::getStatus, FlagConstant.ENABLED)
                .orderByAsc(SysMenuPo::getParentId)
                .orderByAsc(SysMenuPo::getOrderNum);
        return this.selectList(query);
    }

    /**
     * 查询该用户可访问所有目录与菜单
     *
     * @param userId 用户ID
     * @return 该用户可访问所有目录与菜单
     */
    List<SysMenuPo> findDirAndMenuByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单下拉树
     *
     * @param wrapper 构建查询条件SQL
     * @return 菜单下拉树
     */
    List<SysMenuPo> findAllMenuTree(@Param(Constants.WRAPPER) Wrapper<SysMenuPo> wrapper);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 该角色已选择的菜单列表
     */
    List<Long> findSelectedMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询管理员可用的所有菜单ID
     *
     * @return 管理员可用的所有菜单ID
     */
    List<Long> findAdminMenuList();

    /**
     * 根据用户ID查询菜单列表
     *
     * @param wrapper 构造的SQL
     * @return 菜单列表
     */
    List<FindMenuListByUserIdEntity> findMenuListByUserId(@Param(Constants.WRAPPER) Wrapper<SysMenuPo> wrapper);

    /**
     * 查询菜单树下拉框集合
     *
     * @param wrapper 构造的SQL
     * @return 菜单树下拉框集合
     */
    List<FindTreeMenuSelectEntity> findTreeMenuSelect(@Param(Constants.WRAPPER) Wrapper<SysMenuPo> wrapper);

    /**
     * 查询菜单中对应path是否已经被使用
     *
     * @param sysMenuPo 菜单信息
     * @return 菜单中对应path是否已经被使用
     */
    boolean findMenuPathExist(@Param("sysMenuPo") SysMenuPo sysMenuPo);
}
