package com.freesia.service;

import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.po.SysDeptPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

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
    SysDeptPo saveUpdate(SysDeptDto sysDeptDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysDeptPo> saveUpdateBatch(List<SysDeptDto> list);

    /**
     * 查询部门列表
     *
     * @param sysDeptDto 查询参数
     * @return 部门列表
     */
    List<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptDto sysDeptDto);

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
}
