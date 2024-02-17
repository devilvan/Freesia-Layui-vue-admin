package com.freesia.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 模块传输对象
 * @date 2023-08-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "敏感操作信息表 模块传输对象")
public class SysSensitiveLogBean extends BaseBean {
    @Schema(description = "操作人ID")
    private Long operatorId;
    @Schema(description = "操作人姓名")
    private String operatorName;
    @Schema(description = "所属部门ID")
    private Long deptId;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "请求类型")
    private String methodType;
    @Schema(description = "URL")
    private String url;
    @Schema(description = "被操作用户ID")
    private Long beOperatedId;
    @Schema(description = "被操作用户姓名")
    private String beOperatedName;
    @Schema(description = "IP地址")
    private String ipAddress;
    @Schema(description = "操作地点")
    private String location;
    @Schema(description = "操作时间")
    private Date operateTime;
    @Schema(description = "浏览器")
    private String browser;
    @Schema(description = "操作系统")
    private String os;
    @Schema(description = "操作结果")
    private String result;
    @Schema(description = "操作模块（见OPERATE_MODULE）")
    private String module;
    @Schema(description = "操作子模块（见OPERATE_MODULE）")
    private String subModule;
    @Schema(description = "操作类型（见OPERATE_TYPE）")
    private String type;
    @Schema(description = "操作前的JSON，敏感字段要加密")
    private String contextOld;
    @Schema(description = "操作后的JSON，敏感字段要加密")
    private String context;
    @Schema(description = "操作标识（字段、单号等）")
    private String sign;
    @Schema(description = "备注")
    private String remark;
}
