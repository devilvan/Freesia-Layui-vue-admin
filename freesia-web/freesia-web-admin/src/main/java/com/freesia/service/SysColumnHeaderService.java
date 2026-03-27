package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SysColumnHeaderDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列头表 业务逻辑接口
 * @date 2026-03-27
 */
public interface SysColumnHeaderService {
    /**
     * 保存系统列头表信息
     *
     * @param sysColumnHeaderDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysColumnHeaderDto saveUpdate(SysColumnHeaderDto sysColumnHeaderDto);

    /**
     * 批量保存系统列头表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysColumnHeaderDto> saveUpdateBatch(List<SysColumnHeaderDto> list);

    /**
     * 查询系统列头表信息
     *
     * @param sysColumnHeaderDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysColumnHeaderDto> findPage(SysColumnHeaderDto sysColumnHeaderDto, PageQuery pageQuery);

    /**
     * 条件查询系统列头表信息
     *
     * @param sysColumnHeaderDto 查询条件
     * @return 系统列头表信息
     */
    SysColumnHeaderDto findOne(SysColumnHeaderDto sysColumnHeaderDto);

    /**
     * 条件查询系统列头表信息
     *
     * @param sysColumnHeaderDto 查询条件
     * @return 系统列头表信息
     */
    List<SysColumnHeaderDto> findList(SysColumnHeaderDto sysColumnHeaderDto);

    /**
     * 删除系统列头表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
