package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysConfigDto;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysConfigService;
import com.freesia.util.UCopy;
import com.freesia.util.UMessage;
import com.freesia.vo.R;
import com.freesia.vo.SysConfigVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 控制器
 * @date 2023-09-23
 */
@Valid
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysConfigController")
@Tag(name = "SysConfigController", description = "全局配置信息表 控制器")
public class SysConfigController extends BaseController {
    private final SysConfigService sysConfigService;

    @SaCheckPermission(value = MenuPermission.SYSTEM_CONFIG_INDEX)
    @Operation(summary = "获取参数配置分页")
    @GetMapping(value = "findPageSysConfig")
    public TableResult<SysConfigDto> findPageSysConfig(SysConfigVo sysConfigVo, PageQuery pageQuery) {
        SysConfigDto sysConfigDto = UCopy.copyVo2Dto(sysConfigVo, SysConfigDto.class);
        return sysConfigService.findPageSysConfig(sysConfigDto, pageQuery);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_CONFIG_INDEX)
    @Operation(summary = "根据系统配置键获取配置值")
    @GetMapping(value = "findConfigByKey")
    public R<String> findPageSysConfig(@RequestParam String configKey) {
        String configByKey = sysConfigService.findConfigByKey(configKey);
        return R.ok(configByKey);
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_CONFIG_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_CONFIG_EDIT),
    })
    @Operation(summary = "保存系统配置信息")
    @PostMapping(value = "saveConfig")
    public R<Void> saveConfig(@RequestBody SysConfigVo sysConfigVo) {
        SysConfigDto sysConfigDto = UCopy.copyVo2Dto(sysConfigVo, SysConfigDto.class);
        sysConfigService.saveConfig(sysConfigDto);
        return R.ok();
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_CONFIG_DELETE)
    @Operation(summary = "删除系统配置信息")
    @DeleteMapping(value = "deleteConfig")
    public R<Void> deleteConfig(@RequestParam String configKey) {
        SysConfigDto sysConfigDto = sysConfigService.findSysConfigByConfigKey(configKey);
        if (ObjectUtil.isNotNull(sysConfigDto)) {
            if (sysConfigDto.getBuildIn()) {
                R<Void> r = R.failed();
                r.setMsg(UMessage.message("buildIn.config.delete.failed"));
                return r;
            }
        }
        sysConfigService.deleteConfig(configKey);
        return R.ok();
    }
}
