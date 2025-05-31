package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户信息表 值对象")
public class SysTenantVo extends BaseVo {
    @Schema(description = "租户编码")
    @JsonAlias(value = {"code"})
    private String code;
    @Schema(description = "租户名称")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "租户类型")
    @JsonAlias(value = {"type"})
    private String type;
    @Schema(description = "租户状态（0-禁用 1-开启）")
    @JsonAlias(value = {"status"})
    private Boolean status;
    @Schema(description = "租户备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "联系人姓名")
    @JsonAlias(value = {"contactName"})
    private String contactName;
    @Schema(description = "联系人电话")
    @JsonAlias(value = {"contactTel"})
    private String contactTel;
    @Schema(description = "联系人邮箱")
    @JsonAlias(value = {"contactEmail"})
    private String contactEmail;
    @Schema(description = "租户地址")
    @JsonAlias(value = {"address"})
    private String address;
    @Schema(description = "营业时间（从）")
    @JsonAlias(value = {"businessHoursFrom"})
    @JsonFormat(pattern = Constants.YM)
    private Date businessHoursFrom;
}
