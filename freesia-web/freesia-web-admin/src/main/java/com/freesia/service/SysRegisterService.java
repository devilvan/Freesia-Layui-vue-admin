package com.freesia.service;

import com.freesia.dto.RegisterDto;
import com.freesia.dto.SysUserDto;
import com.freesia.po.SysUserPo;

/**
 * @author Evad.Wu
 * @Description 注册功能 业务逻辑接口
 * @date 2023-08-22
 */
public interface SysRegisterService {
    /**
     * 用户注册
     *
     * @param sysUserDto 用户注册信息
     */
    SysUserPo register(SysUserDto sysUserDto);
}
