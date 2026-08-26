package com.freesia.account.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查询分摊金额（包括本人未分摊、他人未分摊） 响应DTO
 * @date 2026-05-10
 */
@Data
public class RpFindAllocAmountDto {
    @Schema(description = "他人未分摊")
    private List<Alloc> collected;
    @Schema(description = "他人未分摊总金额")
    private BigDecimal totalCollected;
    @Schema(description = "本人未分摊")
    private List<Alloc> allocated;
    @Schema(description = "本人未分摊总金额")
    private BigDecimal totalAllocated;

    public RpFindAllocAmountDto() {
        this.collected = new ArrayList<>();
        this.allocated = new ArrayList<>();
        this.totalCollected = BigDecimal.ZERO;
        this.totalAllocated = BigDecimal.ZERO;
    }

    public void addCollected(Alloc alloc) {
        this.collected.add(alloc);
        this.totalCollected = this.totalCollected.add(alloc.getAmount());
    }

    public void addAllocated(Alloc alloc) {
        this.allocated.add(alloc);
        this.totalAllocated = this.totalAllocated.add(alloc.getAmount());
    }

    @Data
    public static class Alloc {
        private Long id;
        private String userId;
        private String nickName;
        private Long payeeUserId;
        private String payeeNickName;
        private BigDecimal amount;
        @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
        private Date paymentTime;
        private String costDesc;
        private String remark;
    }
}
