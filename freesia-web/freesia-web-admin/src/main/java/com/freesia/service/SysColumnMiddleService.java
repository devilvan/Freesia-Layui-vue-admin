package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SysColumnMiddleDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 业务逻辑接口
 * @date 2026-03-20
 */
public interface SysColumnMiddleService {
    /**
     * 保存系统列中间表信息
     *
     * @param sysColumnMiddleDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysColumnMiddleDto saveUpdate(SysColumnMiddleDto sysColumnMiddleDto);

    /**
     * 批量保存系统列中间表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysColumnMiddleDto> saveUpdateBatch(List<SysColumnMiddleDto> list);

    /**
     * 查询系统列中间表信息
     *
     * @param sysColumnMiddleDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysColumnMiddleDto> findPage(SysColumnMiddleDto sysColumnMiddleDto, PageQuery pageQuery);

    /**
     * 条件查询系统列中间表信息
     *
     * @param sysColumnMiddleDto 查询条件
     * @return 系统列中间表信息
     */
    SysColumnMiddleDto findOne(SysColumnMiddleDto sysColumnMiddleDto);

    /**
     * 条件查询系统列中间表信息
     *
     * @param sysColumnMiddleDto 查询条件
     * @return 系统列中间表信息
     */
    List<SysColumnMiddleDto> findList(SysColumnMiddleDto sysColumnMiddleDto);

    /**
     * 删除系统列中间表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
