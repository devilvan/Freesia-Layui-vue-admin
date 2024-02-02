package com.freesia.controller;

import com.freesia.dto.SysTenantDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.vo.SysTenantVo;
import com.freesia.service.SysTenantService;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 控制器
 * @date 2024-01-31
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysTenantController")
@Tag(name = "SysTenantController", description = "租户信息表 控制器")
public class SysTenantController {
    private final SysTenantService sysTenantService;

    /**
     * 保存租户信息表信息
     *
     * @return 形式返回
     */
    @Operation(summary = "保存租户信息表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysTenantVo sysTenantVo) {
        SysTenantDto sysTenantDto = UCopy.copyVo2Dto(sysTenantVo, SysTenantDto.class);
        sysTenantService.saveUpdate(sysTenantDto);
        return R.ok();
    }

    /**
     * 批量保存租户信息表信息
     *
     * @return 形式返回
     */
    @Operation(summary = "保存租户信息表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysTenantVo> sysTenantVoList) {
        List<SysTenantDto> sysTenantDtoList = UCopy.fullCopyList(sysTenantVoList, SysTenantDto.class);
        sysTenantService.saveUpdateBatch(sysTenantDtoList);
        return R.ok();
    }

    /**
     * 查询租户信息表分页信息
     *
     * @param sysTenantVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询租户信息表分页信息")
    @GetMapping(value = "findPageSysTenant")
    public TableResult<SysTenantDto> findPageSysTenant(SysTenantVo sysTenantVo, PageQuery pageQuery) {
        SysTenantDto sysTenantDto = UCopy.copyVo2Dto(sysTenantVo, SysTenantDto.class);
        return sysTenantService.findPageSysTenant(sysTenantDto, pageQuery);
    }

    /**
     * 条件查询租户信息表
     *
     * @param sysTenantVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询租户信息表")
    @GetMapping(value = "findSysTenant")
    public R<SysTenantDto> findSysTenant(SysTenantVo sysTenantVo) {
        SysTenantDto sysTenantDto = UCopy.copyVo2Dto(sysTenantVo, SysTenantDto.class);
        SysTenantDto tableResult = sysTenantService.findSysTenant(sysTenantDto);
        return R.ok(tableResult);
    }

    /**
     * 删除租户信息表
     *
     * @param id 主键
     * @return 形式返回
     */
    @Operation(summary = "删除租户信息表")
    @DeleteMapping(value = "deleteSysTenant")
    public R<Void> deleteSysTenant(Long id) {
        sysTenantService.deleteSysTenant(id);
        return R.ok();
    }
}
