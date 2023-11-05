package com.freesia.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.*;
import com.freesia.exception.UserException;
import com.freesia.mapper.SysUserMapper;
import com.freesia.model.LoginUserModel;
import com.freesia.model.SysRoleModel;
import com.freesia.po.SysDeptPo;
import com.freesia.po.SysRolePo;
import com.freesia.po.SysUserPo;
import com.freesia.properties.LoginPasswordProperties;
import com.freesia.service.*;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Evad.Wu
 * @Description 登录功能 业务逻辑实现类
 * @date 2023-08-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginServiceImpl implements SysLoginService {
    private final LoginPasswordProperties loginPasswordProperties;
    private final SysConfigService sysConfigService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;
    private final SysUserMapper sysUserMapper;


    @Override
    public String login(String username, String password, String code, String captchaKey) {
        sysConfigService.validateCaptcha(username, code, captchaKey);
        SysUserPo sysUserPo = findByUsername(username);
        // 检查登录状态
        checkLogin(LoginRetryType.PASSWORD, username, () -> !BCrypt.checkpw(password, sysUserPo.getPassword()));
        // 构建登录用户模型
        LoginUserModel loginUserModel = buildLoginUser(sysUserPo);
        // 登录系统，基于不同设备类型，用于不同的客户端
        USecurity.loginByDevice(loginUserModel, DeviceType.PC);
        // 登录成功后记录操作日志
        SysSensitiveLogBean loginOperLogEvent = USecurity.recordSensitiveLog(() -> {
            SysSensitiveLogBean loginOperLog = new SysSensitiveLogBean();
            String ip = UServlet.getInitiatedRequestIp();
            LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
            loginOperLog.setOperatorId(loginUser.getUserId());
            loginOperLog.setOperatorName(username);
            loginOperLog.setDeptId(loginUser.getDeptId());
            loginOperLog.setDeptName(loginUser.getDeptName());
            loginOperLog.setMethodType(UServlet.getMethod());
            loginOperLog.setUrl(UServlet.getRequestUri());
            loginOperLog.setBeOperatedId(loginUser.getUserId());
            loginOperLog.setBeOperatedName(username);
            loginOperLog.setIpAddress(ip);
            loginOperLog.setLocation(URegion.getRealAddressByIp(ip));
            loginOperLog.setOperateTime(new Date());
            loginOperLog.setBrowser(UServlet.getBrowser());
            loginOperLog.setOs(UServlet.getOs());
            loginOperLog.setModule(SysModule.USER_MANAGEMENT);
            loginOperLog.setSubModule(SysModule.LOGIN);
            loginOperLog.setType(SysModule.LOGIN);
            loginOperLog.setResult(FlagConstant.SUCCESS);
            loginOperLog.setContextOld(null);
            loginOperLog.setContext(null);
            loginOperLog.setSign(username);
            loginOperLog.setRemark(UMessage.message("user.login.success"));
            return loginOperLog;
        });
        USpring.context().publishEvent(loginOperLogEvent);
        return StpUtil.getTokenValue();
    }

    /**
     * 构建登录用户模型
     *
     * @param sysUserPo 用户基本信息
     * @return 登录用户
     */
    @Override
    public LoginUserModel buildLoginUser(SysUserPo sysUserPo) {
        // 获取用户对应的部门
        SysDeptPo sysDeptPo = sysUserPo.getSysDeptPo();
        // 是否管理员
        boolean isAdmin = isAdmin(sysUserPo);
        // 获取用户对应的角色
        Set<String> sysRoleStrSet = loadRolePermission(isAdmin, sysUserPo);
        // 获取用户对应的菜单
        Set<String> sysMenuStrSet = loadMenuPermission(isAdmin, sysUserPo);
        // 获取角色信息
        Set<SysRolePo> sysRolePoSet = sysUserPo.getSysRolePoSet();
        List<SysRoleModel> sysRoleModelList = sysRolePoSet2ModelList(sysRolePoSet);
        String ip = UServlet.getInitiatedRequestIp();
        LoginUserModel loginUserModel = new LoginUserModel();
        loginUserModel.setUserId(sysUserPo.getId());
        loginUserModel.setDeptId(sysUserPo.getDeptId());
        loginUserModel.setDeptName(sysDeptPo.getDeptName());
        loginUserModel.setUserType(sysUserPo.getUserType());
        loginUserModel.setIpaddr(ip);
        loginUserModel.setLoginLocation(URegion.getRealAddressByIp(ip));
        loginUserModel.setBrowser(UServlet.getBrowser());
        loginUserModel.setOs(UServlet.getOs());
        loginUserModel.setMenuPermission(sysMenuStrSet);
        loginUserModel.setRolePermission(sysRoleStrSet);
        loginUserModel.setUsername(sysUserPo.getUserName());
        loginUserModel.setRoles(sysRoleModelList);
        return loginUserModel;
    }

    public List<SysRoleModel> sysRolePoSet2ModelList(Set<SysRolePo> sysRolePoSet) {
        List<SysRoleModel> sysRoleModelList = UCollection.optimizeInitialCapacityArrayList(sysRolePoSet.size());
        for (SysRolePo sysRolePo : sysRolePoSet) {
            SysRoleModel sysRoleModel = new SysRoleModel();
            UCopy.fullCopy(sysRolePo, sysRoleModel);
            sysRoleModelList.add(sysRoleModel);
        }
        return sysRoleModelList;
    }

    /**
     * 获取用户对应的菜单权限信息
     *
     * @param isAdmin   是否管理员
     * @param sysUserPo 当前用户
     * @return 菜单权限集合
     */
    public Set<String> loadMenuPermission(boolean isAdmin, SysUserPo sysUserPo) {
        Set<String> sysRolePoSet = new HashSet<>();
        if (isAdmin) {
            sysRolePoSet.add(AdminConstant.ADMIN_MENU_PERMS);
        } else {
            Set<String> menuStrSet = sysMenuService.findMenuPermissionByUserId(sysUserPo.getId());
            sysRolePoSet.addAll(menuStrSet);
        }
        return sysRolePoSet;
    }

    /**
     * 获取用户对应的角色权限信息
     *
     * @param isAdmin   是否管理员
     * @param sysUserPo 当前用户
     * @return 角色权限集合
     */
    public Set<String> loadRolePermission(boolean isAdmin, SysUserPo sysUserPo) {
        Set<String> sysRolePoSet = new HashSet<>();
        if (isAdmin) {
            sysRolePoSet.add(AdminConstant.ADMIN);
        } else {
            Set<String> roleStrSet = sysRoleService.findRolePermissionStrByUserId(sysUserPo.getId());
            sysRolePoSet.addAll(roleStrSet);
        }
        return sysRolePoSet;
    }

    /**
     * 用户是否管理员
     *
     * @param sysUserPo 当前用户
     * @return flag
     */
    @Override
    public boolean isAdmin(SysUserPo sysUserPo) {
        return Convert.toBool(sysUserMapper.isAdmin(sysUserPo.getId()), false);
    }

    /**
     * 登录校验
     *
     * @param loginRetryType 登录重试配置项
     * @param username       用户名
     * @param bcrptCheckpw   Bcrpt验证函数
     */
    @Override
    public void checkLogin(LoginRetryType loginRetryType, String username, Supplier<Boolean> bcrptCheckpw) {
        // 构造用户对应的登录账户密码错误次数
        String errorKey = CacheConstant.PWD_ERR_CNT_KEY + username;
        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        int errorCount = ObjectUtil.defaultIfNull(URedis.get(errorKey), 0);
        Integer maxRetryCount = loginPasswordProperties.getMaxRetryCount();
        // 如果输入错误超过最大次数，则锁定
        String ip = UServlet.getInitiatedRequestIp();
        if (errorCount >= maxRetryCount) {
            String message = UMessage.message(loginRetryType.getRetryLimitExceed(), errorCount, maxRetryCount);
            SysSensitiveLogBean loginOperLogEvent = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean loginOperLog = new SysSensitiveLogBean();
                loginOperLog.setOperatorId(0L);
                loginOperLog.setDeptId(-1L);
                loginOperLog.setDeptName(AdminConstant.UNKNOWN);
                loginOperLog.setOperatorName(username);
                loginOperLog.setMethodType(UServlet.getMethod());
                loginOperLog.setUrl(UServlet.getRequestUri());
                loginOperLog.setBeOperatedId(0L);
                loginOperLog.setBeOperatedName(username);
                loginOperLog.setIpAddress(ip);
                loginOperLog.setLocation(URegion.getRealAddressByIp(ip));
                loginOperLog.setOperateTime(new Date());
                loginOperLog.setBrowser(UServlet.getBrowser());
                loginOperLog.setOs(UServlet.getOs());
                loginOperLog.setModule(SysModule.USER_MANAGEMENT);
                loginOperLog.setSubModule(SysModule.CHECK_PASSWORD);
                loginOperLog.setType(SysModule.LOGIN);
                loginOperLog.setResult(FlagConstant.FAILED);
                loginOperLog.setContextOld(null);
                loginOperLog.setContext(null);
                loginOperLog.setSign(username);
                loginOperLog.setRemark(message);
                return loginOperLog;
            });
            USpring.context().publishEvent(loginOperLogEvent);
            throw new UserException(loginRetryType.getRetryLimitExceed(), errorCount, maxRetryCount);
        }
        // 密码输入错误
        if (bcrptCheckpw.get()) {
            errorCount++;
            // 更新Redis中的密码错误次数
            URedis.set(errorKey, errorCount);
            // 如果输入错误超过最大次数，则锁定
            if (errorCount >= maxRetryCount) {
                String message = UMessage.message(loginRetryType.getRetryLimitExceed(), errorCount, maxRetryCount);
                SysSensitiveLogBean loginOperLogEvent = USecurity.recordSensitiveLog(() -> {
                    SysSensitiveLogBean loginOperLog = new SysSensitiveLogBean();
                    loginOperLog.setOperatorId(0L);
                    loginOperLog.setDeptId(-1L);
                    loginOperLog.setDeptName(AdminConstant.UNKNOWN);
                    loginOperLog.setOperatorName(username);
                    loginOperLog.setMethodType(UServlet.getMethod());
                    loginOperLog.setUrl(UServlet.getRequestUri());
                    loginOperLog.setBeOperatedId(0L);
                    loginOperLog.setBeOperatedName(username);
                    loginOperLog.setIpAddress(ip);
                    loginOperLog.setLocation(URegion.getRealAddressByIp(ip));
                    loginOperLog.setOperateTime(new Date());
                    loginOperLog.setBrowser(UServlet.getBrowser());
                    loginOperLog.setOs(UServlet.getOs());
                    loginOperLog.setModule(SysModule.USER_MANAGEMENT);
                    loginOperLog.setSubModule(SysModule.CHECK_PASSWORD);
                    loginOperLog.setType(SysModule.LOGIN);
                    loginOperLog.setResult(FlagConstant.FAILED);
                    loginOperLog.setContextOld(null);
                    loginOperLog.setContext(null);
                    loginOperLog.setSign(username);
                    loginOperLog.setRemark(message);
                    return loginOperLog;
                });
                USpring.context().publishEvent(loginOperLogEvent);
                throw new UserException(loginRetryType.getRetryLimitExceed(), errorCount, maxRetryCount);
            } else {
                // 未达到最大重试次数
                String message = UMessage.message(loginRetryType.getRetryLimitCount(), errorCount);
                SysSensitiveLogBean loginOperLogEvent = USecurity.recordSensitiveLog(() -> {
                    SysSensitiveLogBean loginOperLog = new SysSensitiveLogBean();
                    loginOperLog.setOperatorId(0L);
                    loginOperLog.setDeptId(-1L);
                    loginOperLog.setDeptName(AdminConstant.UNKNOWN);
                    loginOperLog.setOperatorName(username);
                    loginOperLog.setMethodType(UServlet.getMethod());
                    loginOperLog.setUrl(UServlet.getRequestUri());
                    loginOperLog.setBeOperatedId(0L);
                    loginOperLog.setBeOperatedName(username);
                    loginOperLog.setIpAddress(ip);
                    loginOperLog.setLocation(URegion.getRealAddressByIp(ip));
                    loginOperLog.setOperateTime(new Date());
                    loginOperLog.setBrowser(UServlet.getBrowser());
                    loginOperLog.setOs(UServlet.getOs());
                    loginOperLog.setModule(SysModule.USER_MANAGEMENT);
                    loginOperLog.setSubModule(SysModule.CHECK_CAPTCHA);
                    loginOperLog.setType(SysModule.LOGIN);
                    loginOperLog.setResult(FlagConstant.FAILED);
                    loginOperLog.setContextOld(null);
                    loginOperLog.setContext(null);
                    loginOperLog.setSign(username);
                    loginOperLog.setRemark(message);
                    return loginOperLog;
                });
                USpring.context().publishEvent(loginOperLogEvent);
                throw new UserException(loginRetryType.getRetryLimitCount(), errorCount);
            }
        }
        // 登录成功，清除登录失败次数
        URedis.delete(errorKey);
    }

    /**
     * 根据用户名查询用户信息，判断是否合法
     *
     * @param username 用户名
     * @return 合法的用户
     */
    @Override
    public SysUserPo findByUsername(String username) {
        SysUserPo sysUserPo = sysUserService.findOneByUsername(username);
        if (ObjectUtil.isNull(sysUserPo)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (FlagConstant.DISABLED.equals(sysUserPo.getAccountStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }
        return sysUserService.findByUsername(username);
    }

    @Override
    public void logout() {
        try {
            LoginUserModel loginUser = USecurity.getLoginUser();
            if (ObjectUtil.isNull(loginUser)) {
                return;
            }
            StpUtil.logout();
            SysSensitiveLogBean loginOperLogEvent = USecurity.recordSensitiveLog(() -> {
                String username = ObjectUtil.defaultIfNull(loginUser.getUsername(), AdminConstant.UNKNOWN);
                String ip = UServlet.getInitiatedRequestIp();
                SysSensitiveLogBean loginOperLog = new SysSensitiveLogBean();
                loginOperLog.setOperatorId(loginUser.getUserId());
                loginOperLog.setOperatorName(username);
                loginOperLog.setDeptId(loginUser.getDeptId());
                loginOperLog.setDeptName(loginUser.getDeptName());
                loginOperLog.setMethodType(UServlet.getMethod());
                loginOperLog.setUrl(UServlet.getRequestUri());
                loginOperLog.setBeOperatedId(loginUser.getUserId());
                loginOperLog.setBeOperatedName(username);
                loginOperLog.setIpAddress(ip);
                loginOperLog.setLocation(URegion.getRealAddressByIp(ip));
                loginOperLog.setOperateTime(new Date());
                loginOperLog.setBrowser(UServlet.getBrowser());
                loginOperLog.setOs(UServlet.getOs());
                loginOperLog.setModule(SysModule.USER_MANAGEMENT);
                loginOperLog.setSubModule(SysModule.LOGOUT);
                loginOperLog.setType(SysModule.LOGOUT);
                loginOperLog.setResult(FlagConstant.SUCCESS);
                loginOperLog.setContextOld(null);
                loginOperLog.setContext(null);
                loginOperLog.setSign(username);
                loginOperLog.setRemark(UMessage.message("user.logout.success"));
                return loginOperLog;
            });
            USpring.context().publishEvent(loginOperLogEvent);
        } catch (NotLoginException ignored) {
        }
    }
}
