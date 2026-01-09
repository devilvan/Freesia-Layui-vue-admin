package com.freesia.service;


import com.freesia.dto.SysSensitiveLogDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 业务逻辑接口
 * @date 2023-08-13
 */
public interface SysSensitiveLogService {
    /**
     * 保存
     *
     * @param sysSensitiveLogDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysSensitiveLogDto saveUpdate(SysSensitiveLogDto sysSensitiveLogDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysSensitiveLogDto> saveUpdateBatch(List<SysSensitiveLogDto> list);

    /**
     * 查询登录日志分页数据
     *
     * @param sysSensitiveLogDto 查询参数
     * @param pageQuery          分页参数
     * @return 分页返回
     */
    TableResult<SysSensitiveLogDto> findPageLoginLog(SysSensitiveLogDto sysSensitiveLogDto, PageQuery pageQuery);

    /**
     * 查询操作日志分页数据
     *
     * @param sysSensitiveLogDto 查询参数
     * @param pageQuery          分页参数
     * @return 分页返回
     */
    TableResult<SysSensitiveLogDto> findPageOptionLog(SysSensitiveLogDto sysSensitiveLogDto, PageQuery pageQuery);
}
