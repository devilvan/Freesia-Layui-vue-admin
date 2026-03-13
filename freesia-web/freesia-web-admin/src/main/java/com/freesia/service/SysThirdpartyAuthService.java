package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SysThirdpartyAuthDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 业务逻辑接口
 * @date 2026-03-13
 */
public interface SysThirdpartyAuthService {
    /**
     * 保存第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysThirdpartyAuthDto saveUpdate(SysThirdpartyAuthDto sysThirdpartyAuthDto);

    /**
     * 批量保存第三方平台授权表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysThirdpartyAuthDto> saveUpdateBatch(List<SysThirdpartyAuthDto> list);

    /**
     * 查询第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysThirdpartyAuthDto> findPage(SysThirdpartyAuthDto sysThirdpartyAuthDto, PageQuery pageQuery);

    /**
     * 条件查询第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 查询条件
     * @return 第三方平台授权表信息
     */
    SysThirdpartyAuthDto findOne(SysThirdpartyAuthDto sysThirdpartyAuthDto);

    /**
     * 条件查询第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 查询条件
     * @return 第三方平台授权表信息
     */
    List<SysThirdpartyAuthDto> findList(SysThirdpartyAuthDto sysThirdpartyAuthDto);

    /**
     * 删除第三方平台授权表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
