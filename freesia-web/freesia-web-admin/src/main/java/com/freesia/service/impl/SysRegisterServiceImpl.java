package com.freesia.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.SysModule;
import com.freesia.constant.UserType;
import com.freesia.dto.RegisterDto;
import com.freesia.dto.SysUserDto;
import com.freesia.exception.UserException;
import com.freesia.service.SysConfigService;
import com.freesia.service.SysRegisterService;
import com.freesia.service.SysUserService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 注册功能 业务逻辑实现类
 * @date 2023-08-22
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SysRegisterServiceImpl implements SysRegisterService {
    private final SysConfigService sysConfigService;
    private final SysUserService sysUserService;

    @Override
    public void register(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        String userType = UserType.getInstanceByKey(registerDto.getUserType()).getUserType();
        sysConfigService.validateCaptcha(username, registerDto.getCode(), registerDto.getUuid());
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setUserName(username);
        sysUserDto.setNickName(username);
        sysUserDto.setPassword(BCrypt.hashpw(password));
        sysUserDto.setUserType(userType);
        if (sysUserService.checkUserNameUnique(sysUserDto)) {
            throw new UserException("user.register.not.unique", username);
        }
        boolean flag = sysUserService.register(sysUserDto);
        if (!flag) {
            throw new UserException("user.register.error");
        }
        SysSensitiveLogBean registerOperLogEvent = USecurity.recordSensitiveLog(() -> {
            String ip = UServlet.getInitiatedRequestIp();
            SysSensitiveLogBean registerOperLog = new SysSensitiveLogBean();
            registerOperLog.setOperatorId(0L);
            registerOperLog.setOperatorName(username);
            registerOperLog.setMethodType(UServlet.getMethod());
            registerOperLog.setUrl(UServlet.getRequestUri());
            registerOperLog.setBeOperatedId(0L);
            registerOperLog.setBeOperatedName(username);
            registerOperLog.setIpAddress(ip);
            registerOperLog.setLocation(URegion.getRealAddressByIp(ip));
            registerOperLog.setOperateTime(new Date());
            registerOperLog.setBrowser(UServlet.getBrowser());
            registerOperLog.setOs(UServlet.getOs());
            registerOperLog.setModule(SysModule.USER_MANAGEMENT);
            registerOperLog.setSubModule(SysModule.REGISTER);
            registerOperLog.setType(SysModule.REGISTER);
            registerOperLog.setResult(FlagConstant.SUCCESS);
            registerOperLog.setContextOld(null);
            registerOperLog.setContext(null);
            registerOperLog.setSign(username);
            registerOperLog.setRemark(UMessage.message("user.register.success"));
            return registerOperLog;
        });
        USpring.context().publishEvent(registerOperLogEvent);
    }
}
