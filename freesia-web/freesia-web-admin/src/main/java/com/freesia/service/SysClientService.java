package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SysClientDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 业务逻辑接口
 * @date 2026-03-13
 */
public interface SysClientService {
    /**
     * 保存系统用户授权表信息
     *
     * @param sysClientDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysClientDto saveUpdate(SysClientDto sysClientDto);

    /**
     * 批量保存系统用户授权表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysClientDto> saveUpdateBatch(List<SysClientDto> list);

    /**
     * 查询系统用户授权表信息
     *
     * @param sysClientDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysClientDto> findPage(SysClientDto sysClientDto, PageQuery pageQuery);

    /**
     * 条件查询系统用户授权表信息
     *
     * @param sysClientDto 查询条件
     * @return 系统用户授权表信息
     */
    SysClientDto findOne(SysClientDto sysClientDto);

    /**
     * 条件查询系统用户授权表信息
     *
     * @param sysClientDto 查询条件
     * @return 系统用户授权表信息
     */
    List<SysClientDto> findList(SysClientDto sysClientDto);

    /**
     * 删除系统用户授权表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
