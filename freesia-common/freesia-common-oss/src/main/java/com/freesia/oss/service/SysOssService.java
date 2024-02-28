package com.freesia.oss.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.oss.dto.SysOssDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 业务逻辑接口
 * @date 2024-02-27
 */
public interface SysOssService {
    /**
     * 保存OSS对象存储表信息
     *
     * @param sysOssDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysOssDto saveUpdate(SysOssDto sysOssDto);

    /**
     * 批量保存OSS对象存储表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysOssDto> saveUpdateBatch(List<SysOssDto> list);

    /**
     * 查询OSS对象存储表信息
     *
     * @param sysOssDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysOssDto> findPageSysOss(SysOssDto sysOssDto, PageQuery pageQuery);

    /**
     * 条件查询OSS对象存储表信息
     *
     * @param sysOssDto 查询条件
     * @return OSS对象存储表信息
     */
    SysOssDto findSysOss(SysOssDto sysOssDto);

    /**
     * 删除OSS对象存储表信息
     *
     * @param idList 主键
     */
    void deleteSysOss(List<Long> idList);

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return OSS对象存储实体
     */
    SysOssDto upload(MultipartFile file);
}
