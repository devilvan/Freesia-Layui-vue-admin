package com.freesia.controller;

import com.freesia.annotation.Idempotent;
import com.freesia.dto.SysOssConfigDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssConfigService;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import com.freesia.vo.SysOssConfigVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 控制器
 * @date 2024-02-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/sysOssConfigController")
@Tag(name = "SysOssConfigController", description = "OSS配置信息表 控制器")
public class SysOssConfigController {
    private final SysOssConfigService sysOssConfigService;

    /**
     * 保存OSS配置信息表信息
     *
     * @param sysOssConfigVo 待保存对象
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存OSS配置信息表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysOssConfigVo sysOssConfigVo) {
        SysOssConfigDto sysOssConfigDto = UCopy.copyVo2Dto(sysOssConfigVo, SysOssConfigDto.class);
        sysOssConfigService.saveUpdate(sysOssConfigDto);
        return R.ok();
    }

    /**
     * 批量保存OSS配置信息表信息
     * <p>
     * sysOssConfigVoList    待保存对象
     *
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存OSS配置信息表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysOssConfigVo> sysOssConfigVoList) {
        List<SysOssConfigDto> sysOssConfigDtoList = UCopy.fullCopyList(sysOssConfigVoList, SysOssConfigDto.class);
        sysOssConfigService.saveUpdateBatch(sysOssConfigDtoList);
        return R.ok();
    }

    /**
     * 查询OSS配置信息表分页信息
     *
     * @param sysOssConfigVo 查询条件
     * @param pageQuery      分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询OSS配置信息表分页信息")
    @GetMapping(value = "findPageSysOssConfig")
    public TableResult<SysOssConfigDto> findPageSysOssConfig(SysOssConfigVo sysOssConfigVo, PageQuery pageQuery) {
        SysOssConfigDto sysOssConfigDto = UCopy.copyVo2Dto(sysOssConfigVo, SysOssConfigDto.class);
        return sysOssConfigService.findPageSysOssConfig(sysOssConfigDto, pageQuery);
    }

    /**
     * 条件查询OSS配置信息表
     *
     * @param sysOssConfigVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询OSS配置信息表")
    @GetMapping(value = "findSysOssConfig")
    public R<SysOssConfigDto> findSysOssConfig(SysOssConfigVo sysOssConfigVo) {
        SysOssConfigDto sysOssConfigDto = UCopy.copyVo2Dto(sysOssConfigVo, SysOssConfigDto.class);
        SysOssConfigDto tableResult = sysOssConfigService.findSysOssConfig(sysOssConfigDto);
        return R.ok(tableResult);
    }

    /**
     * 删除OSS配置信息表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "删除OSS配置信息表")
    @PostMapping(value = "deleteSysOssConfig")
    public R<Void> deleteSysOssConfig(@RequestBody List<Long> idList) {
        sysOssConfigService.deleteSysOssConfig(idList);
        return R.ok();
    }
}
