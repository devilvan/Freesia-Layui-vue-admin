package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 部门管理模块 静态类
 * @date 2024-07-09
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeptModule extends SysModule {
    /**
     * 主模块 部门管理模块
     */
    public static final String DEPT_MANAGEMENT = "dept_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {
        public static final String ASSIGN_ROLE = "dept_assign_role";
    }
}
