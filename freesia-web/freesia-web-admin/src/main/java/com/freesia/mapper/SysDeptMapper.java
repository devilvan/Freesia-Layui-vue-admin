package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.annotation.DataColumn;
import com.freesia.annotation.DataPermission;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.entity.FindTreeDeptSelectEntity;
import com.freesia.po.SysDeptPo;
import com.freesia.pojo.TableResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 部门信息表 持久层
 * @date 2023-08-12
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDeptPo> {
    /**
     * 查询部门列表
     *
     * @param wrapper 构建的SQL
     * @return 部门列表
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "D.ID"),
            @DataColumn(key = "userName", value = "U.ID"),
    })
    List<FindPageSysDeptListEntity> findPageSysDeptList(@Param(Constants.WRAPPER) Wrapper<SysDeptPo> wrapper);

    /**
     * 查询部门列表分页数据
     *
     * @param page    分页参数
     * @param wrapper 构建的SQL
     * @return 部门列表分页数据
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "D.ID"),
    })
    TableResult<FindPageSysDeptListEntity> findPageSysDeptList(@Param("page") Page<SysDeptPo> page, @Param(Constants.WRAPPER) Wrapper<SysDeptPo> wrapper);

    /**
     * 查询部门树下拉框集合
     *
     * @param wrapper 构建的SQL
     * @return 部门树下拉框集合
     */
    List<FindTreeDeptSelectEntity> findTreeDeptSelect(@Param(Constants.WRAPPER) Wrapper<SysDeptPo> wrapper);

    /**
     * 查询自增排序号
     *
     * @param parentId 上级部门ID
     * @return 期望的自增排序号
     */
    Long findMaxOrderNum(@Param("parentId") Long parentId);
}
