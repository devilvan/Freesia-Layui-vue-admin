package com.freesia.account.entity;

import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 自动完成-根据输入查询图标类型和URL 结果集
 * @date 2025-09-09
 */
@Data
public class FindCacheCostTypeEntity {
    /**
     * 开销类型
     */
    private String costType;
    /**
     * 图标URL
     */
    private String icon;
}
