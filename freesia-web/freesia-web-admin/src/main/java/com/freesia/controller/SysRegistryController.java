package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.SysConfigConstant;
import com.freesia.dto.RegisterDto;
import com.freesia.dto.SysConfigDto;
import com.freesia.dto.SysUserDto;
import com.freesia.po.SysConfigPo;
import com.freesia.service.SysConfigService;
import com.freesia.service.SysRegisterService;
import com.freesia.util.UCopy;
import com.freesia.util.UMessage;
import com.freesia.vo.R;
import com.freesia.vo.RegisterVo;
import com.freesia.vo.SysUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evad.Wu
 * @Description 注册功能 控制器
 * @date 2023-08-22
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysRegistryController")
@Tag(name = "SysRegistryController", description = "注册功能 控制器")
public class SysRegistryController extends BaseController {
    private final SysRegisterService sysRegisterService;
    private final SysConfigService sysConfigService;

    @SaIgnore
    @PostMapping("register")
    @Operation(summary = "用户注册功能")
    public R<Void> register(@Validated @RequestBody SysUserVo sysUserVo) {
        SysConfigDto sysConfigDto = sysConfigService.findConfigByKey(SysConfigConstant.SYS_ACCOUNT_REGISTER_USER);
        String flag = sysConfigDto.getConfigValue();
        if (!FlagConstant.TRUE.equals(flag)) {
            return R.ok(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("sys.register.disabled"));
        }
        SysUserDto sysUserDto = new SysUserDto();
        UCopy.fullCopy(sysUserVo, sysUserDto);
        sysRegisterService.register(sysUserDto);
        return R.ok();
    }
}
