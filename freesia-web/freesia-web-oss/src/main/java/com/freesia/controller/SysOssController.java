package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.dto.SysOssDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssService;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import com.freesia.vo.SysOssVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
public class SysOssController {
    private final SysOssService sysOssService;

    /**
     * 保存OSS对象存储表信息
     *
     * @param sysOssVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存OSS对象存储表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysOssVo sysOssVo) {
        SysOssDto sysOssDto = UCopy.copyVo2Dto(sysOssVo, SysOssDto.class);
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
    @Operation(summary = "保存OSS对象存储表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysOssVo> sysOssVoList) {
        List<SysOssDto> sysOssDtoList = UCopy.fullCopyList(sysOssVoList, SysOssDto.class);
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
    @Operation(summary = "查询OSS对象存储表分页信息")
    @GetMapping(value = "findPageSysOss")
    public TableResult<SysOssDto> findPageSysOss(SysOssVo sysOssVo, PageQuery pageQuery) {
        SysOssDto sysOssDto = UCopy.copyVo2Dto(sysOssVo, SysOssDto.class);
        return sysOssService.findPageSysOss(sysOssDto, pageQuery);
    }

    /**
     * 条件查询OSS对象存储表
     *
     * @param sysOssVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询OSS对象存储表")
    @GetMapping(value = "findSysOss")
    public R<SysOssDto> findSysOss(SysOssVo sysOssVo) {
        SysOssDto sysOssDto = UCopy.copyVo2Dto(sysOssVo, SysOssDto.class);
        SysOssDto tableResult = sysOssService.findSysOss(sysOssDto);
        return R.ok(tableResult);
    }

    /**
     * 删除OSS对象存储表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除OSS对象存储表")
    @PostMapping(value = "deleteSysOss")
    public R<Void> deleteSysOss(@RequestBody List<Long> idList) {
        sysOssService.deleteSysOss(idList);
        return R.ok();
    }

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 形式返回
     */
    @SaIgnore
    @Operation(summary = "上传文件")
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysOssDto> upload(@RequestPart("file") MultipartFile file) {
        SysOssDto sysOssDto = sysOssService.upload(file);
        return R.ok(sysOssDto);
    }

    /**
     * 下载文件
     *
     * @param id 文件ID
     */
    @SaIgnore
    @Operation(summary = "下载文件")
    @GetMapping(value = "download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        sysOssService.download(Long.parseLong(id), response);
    }
}
