package com.freesia.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.annotation.Desensitize;
import com.freesia.constant.Constants;
import com.freesia.constant.DesensitizedType;
import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 租户信息表 数据传输对象
 * @date 2024-02-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户信息表 数据传输对象")
public class SysTenantDto extends BaseDto {
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
    @Desensitize(strategy = DesensitizedType.EURO_AMERICAN_NAME)
    private String contactName;
    @Desensitize(strategy = DesensitizedType.MOBILE_PHONE)
    @Schema(description = "联系人电话")
    private String contactTel;
    @Desensitize(strategy = DesensitizedType.EMAIL)
    @Schema(description = "联系人邮箱")
    private String contactEmail;
    @Schema(description = "租户地址")
    private String address;
    @Schema(description = "营业时间（从）")
    @JSONField(format = Constants.YM)
    private Date businessHoursFrom;
}
