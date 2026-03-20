package com.freesia.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.vo.SysColumnMiddleVo;
import com.freesia.dto.SysColumnMiddleDto;
import com.freesia.service.SysColumnMiddleService;
import com.freesia.converter.SysColumnMiddleConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 控制器
 * @date 2026-03-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysColumnMiddleController")
@Tag(name = "SysColumnMiddleController", description = "系统列中间表 控制器")
public class SysColumnMiddleController extends BaseController {
    private final SysColumnMiddleService sysColumnMiddleService;
    private final SysColumnMiddleConverter sysColumnMiddleConverter;

    /**
     * 保存系统列中间表信息
     *
     * @param sysColumnMiddleVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列中间表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysColumnMiddleVo sysColumnMiddleVo) {
        SysColumnMiddleDto sysColumnMiddleDto = sysColumnMiddleConverter.convertVo2Dto(sysColumnMiddleVo);
        sysColumnMiddleService.saveUpdate(sysColumnMiddleDto);
        return R.ok();
    }

    /**
     * 批量保存系统列中间表信息
     *
     * @param sysColumnMiddleVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列中间表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysColumnMiddleVo> sysColumnMiddleVoList) {
        List<SysColumnMiddleDto> sysColumnMiddleDtoList = sysColumnMiddleConverter.convertBatchVo2Dto(sysColumnMiddleVoList);
        sysColumnMiddleService.saveUpdateBatch(sysColumnMiddleDtoList);
        return R.ok();
    }

    /**
     * 查询系统列中间表分页信息
     *
     * @param sysColumnMiddleVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询系统列中间表分页信息")
    @GetMapping(value = "findPageSysColumnMiddle")
    public TableResult<SysColumnMiddleDto> findPageSysColumnMiddle(SysColumnMiddleVo sysColumnMiddleVo, PageQuery pageQuery) {
        SysColumnMiddleDto sysColumnMiddleDto = sysColumnMiddleConverter.convertVo2Dto(sysColumnMiddleVo);
        return sysColumnMiddleService.findPage(sysColumnMiddleDto, pageQuery);
    }

    /**
     * 条件查询系统列中间表
     *
     * @param sysColumnMiddleVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统列中间表")
    @GetMapping(value = "findSysColumnMiddle")
    public R<SysColumnMiddleDto> findSysColumnMiddle(SysColumnMiddleVo sysColumnMiddleVo) {
        SysColumnMiddleDto sysColumnMiddleDto = sysColumnMiddleConverter.convertVo2Dto(sysColumnMiddleVo);
        sysColumnMiddleDto = sysColumnMiddleService.findOne(sysColumnMiddleDto);
        return R.ok(sysColumnMiddleDto);
    }

    /**
    * 条件查询系统列中间表
    *
    * @param sysColumnMiddleVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询系统列中间表")
    @GetMapping(value = "findListSysColumnMiddle")
    public R<List<SysColumnMiddleDto>> findListSysColumnMiddle(SysColumnMiddleVo sysColumnMiddleVo) {
        SysColumnMiddleDto sysColumnMiddleDto = sysColumnMiddleConverter.convertVo2Dto(sysColumnMiddleVo);
        List<SysColumnMiddleDto> sysColumnMiddleDtoList = sysColumnMiddleService.findList(sysColumnMiddleDto);
        return R.ok(sysColumnMiddleDtoList);
    }

    /**
     * 删除系统列中间表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除系统列中间表")
    @PostMapping(value = "deleteSysColumnMiddle")
    public R<Void> deleteSysColumnMiddle(@RequestBody List<Long> idList) {
        sysColumnMiddleService.deleteBatch(idList);
        return R.ok();
    }
}
