package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysConfigDto;
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
public class SysConfigController {
    private final SysConfigService sysConfigService;

    @SaIgnore
    @Operation(summary = "查询验证码启用状态")
    @GetMapping(value = "findCaptchaEnabled")
    public R<Boolean> findCaptchaEnabled() {
        boolean captchaEnabled = sysConfigService.findCaptchaEnabled();
        return R.ok(captchaEnabled);
    }

    @Operation(summary = "获取参数配置分页")
    @GetMapping(value = "findPageSysConfig")
    public TableResult<SysConfigDto> findPageSysConfig(SysConfigVo sysConfigVo, PageQuery pageQuery) {
        SysConfigDto sysConfigDto = UCopy.copyVo2Dto(sysConfigVo, SysConfigDto.class);
        return sysConfigService.findPageSysConfig(sysConfigDto, pageQuery);
    }

    @Operation(summary = "保存系统配置信息")
    @PostMapping(value = "saveConfig")
    public R<Void> saveConfig(@RequestBody SysConfigVo sysConfigVo) {
        SysConfigDto sysConfigDto = UCopy.copyVo2Dto(sysConfigVo, SysConfigDto.class);
        sysConfigService.saveConfig(sysConfigDto);
        return R.ok();
    }

    @Operation(summary = "删除系统配置信息")
    @DeleteMapping(value = "deleteConfig")
    public R<Void> deleteConfig(@RequestParam String configKey) {
        SysConfigDto sysConfigDto = sysConfigService.findSysConfigByConfigKey(configKey);
        if (ObjectUtil.isNotNull(sysConfigDto)) {
            if (sysConfigDto.getConfigType()) {
                R<Void> r = R.failed();
                r.setMsg(UMessage.message("sys.build-in.config.delete.failed"));
                return r;
            }
        }
        sysConfigService.deleteConfig(configKey);
        return R.ok();
    }
}
