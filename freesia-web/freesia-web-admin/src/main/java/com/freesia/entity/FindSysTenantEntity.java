package com.freesia.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 删除租户信息表 持久层传输类
 * @date 2024-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindSysTenantEntity extends BaseEntity {
    @Schema(description = "租户编码")
    private String code;
    @Schema(description = "租户名称")
    private String name;
    @Schema(description = "租户类型")
    private String type;
    @Schema(description = "租户状态（0-禁用 1-开启）")
    private Boolean status;
    @Schema(description = "租户备注")
    private String remark;
    @Schema(description = "联系人姓名")
    private String contactName;
    @Schema(description = "联系人电话")
    private String contactTel;
    @Schema(description = "联系人邮箱")
    private String contactEmail;
    @Schema(description = "租户地址")
    private String address;
    @Schema(description = "营业时间（从）")
    @JSONField(format = Constants.YM)
    private Date businessHoursFrom;
}
