package com.freesia.service;

import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindDeptRolesByDeptIdEntity;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.entity.FindTreeDeptSelectEntity;
import com.freesia.model.LoginUserModel;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 部门信息表 业务逻辑接口
 * @date 2023-08-17
 */
public interface SysDeptService {
    /**
     * 保存
     *
     * @param sysDeptDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysDeptDto saveUpdate(SysDeptDto sysDeptDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysDeptDto> saveUpdateBatch(List<SysDeptDto> list);

    /**
     * 查询部门列表
     *
     * @param sysDeptDto 查询参数
     * @return 部门列表
     */
    List<FindPageSysDeptListEntity> findListSysDept(SysDeptDto sysDeptDto);

    /**
     * 查询部门列表分页数据
     *
     * @param sysDeptDto 查询参数
     * @param pageQuery  分页参数
     * @return 部门列表分页数据
     */
    TableResult<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptDto sysDeptDto, PageQuery pageQuery);

    /**
     * 查询部门下拉树
     *
     * @param sysDeptDto 查询条件
     * @return 部门下拉树
     */
    List<FindPageSysDeptListEntity> findDeptTreeList(SysDeptDto sysDeptDto);

    /**
     * 根据ID查询部门
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDeptDto findDeptById(Long deptId);

    /**
     * 删除部门信息
     *
     * @param sysDeptDto 部门信息
     * @return 删除的部门信息
     */
    SysDeptDto deleteDept(SysDeptDto sysDeptDto);

    /**
     * 查询部门树下拉框集合
     *
     * @param loginUserModel 用户信息
     * @return 部门树下拉框集合
     */
    List<FindTreeDeptSelectEntity> findTreeDeptSelect(LoginUserModel loginUserModel);

    /**
     * 保存部门信息
     *
     * @param sysDeptDto 入参
     * @return 保存后的部门信息
     */
    SysDeptDto saveDept(SysDeptDto sysDeptDto);

    /**
     * 查询自增排序号
     *
     * @param sysDeptDto 入参
     * @return 期望的自增排序号
     */
    Long findIncrementOrderNum(SysDeptDto sysDeptDto);

    /**
     * 查询部门树下拉框集合（分配部门）
     *
     * @param loginUserModel 用户信息
     * @return 部门树下拉框集合
     */
    List<FindTreeDeptSelectEntity> findTreeAssignDeptSelect(LoginUserModel loginUserModel);

    /**
     * 给部门分配角色
     *
     * @param deptId         部门ID
     * @param afterRoleIdSet 角色ID集合
     */
    void assignRole(Long deptId, Set<Long> afterRoleIdSet);

    /**
     * 根据部门ID查询【分配角色】加载数据
     *
     * @param deptId 部门ID
     * @return 部门与已分配角色的信息
     */
    FindDeptRolesByDeptIdEntity findDeptRolesByDeptId(Long deptId);
}
