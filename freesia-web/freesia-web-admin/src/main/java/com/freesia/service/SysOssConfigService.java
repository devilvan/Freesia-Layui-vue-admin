package com.freesia.service;

import com.freesia.dto.SysOssConfigDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 业务逻辑接口
 * @date 2024-02-28
 */
public interface SysOssConfigService {
    /**
     * 保存OSS配置信息表信息
     *
     * @param sysOssConfigDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysOssConfigDto saveUpdate(SysOssConfigDto sysOssConfigDto);

    /**
     * 批量保存OSS配置信息表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysOssConfigDto> saveUpdateBatch(List<SysOssConfigDto> list);

    /**
     * 查询OSS配置信息表信息
     *
     * @param sysOssConfigDto 查询条件
     * @param pageQuery       分页条件
     * @return 分页信息
     */
    TableResult<SysOssConfigDto> findPageSysOssConfig(SysOssConfigDto sysOssConfigDto, PageQuery pageQuery);

    /**
     * 条件查询OSS配置信息表信息
     *
     * @param sysOssConfigDto 查询条件
     * @return OSS配置信息表信息
     */
    SysOssConfigDto findSysOssConfig(SysOssConfigDto sysOssConfigDto);

    /**
     * 删除OSS配置信息表信息
     *
     * @param idList 主键
     */
    void deleteSysOssConfig(List<Long> idList);

    /**
     * 应用启动初始化OSS对象存储配置
     */
    void loadSysOssConfig();
}
