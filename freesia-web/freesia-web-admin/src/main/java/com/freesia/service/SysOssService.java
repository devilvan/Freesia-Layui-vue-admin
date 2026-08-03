package com.freesia.service;

import com.freesia.dto.SysOssDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * @param pageQuery 分页条件
     * @return 分页信息
     */
    TableResult<SysOssDto> findPage(SysOssDto sysOssDto, PageQuery pageQuery);

    /**
     * 条件查询OSS对象存储表信息
     *
     * @param sysOssDto 查询条件
     * @return OSS对象存储表信息
     */
    SysOssDto findOne(SysOssDto sysOssDto);

    /**
     * 删除OSS对象存储表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);

    /**
     * 上传文件
     *
     * @param files 文件对象
     * @return OSS对象存储实体
     */
    List<SysOssDto> upload(List<MultipartFile> files);

    /**
     * 上传文件到桶下指定目录
     *
     * @param files 文件对象
     * @param dir   目录
     * @return OSS对象存储实体
     */
    List<SysOssDto> upload(List<MultipartFile> files, String dir);

    /**
     * 批量上传临时文件
     *
     * @param fileList 文件对象
     * @return OSS对象存储实体
     */
    List<SysOssDto> uploadTemp(List<MultipartFile> fileList);

    /**
     * 下载文件
     *
     * @param id       文件ID
     * @param response 响应体
     * @throws IOException IO异常
     */
    void download(Long id, HttpServletResponse response) throws IOException;

    /**
     * 根据主键查询缓存
     *
     * @param id 主键
     * @return OSS对象存储实体
     */
    SysOssDto findCacheById(Long id);

    /**
     * 初始化删除临时目录下的文件
     */
    void initDeleteTempFile();
}
