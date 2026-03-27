package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SysColumnDetailDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 业务逻辑接口
 * @date 2026-03-27
 */
public interface SysColumnDetailService {
    /**
     * 保存系统列明细表信息
     *
     * @param sysColumnDetailDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysColumnDetailDto saveUpdate(SysColumnDetailDto sysColumnDetailDto);

    /**
     * 批量保存系统列明细表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysColumnDetailDto> saveUpdateBatch(List<SysColumnDetailDto> list);

    /**
     * 查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @param pageQuery          分页条件
     * @return 分页信息
     */
    TableResult<SysColumnDetailDto> findPage(SysColumnDetailDto sysColumnDetailDto, PageQuery pageQuery);

    /**
     * 条件查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @return 系统列明细表信息
     */
    SysColumnDetailDto findOne(SysColumnDetailDto sysColumnDetailDto);

    /**
     * 条件查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @return 系统列明细表信息
     */
    List<SysColumnDetailDto> findList(SysColumnDetailDto sysColumnDetailDto);

    /**
     * （缓存查询）条件查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @return 系统列明细表信息
     */
    List<SysColumnDetailDto> findCacheList(SysColumnDetailDto sysColumnDetailDto);

    /**
     * 删除系统列明细表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);

    /**
     * 根据中间表过滤后的明细
     *
     * @param sysColumnDetailDto 查询条件
     * @return 结果集
     */
    List<SysColumnDetailDto> findMiddleList(SysColumnDetailDto sysColumnDetailDto);
}
