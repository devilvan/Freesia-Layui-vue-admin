package com.freesia.service;


import com.freesia.constant.LoginRetryType;
import com.freesia.dto.SysUserDto;
import com.freesia.dto.WxLoginDto;
import com.freesia.po.SysUserPo;
import com.freesia.satoken.model.LoginUserModel;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Evad.Wu
 * @Description 登录功能 业务逻辑接口
 * @date 2023-08-12
 */
public interface SysLoginService {
    /**
     * 登录验证
     *
     * @param username   用户名
     * @param password   密码
     * @param code       验证码
     * @param captchaKey 唯一标识
     * @return 返回登录成功后生成的token
     */
    String login(String username, String password, String code, String captchaKey);

    /**
     * 微信登录验证
     *
     * @param wxLoginDto 微信登录信息
     * @return 返回登录成功后生成的信息，包含token和重定向URL
     */
    Map<String, Object> wxLogin(WxLoginDto wxLoginDto);

    /**
     * 构建登录用户模型
     *
     * @param sysUserPo 用户基本信息
     * @return 登录用户
     */
    LoginUserModel buildLoginUser(SysUserPo sysUserPo);

    /**
     * 用户是否管理员
     *
     * @param sysUserPo 当前用户
     * @return flag
     */
    boolean isAdmin(SysUserPo sysUserPo);

    /**
     * 登录校验
     *
     * @param loginRetryType 登录重试配置项
     * @param username       用户名
     * @param bcrptCheckpw   Bcrpt验证函数
     */
    void checkLogin(LoginRetryType loginRetryType, String username, Supplier<Boolean> bcrptCheckpw);

    /**
     * 根据用户名查询用户信息，判断是否合法
     *
     * @param username 用户名
     * @return 合法的用户
     */
    SysUserPo findByUsername(String username);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 初始化用户信息
     */
    void initUser(SysUserDto sysUserDto);
}
