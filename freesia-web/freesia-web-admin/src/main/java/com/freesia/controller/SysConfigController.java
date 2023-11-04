package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.dto.SysConfigDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysConfigService;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import com.freesia.vo.SysConfigVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 控制器
 * @date 2023-09-23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysConfigController")
@Tag(name = "SysConfigController", description = "全局配置信息表 控制器")
public class SysConfigController {
    private final SysConfigService sysConfigService;

    @SaIgnore
    @Operation(summary = "查询验证码启用状态")
    @RequestMapping(value = "findCaptchaEnabled")
    public R<Boolean> findCaptchaEnabled() {
        boolean captchaEnabled = sysConfigService.findCaptchaEnabled();
        return R.ok(captchaEnabled);
    }

    @Operation(summary = "获取参数配置分页")
    @RequestMapping(value = "findPageSysConfig")
    public TableResult<SysConfigDto> findPageSysConfig(SysConfigVo sysConfigVo, PageQuery pageQuery) {
        SysConfigDto sysConfigDto = new SysConfigDto();
        UCopy.fullCopy(sysConfigVo, sysConfigDto);
        return sysConfigService.findPageSysConfig(sysConfigDto, pageQuery);
    }
}
