package com.freesia.service;


import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindPageSysUserByDeptEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.po.SysUserPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

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
    SysUserPo saveUpdate(SysUserDto sysUserDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysUserPo> saveUpdateBatch(List<SysUserDto> list);

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
}
