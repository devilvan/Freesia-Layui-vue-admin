package com.freesia.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.vo.SysClientVo;
import com.freesia.dto.SysClientDto;
import com.freesia.service.SysClientService;
import com.freesia.converter.SysClientConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 控制器
 * @date 2026-03-13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysClientController")
@Tag(name = "SysClientController", description = "系统用户授权表 控制器")
public class SysClientController extends BaseController {
    private final SysClientService sysClientService;
    private final SysClientConverter sysClientConverter;

    /**
     * 保存系统用户授权表信息
     *
     * @param sysClientVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统用户授权表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysClientVo sysClientVo) {
        SysClientDto sysClientDto = sysClientConverter.convertVo2Dto(sysClientVo);
        sysClientService.saveUpdate(sysClientDto);
        return R.ok();
    }

    /**
     * 批量保存系统用户授权表信息
     *
     * @param sysClientVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统用户授权表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysClientVo> sysClientVoList) {
        List<SysClientDto> sysClientDtoList = sysClientConverter.convertBatchVo2Dto(sysClientVoList);
        sysClientService.saveUpdateBatch(sysClientDtoList);
        return R.ok();
    }

    /**
     * 查询系统用户授权表分页信息
     *
     * @param sysClientVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询系统用户授权表分页信息")
    @GetMapping(value = "findPageSysClient")
    public TableResult<SysClientDto> findPageSysClient(SysClientVo sysClientVo, PageQuery pageQuery) {
        SysClientDto sysClientDto = sysClientConverter.convertVo2Dto(sysClientVo);
        return sysClientService.findPage(sysClientDto, pageQuery);
    }

    /**
     * 条件查询系统用户授权表
     *
     * @param sysClientVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统用户授权表")
    @GetMapping(value = "findSysClient")
    public R<SysClientDto> findSysClient(SysClientVo sysClientVo) {
        SysClientDto sysClientDto = sysClientConverter.convertVo2Dto(sysClientVo);
        sysClientDto = sysClientService.findOne(sysClientDto);
        return R.ok(sysClientDto);
    }

    /**
    * 条件查询系统用户授权表
    *
    * @param sysClientVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询系统用户授权表")
    @GetMapping(value = "findListSysClient")
    public R<List<SysClientDto>> findListSysClient(SysClientVo sysClientVo) {
        SysClientDto sysClientDto = sysClientConverter.convertVo2Dto(sysClientVo);
        List<SysClientDto> sysClientDtoList = sysClientService.findList(sysClientDto);
        return R.ok(sysClientDtoList);
    }

    /**
     * 删除系统用户授权表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除系统用户授权表")
    @PostMapping(value = "deleteSysClient")
    public R<Void> deleteSysClient(@RequestBody List<Long> idList) {
        sysClientService.deleteBatch(idList);
        return R.ok();
    }
}
