package com.freesia.controller;

import com.freesia.converter.SysColumnDetailConverter;
import com.freesia.dto.SysColumnDetailDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysColumnDetailService;
import com.freesia.vo.R;
import com.freesia.vo.SysColumnDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 控制器
 * @date 2026-03-16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysColumnDetailController")
@Tag(name = "SysColumnDetailController", description = "系统列明细表 控制器")
public class SysColumnDetailController extends BaseController {
    private final SysColumnDetailService sysColumnDetailService;
    private final SysColumnDetailConverter sysColumnDetailConverter;

    /**
     * 保存系统列明细表信息
     *
     * @param sysColumnDetailVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列明细表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysColumnDetailVo sysColumnDetailVo) {
        SysColumnDetailDto sysColumnDetailDto = sysColumnDetailConverter.convertVo2Dto(sysColumnDetailVo);
        sysColumnDetailService.saveUpdate(sysColumnDetailDto);
        return R.ok();
    }

    /**
     * 批量保存系统列明细表信息
     *
     * @param sysColumnDetailVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列明细表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysColumnDetailVo> sysColumnDetailVoList) {
        List<SysColumnDetailDto> sysColumnDetailDtoList = sysColumnDetailConverter.convertBatchVo2Dto(sysColumnDetailVoList);
        sysColumnDetailService.saveUpdateBatch(sysColumnDetailDtoList);
        return R.ok();
    }

    /**
     * 查询系统列明细表分页信息
     *
     * @param sysColumnDetailVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询系统列明细表分页信息")
    @GetMapping(value = "findPageSysColumnDetail")
    public TableResult<SysColumnDetailDto> findPageSysColumnDetail(SysColumnDetailVo sysColumnDetailVo, PageQuery pageQuery) {
        SysColumnDetailDto sysColumnDetailDto = sysColumnDetailConverter.convertVo2Dto(sysColumnDetailVo);
        return sysColumnDetailService.findPage(sysColumnDetailDto, pageQuery);
    }

    /**
     * 条件查询系统列明细表
     *
     * @param sysColumnDetailVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统列明细表")
    @GetMapping(value = "findSysColumnDetail")
    public R<SysColumnDetailDto> findSysColumnDetail(SysColumnDetailVo sysColumnDetailVo) {
        SysColumnDetailDto sysColumnDetailDto = sysColumnDetailConverter.convertVo2Dto(sysColumnDetailVo);
        sysColumnDetailDto = sysColumnDetailService.findOne(sysColumnDetailDto);
        return R.ok(sysColumnDetailDto);
    }

    /**
    * 条件查询系统列明细表
    *
    * @param sysColumnDetailVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询系统列明细表")
    @GetMapping(value = "findListSysColumnDetail")
    public R<List<SysColumnDetailDto>> findListSysColumnDetail(SysColumnDetailVo sysColumnDetailVo) {
        SysColumnDetailDto sysColumnDetailDto = sysColumnDetailConverter.convertVo2Dto(sysColumnDetailVo);
        List<SysColumnDetailDto> sysColumnDetailDtoList = sysColumnDetailService.findList(sysColumnDetailDto);
        return R.ok(sysColumnDetailDtoList);
    }

    /**
     * 删除系统列明细表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除系统列明细表")
    @PostMapping(value = "deleteSysColumnDetail")
    public R<Void> deleteSysColumnDetail(@RequestBody List<Long> idList) {
        sysColumnDetailService.deleteBatch(idList);
        return R.ok();
    }
}
