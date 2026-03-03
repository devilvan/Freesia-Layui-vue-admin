package com.freesia.account.entity;

import com.freesia.account.dto.AccountBudgetDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 容量图-根据预算日期类型查询 实体类
 * @date 2025-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindBudgetCapacityEntity extends AccountBudgetDto {
}
