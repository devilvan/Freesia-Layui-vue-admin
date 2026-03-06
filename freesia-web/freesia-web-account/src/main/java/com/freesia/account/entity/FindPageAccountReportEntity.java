package com.freesia.account.entity;

import com.freesia.account.dto.AccountReportDto;
import com.freesia.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author Bliss.Wu
 * @Description 自定义分页查询记账报表表信息 实体类
 * @date 2026-03-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageAccountReportEntity extends AccountReportDto {
    @Schema(description = "攒钱金额")
    private BigDecimal saveAmount;
}
