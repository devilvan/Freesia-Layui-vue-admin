package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 租户信息表 值对象
 * @date 2024-02-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "租户信息表 值对象")
public class SysTenantVo {
    @Schema(description = "租户编码")
    @JSONField(alternateNames = {"code"})
    private String code;
    @Schema(description = "租户名称")
    @JSONField(alternateNames = {"name"})
    private String name;
    @Schema(description = "租户类型")
    @JSONField(alternateNames = {"type"})
    private String type;
    @Schema(description = "租户状态（0-禁用 1-开启）")
    @JSONField(alternateNames = {"status"})
    private Boolean status;
    @Schema(description = "租户备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "联系人姓名")
    @JSONField(alternateNames = {"contactName"})
    private String contactName;
    @Schema(description = "联系人电话")
    @JSONField(alternateNames = {"contactTel"})
    private String contactTel;
    @Schema(description = "联系人邮箱")
    @JSONField(alternateNames = {"contactEmail"})
    private String contactEmail;
    @Schema(description = "租户地址")
    @JSONField(alternateNames = {"address"})
    private String address;
    @Schema(description = "营业时间（从）")
    @JSONField(alternateNames = {"businessHoursFrom"}, format = Constants.YM)
    private Date businessHoursFrom;
}
