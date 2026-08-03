package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.constant.MenuPermission;
import com.freesia.converter.SysOssConverter;
import com.freesia.dto.SysOssDto;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssService;
import com.freesia.vo.R;
import com.freesia.vo.SysOssVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 控制器
 * @date 2024-02-27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/sysOssController")
@Tag(name = "SysOssController", description = "OSS对象存储表 控制器")
public class SysOssController extends BaseController {
    private final SysOssService sysOssService;
    private final SysOssConverter sysOssConverter;

    /**
     * 保存OSS对象存储表信息
     *
     * @param sysOssVo 待保存对象
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存OSS对象存储表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysOssVo sysOssVo) {
        SysOssDto sysOssDto = sysOssConverter.convertVo2Dto(sysOssVo);
        sysOssService.saveUpdate(sysOssDto);
        return R.ok();
    }

    /**
     * 批量保存OSS对象存储表信息
     * <p>
     * sysOssVoList    待保存对象
     *
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存OSS对象存储表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysOssVo> sysOssVoList) {
        List<SysOssDto> sysOssDtoList = sysOssConverter.convertBatchVo2Dto(sysOssVoList);
        sysOssService.saveUpdateBatch(sysOssDtoList);
        return R.ok();
    }

    /**
     * 查询OSS对象存储表分页信息
     *
     * @param sysOssVo  查询条件
     * @param pageQuery 分页条件
     * @return 形式返回
     */
    @SaCheckPermission(value = MenuPermission.SYSTEM_OSS_INDEX)
    @Operation(summary = "查询OSS对象存储表分页信息")
    @GetMapping(value = "findPageSysOss")
    public TableResult<SysOssDto> findPageSysOss(SysOssVo sysOssVo, PageQuery pageQuery) {
        SysOssDto sysOssDto = sysOssConverter.convertVo2Dto(sysOssVo);
        return sysOssService.findPage(sysOssDto, pageQuery);
    }

    /**
     * 条件查询OSS对象存储表
     *
     * @param sysOssVo 查询条件
     * @return 形式返回
     */
    @SaCheckPermission(value = MenuPermission.SYSTEM_OSS_INDEX)
    @Operation(summary = "条件查询OSS对象存储表")
    @GetMapping(value = "findSysOss")
    public R<SysOssDto> findSysOss(SysOssVo sysOssVo) {
        SysOssDto sysOssDto = sysOssConverter.convertVo2Dto(sysOssVo);
        SysOssDto tableResult = sysOssService.findOne(sysOssDto);
        return R.ok(tableResult);
    }

    /**
     * 删除OSS对象存储表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_OSS_DELETE)
    @Operation(summary = "删除OSS对象存储表")
    @PostMapping(value = "deleteSysOss")
    public R<Void> deleteSysOss(@RequestBody List<Long> idList) {
        sysOssService.deleteBatch(idList);
        return R.ok();
    }

    /**
     * 上传文件
     *
     * @param files 上传的文件
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "上传文件")
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<List<SysOssDto>> upload(@RequestParam("file[]") List<MultipartFile> files) {
        List<SysOssDto> sysOssDtoList = sysOssService.upload(files);
        return R.ok(sysOssDtoList);
    }

    /**
     * 批量上传临时文件
     *
     * @param fileList 上传的文件
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "批量上传文件")
    @PostMapping(value = "uploadTemp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<List<SysOssDto>> uploadTemp(@RequestPart("file[]") List<MultipartFile> fileList) {
        List<SysOssDto> sysOssDtoList = sysOssService.uploadTemp(fileList);
        return R.ok(sysOssDtoList);
    }

    /**
     * 下载文件
     *
     * @param id 文件ID
     */
    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_OSS_DOWNLOAD)
    @Operation(summary = "下载文件")
    @GetMapping(value = "download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        sysOssService.download(Long.parseLong(id), response);
    }

}
