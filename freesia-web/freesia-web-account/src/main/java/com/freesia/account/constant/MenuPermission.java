package com.freesia.account.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 菜单权限 静态类
 * @date 2025-02-26
 */
@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuPermission {
    /**
     * 记账管理
     */
    public static final String
            ACCOUNT_COST_INDEX = "account:cost:index",
            ACCOUNT_COST_ADD = "account:cost:add",
            ACCOUNT_COST_EDIT = "account:cost:edit",
            ACCOUNT_COST_DELETE = "account:cost:delete",
            ACCOUNT_COST_IMPORT = "account:cost:import",
            ACCOUNT_COST_EXPORT = "account:cost:export";
}
