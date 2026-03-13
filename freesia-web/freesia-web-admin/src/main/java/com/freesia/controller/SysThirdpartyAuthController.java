package com.freesia.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.vo.SysThirdpartyAuthVo;
import com.freesia.dto.SysThirdpartyAuthDto;
import com.freesia.service.SysThirdpartyAuthService;
import com.freesia.converter.SysThirdpartyAuthConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 控制器
 * @date 2026-03-13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysThirdpartyAuthController")
@Tag(name = "SysThirdpartyAuthController", description = "第三方平台授权表 控制器")
public class SysThirdpartyAuthController extends BaseController {
    private final SysThirdpartyAuthService sysThirdpartyAuthService;
    private final SysThirdpartyAuthConverter sysThirdpartyAuthConverter;

    /**
     * 保存第三方平台授权表信息
     *
     * @param sysThirdpartyAuthVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存第三方平台授权表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysThirdpartyAuthVo sysThirdpartyAuthVo) {
        SysThirdpartyAuthDto sysThirdpartyAuthDto = sysThirdpartyAuthConverter.convertVo2Dto(sysThirdpartyAuthVo);
        sysThirdpartyAuthService.saveUpdate(sysThirdpartyAuthDto);
        return R.ok();
    }

    /**
     * 批量保存第三方平台授权表信息
     *
     * @param sysThirdpartyAuthVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存第三方平台授权表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysThirdpartyAuthVo> sysThirdpartyAuthVoList) {
        List<SysThirdpartyAuthDto> sysThirdpartyAuthDtoList = sysThirdpartyAuthConverter.convertBatchVo2Dto(sysThirdpartyAuthVoList);
        sysThirdpartyAuthService.saveUpdateBatch(sysThirdpartyAuthDtoList);
        return R.ok();
    }

    /**
     * 查询第三方平台授权表分页信息
     *
     * @param sysThirdpartyAuthVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询第三方平台授权表分页信息")
    @GetMapping(value = "findPageSysThirdpartyAuth")
    public TableResult<SysThirdpartyAuthDto> findPageSysThirdpartyAuth(SysThirdpartyAuthVo sysThirdpartyAuthVo, PageQuery pageQuery) {
        SysThirdpartyAuthDto sysThirdpartyAuthDto = sysThirdpartyAuthConverter.convertVo2Dto(sysThirdpartyAuthVo);
        return sysThirdpartyAuthService.findPage(sysThirdpartyAuthDto, pageQuery);
    }

    /**
     * 条件查询第三方平台授权表
     *
     * @param sysThirdpartyAuthVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询第三方平台授权表")
    @GetMapping(value = "findSysThirdpartyAuth")
    public R<SysThirdpartyAuthDto> findSysThirdpartyAuth(SysThirdpartyAuthVo sysThirdpartyAuthVo) {
        SysThirdpartyAuthDto sysThirdpartyAuthDto = sysThirdpartyAuthConverter.convertVo2Dto(sysThirdpartyAuthVo);
        sysThirdpartyAuthDto = sysThirdpartyAuthService.findOne(sysThirdpartyAuthDto);
        return R.ok(sysThirdpartyAuthDto);
    }

    /**
    * 条件查询第三方平台授权表
    *
    * @param sysThirdpartyAuthVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询第三方平台授权表")
    @GetMapping(value = "findListSysThirdpartyAuth")
    public R<List<SysThirdpartyAuthDto>> findListSysThirdpartyAuth(SysThirdpartyAuthVo sysThirdpartyAuthVo) {
        SysThirdpartyAuthDto sysThirdpartyAuthDto = sysThirdpartyAuthConverter.convertVo2Dto(sysThirdpartyAuthVo);
        List<SysThirdpartyAuthDto> sysThirdpartyAuthDtoList = sysThirdpartyAuthService.findList(sysThirdpartyAuthDto);
        return R.ok(sysThirdpartyAuthDtoList);
    }

    /**
     * 删除第三方平台授权表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除第三方平台授权表")
    @PostMapping(value = "deleteSysThirdpartyAuth")
    public R<Void> deleteSysThirdpartyAuth(@RequestBody List<Long> idList) {
        sysThirdpartyAuthService.deleteBatch(idList);
        return R.ok();
    }
}
