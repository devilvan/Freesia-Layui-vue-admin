package com.freesia.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.constant.UserType;
import com.freesia.model.LoginUserModel;
import com.freesia.util.USecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Service层获取用户permissions、roles属性 实现类 {@link StpInterface}
 * @date 2023-08-25
 */
public class SaPermissionImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUserModel loginUser = USecurity.getLoginUser();
        if (ObjectUtil.isNull(loginUser)) {
            return new ArrayList<>();
        }
        UserType userType = UserType.getInstanceByKey(loginUser.getUserType());
        if (UserType.SYS_USER.equals(userType)) {
            return new ArrayList<>(loginUser.getMenuPermission());
        }
        // 如果是其他客户端再加
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUserModel loginUser = USecurity.getLoginUser();
        if (ObjectUtil.isNull(loginUser)) {
            return new ArrayList<>();
        }
        UserType userType = UserType.getInstanceByKey(loginUser.getUserType());
        if (UserType.SYS_USER.equals(userType)) {
            return new ArrayList<>(loginUser.getRolePermission());
        }
        // 如果是其他客户端再加
        return new ArrayList<>();
    }
}
