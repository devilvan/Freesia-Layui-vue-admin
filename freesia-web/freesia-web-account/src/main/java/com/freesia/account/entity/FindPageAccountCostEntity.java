package com.freesia.account.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.dto.BaseDto;
import com.freesia.oss.annotation.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查询开销表分页信息 结果集
 * @date 2025-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageAccountCostEntity extends BaseDto {
    @Schema(description = "开销描述")
    private String costDesc;
    @Schema(description = "开销金额")
    private BigDecimal outlay;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    private String costType;
    @Schema(description = "开销标识（支出、收入）")
    private String paymentSign;
    @Schema(description = "时间")
    @JsonFormat(pattern = Constants.YMD_HM)
    private Date paymentTime;
    @Domain
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 费用分摊集合
     */
    @Schema(description = "费用分摊集合")
    private List<Alloc> allocList;
    /**
     * 关联用户ID
     */
    @Schema(description = "关联用户ID")
    private String accountCostUserId;
    /**
     * 关联用户昵称
     */
    @Schema(description = "关联用户昵称")
    private String accountCostUserName;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String acNickName;
    /**
     * 分摊金额
     */
    @Schema(description = "分摊金额")
    private BigDecimal allocAmount;
    /**
     * 分摊状态
     */
    @Schema(description = "分摊状态")
    private String allocStatus;

    /**
     * 费用分摊集合
     */
    @Data
    public static class Alloc {
        @Schema(description = "费用分摊ID")
        private Long id;
        @Schema(description = "记账ID")
        private Long costId;
        @Schema(description = "用户ID")
        private Long userId;
        @Schema(description = "分摊金额")
        private BigDecimal amount;
        @Schema(description = "分摊时间")
        private Date operateTime;
        @Schema(description = "是否分摊（0-否，1-是）")
        private Boolean allocFlag;
        @Schema(description = "用户信息")
        private User user;

        /**
         * 用户信息
         */
        @Data
        public static class User {
            @Schema(description = "用户ID")
            private Long id;
            @Schema(description = "用户账号")
            private String userName;
            @Schema(description = "用户昵称")
            private String nickName;
        }
    }
}
