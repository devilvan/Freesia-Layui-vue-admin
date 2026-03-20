package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 审计字段名称 静态类
 * @date 2023-08-20
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuditConstant {
    public static final String CREATOR = "creator";
    public static final String CREATE_TIME = "createTime";
    public static final String MODIFIER = "modifier";
    public static final String MODIFY_TIME = "modifyTime";
    public static final String LOGIC_DEL = "logicDel";
    public static final String REC_VER = "recVer";
    public static final String BUILD_IN = "buildIn";
    public static final String TENANT_ID = "tenantId";

    public static final String TABLE_NAME_ID = "ID";
    public static final String TABLE_NAME_CREATOR = "CREATOR";
    public static final String TABLE_NAME_CREATE_TIME = "CREATE_TIME";
    public static final String TABLE_NAME_MODIFIER = "MODIFIER";
    public static final String TABLE_NAME_MODIFY_TIME = "MODIFY_TIME";
    public static final String TABLE_NAME_LOGIC_DEL = "LOGIC_DEL";
    public static final String TABLE_NAME_REC_VER = "REC_VER";
    public static final String TABLE_NAME_BUILD_IN = "BUILD_IN";
    public static final String TABLE_NAME_TENANT_ID = "TENANT_ID";
}
