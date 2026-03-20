package com.freesia.controller;

import com.freesia.dto.SysColumnDetailDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysColumnDetailService;
import com.freesia.util.UEmpty;
import com.freesia.vo.SysColumnHeaderVo;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.service.SysColumnHeaderService;
import com.freesia.converter.SysColumnHeaderConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列头表 控制器
 * @date 2026-03-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysColumnHeaderController")
@Tag(name = "SysColumnHeaderController", description = "系统列头表 控制器")
public class SysColumnHeaderController extends BaseController {
    private final SysColumnHeaderService sysColumnHeaderService;
    private final SysColumnHeaderConverter sysColumnHeaderConverter;
    private final SysColumnDetailService sysColumnDetailService;

    /**
     * 保存系统列头表信息
     *
     * @param sysColumnHeaderVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列头表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysColumnHeaderVo sysColumnHeaderVo) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        sysColumnHeaderService.saveUpdate(sysColumnHeaderDto);
        return R.ok();
    }

    /**
     * 批量保存系统列头表信息
     *
     * @param sysColumnHeaderVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列头表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysColumnHeaderVo> sysColumnHeaderVoList) {
        List<SysColumnHeaderDto> sysColumnHeaderDtoList = sysColumnHeaderConverter.convertBatchVo2Dto(sysColumnHeaderVoList);
        sysColumnHeaderService.saveUpdateBatch(sysColumnHeaderDtoList);
        return R.ok();
    }

    /**
     * 查询系统列头表分页信息
     *
     * @param sysColumnHeaderVo 查询条件
     * @param pageQuery         分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询系统列头表分页信息")
    @GetMapping(value = "findPageSysColumnHeader")
    public TableResult<SysColumnHeaderDto> findPageSysColumnHeader(SysColumnHeaderVo sysColumnHeaderVo, PageQuery pageQuery) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        return sysColumnHeaderService.findPage(sysColumnHeaderDto, pageQuery);
    }

    /**
     * 条件查询系统列头及明细
     *
     * @param sysColumnHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统列头及明细")
    @GetMapping(value = "findSysColumnHeader")
    public R<SysColumnHeaderDto> findSysColumnHeader(@RequestBody SysColumnHeaderVo sysColumnHeaderVo) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        sysColumnHeaderDto = sysColumnHeaderService.findOne(sysColumnHeaderDto);
        if (sysColumnHeaderDto != null) {
            SysColumnDetailDto sysColumnDetailDto = new SysColumnDetailDto();
            sysColumnDetailDto.setHeaderId(sysColumnHeaderDto.getId());
            List<SysColumnDetailDto> sysColumnDetailDtoList = sysColumnDetailService.findMiddleList(sysColumnDetailDto);
            sysColumnHeaderDto.setSysColumnDetailDtoList(sysColumnDetailDtoList);
        }
        return R.ok(sysColumnHeaderDto);
    }

    /**
     * 条件查询系统列头表
     *
     * @param sysColumnHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统列头表")
    @GetMapping(value = "findListSysColumnHeader")
    public R<List<SysColumnHeaderDto>> findListSysColumnHeader(SysColumnHeaderVo sysColumnHeaderVo) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        List<SysColumnHeaderDto> sysColumnHeaderDtoList = sysColumnHeaderService.findList(sysColumnHeaderDto);
        return R.ok(sysColumnHeaderDtoList);
    }

    /**
     * 删除系统列头表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除系统列头表")
    @PostMapping(value = "deleteSysColumnHeader")
    public R<Void> deleteSysColumnHeader(@RequestBody List<Long> idList) {
        sysColumnHeaderService.deleteBatch(idList);
        return R.ok();
    }
}
