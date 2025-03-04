package com.freesia.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.freesia.dto.RegisterDto;
import com.freesia.dto.SysUserDto;
import com.freesia.exception.UserException;
import com.freesia.satoken.constant.UserType;
import com.freesia.service.SysConfigService;
import com.freesia.service.SysRegisterService;
import com.freesia.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

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
        String userType = Optional.of(registerDto)
                .map(RegisterDto::getUserType)
                .map(UserType::getInstanceByKey)
                .orElse(UserType.SYS_USER)
                .getUserType();
        sysConfigService.validateCaptcha(username, registerDto.getCode(), registerDto.getCaptchaKey());
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setUserName(username);
        sysUserDto.setNickName(username);
        sysUserDto.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        sysUserDto.setUserType(userType);
        if (sysUserService.checkUserNameUnique(sysUserDto)) {
            throw new UserException("user.register.not.unique", new Object[]{username});
        }
        boolean flag = sysUserService.register(sysUserDto);
        if (!flag) {
            throw new UserException("user.register.error", new Object[]{});
        }
    }
}
