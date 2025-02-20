package com.freesia.oss.constant;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 桶权限类型 枚举类
 * @date 2024-02-28
 */
@Getter
@AllArgsConstructor
public enum AccessPolicy {
    /**
     * 私有
     */
    PRIVATE(CannedAccessControlList.Private, PolicyType.WRITE),
    /**
     * 公有
     */
    PUBLIC(CannedAccessControlList.PublicRead, PolicyType.READ),
    /**
     * 自定义
     */
    CUSTOM(CannedAccessControlList.PublicRead, PolicyType.READ)
    ;
    /**
     * 文件对象 权限类型
     */
    private final CannedAccessControlList acl;

    /**
     * 桶策略类型
     */
    private final PolicyType policyType;

    public static AccessPolicy getByName(String name) {
        AccessPolicy[] accessPolicies = values();
        for (AccessPolicy accessPolicy : accessPolicies) {
            if (accessPolicy.name().equalsIgnoreCase(name)) {
                return accessPolicy;
            }
        }
        throw new RuntimeException("'name' not found By " + name);
    }
}
