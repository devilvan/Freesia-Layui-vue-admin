package com.freesia.constant;

import com.freesia.util.UEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 租户类型 枚举
 * @date 2026-06-03
 */
@Getter
@AllArgsConstructor
public enum SysTenantType {
    /**
     * 单租户
     */
    INDIVIDUAL("INDIVIDUAL", "个人"),
    /**
     * 企业
     */
    ENTERPRISES("ENTERPRISES", "企业"),
    /**
     * 组织
     */
    ORGANIZATION("ORGANIZATION", "组织");

    /**
     * 编码
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据编码获取实例
     *
     * @param code 编码
     * @return 实例
     */
    public static SysTenantType getInstanceByCode(String code) {
        if (UEmpty.isEmpty(code)) {
            return null;
        }
        for (SysTenantType tenantType : SysTenantType.values()) {
            if (tenantType.getCode().equals(code)) {
                return tenantType;
            }
        }
        return null;
    }
}
