package com.freesia.account.entity;

import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 自动完成-根据输入查询图标类型和URL 结果集
 * @date 2025-09-10
 */
@Data
public class FindCacheCostTypeEntity {
    /**
     * 描述
     */
    private String value;
    /**
     * 图标URL
     */
    private String iconUrl;
    /**
     * 是否禁用
     */
    private Boolean disabled;
}
