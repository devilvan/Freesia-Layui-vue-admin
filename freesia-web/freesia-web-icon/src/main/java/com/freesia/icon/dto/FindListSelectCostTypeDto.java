package com.freesia.icon.dto;

import lombok.Data;

/**
 * @author Bliss.Wu
 * @Description 查询开销类型下拉集合 数据传输对象
 * @date 2025-09-10
 */
@Data
public class FindListSelectCostTypeDto {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 值
     */
    private String value;
}
