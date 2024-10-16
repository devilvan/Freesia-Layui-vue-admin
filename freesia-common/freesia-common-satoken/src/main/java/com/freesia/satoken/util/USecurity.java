package com.freesia.satoken.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.Constants;
import com.freesia.net.util.UServlet;
import com.freesia.satoken.constant.DeviceType;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.util.URegion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Evad.Wu
 * @Description token相关 工具类
 * @date 2023-08-17
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USecurity {
    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String USER_KEY = "userId";

    /**
     * 构建敏感信息bean的函数接口，方便到行级别的自定义敏感数据
     *
     * @param supplier 接收敏感信息的生产者
     * @param <T>      敏感信息bean的类型
     * @return 敏感信息bean
     */
    public static <T extends SysSensitiveLogBean> T recordSensitiveLog(Supplier<T> supplier) {
        T sysSensitiveLogBean = supplier.get();
        String ip = UServlet.getInitiatedRequestIp();
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
        sysSensitiveLogBean.setOperatorId(loginUser.getUserId());
        sysSensitiveLogBean.setOperatorName(loginUser.getUsername());
        sysSensitiveLogBean.setDeptId(loginUser.getDeptId());
        sysSensitiveLogBean.setDeptName(loginUser.getDeptName());
        sysSensitiveLogBean.setMethodType(UServlet.getMethod());
        sysSensitiveLogBean.setUrl(UServlet.getRequestUri());
        sysSensitiveLogBean.setBeOperatedId(loginUser.getUserId());
        sysSensitiveLogBean.setBeOperatedName(loginUser.getUsername());
        sysSensitiveLogBean.setIpAddress(ip);
        sysSensitiveLogBean.setLocation(URegion.getRealAddressByIp(ip));
        sysSensitiveLogBean.setOperateTime(new Date());
        sysSensitiveLogBean.setBrowser(UServlet.getBrowser());
        sysSensitiveLogBean.setOs(UServlet.getOs());
        sysSensitiveLogBean.setSign(loginUser.getUsername());
        sysSensitiveLogBean.setTenantId(USecurity.getTenantId());
        return sysSensitiveLogBean;
    }

    /**
     * 登录系统，基于不同设备类型，用于不同的客户端
     *
     * @param loginUserModel 登录用户模型
     * @param deviceType     设备类型
     */
    public static void loginByDevice(LoginUserModel loginUserModel, DeviceType deviceType) {
        SaStorage storage = SaHolder.getStorage();
        storage.set(LOGIN_USER_KEY, loginUserModel);
        storage.set(USER_KEY, loginUserModel.getUserId());
        SaLoginModel model = new SaLoginModel();
        if (ObjectUtil.isNotNull(deviceType)) {
            model.setDevice(deviceType.getDevice());
        }
        StpUtil.login(loginUserModel.getLoginId(), model.setExtra(USER_KEY, loginUserModel.getUserId()));
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUserModel);
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户模型
     */
    public static LoginUserModel getLoginUser() {
        LoginUserModel loginUserModel = (LoginUserModel) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (loginUserModel != null) {
            return loginUserModel;
        }
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        loginUserModel = (LoginUserModel) session.get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUserModel);
        return loginUserModel;
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     */
    public static Long getUserId() {
        Long userId = null;
        try {
            userId = Convert.toLong(SaHolder.getStorage().get(USER_KEY));
            if (ObjectUtil.isNull(userId)) {
                userId = Convert.toLong(StpUtil.getExtra(USER_KEY));
                SaHolder.getStorage().set(USER_KEY, userId);
            }
        } catch (Exception ignored) {
            log.warn(UMessage.message("user.acquire.id.failed"));
        }
        return userId;
    }

    /**
     * 判断当前用户是否为超级管理员 {@link AdminConstant#ADMIN_ID}
     *
     * @return flag
     */
    public static boolean isAdmin() {
        LoginUserModel loginUser = getLoginUser();
        if (ObjectUtil.isNotNull(loginUser)) {
            return AdminConstant.ADMIN_ID == loginUser.getUserId();
        }
        return false;
    }

    /**
     * 判断用户是否为超级管理员 {@link AdminConstant#ADMIN_ID}
     *
     * @param userId 用户ID
     * @return flag
     */
    public static boolean isAdmin(Long userId) {
        return AdminConstant.ADMIN_ID == Convert.toLong(userId, 0L);
    }

    /**
     * 获取当前用户的租户ID
     *
     * @return 当前用户的租户ID
     */
    public static Long getTenantId() {
        HttpServletRequest request = UServlet.getRequest();
        if (UEmpty.isNotNull(request)) {
            String xTenantId = request.getHeader(Constants.X_TENANT_ID);
            if (UEmpty.isNotEmpty(xTenantId)) {
                return Long.parseLong(xTenantId);
            }
        }
        return null;
    }
}
