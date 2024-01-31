package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 租户信息表 数据传输对象
 * @date 2024-01-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户信息表 数据传输对象")
public class SysTenantDto extends BaseDto {
    @Schema(description = "租户名称")
    private String name;
    @Schema(description = "租户备注")
    private String type;
    @Schema(description = "租户状态")
    private String status;
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
    private Date businessHoursFrom;
}
