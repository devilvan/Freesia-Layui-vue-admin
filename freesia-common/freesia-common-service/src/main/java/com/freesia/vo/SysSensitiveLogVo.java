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
 * @Description 敏感操作信息表 值对象
 * @date 2023-08-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "敏感操作信息表 值对象")
public class SysSensitiveLogVo {
    @Schema(description = "操作人姓名")
    @JSONField(alternateNames = {"operatorName"})
    private String operatorName;
    @Schema(description = "部门名称")
    @JSONField(alternateNames = {"deptName"})
    private String deptName;
    @Schema(description = "请求类型")
    @JSONField(alternateNames = {"methodType"})
    private String methodType;
    @Schema(description = "URL")
    @JSONField(alternateNames = {"url"})
    private String url;
    @Schema(description = "操作时间从")
    @JSONField(alternateNames = {"operateTimeFrom"}, format = Constants.YMD_HMS)
    private Date operateTimeFrom;
    @Schema(description = "操作时间到")
    @JSONField(alternateNames = {"operateTimeTo"}, format = Constants.YMD_HMS)
    private Date operateTimeTo;
    @Schema(description = "操作模块（见OPERATE_MODULE）")
    @JSONField(alternateNames = {"module"})
    private String module;
    @Schema(description = "操作子模块（见OPERATE_MODULE）")
    @JSONField(alternateNames = {"subModule"})
    private String subModule;
    @Schema(description = "操作类型（见OPERATE_TYPE）")
    @JSONField(alternateNames = {"type"})
    private String type;
    @Schema(description = "操作结果")
    @JSONField(alternateNames = {"ok"})
    private String result;
    @Schema(description = "操作标识（字段、单号等）")
    @JSONField(alternateNames = {"sign"})
    private String sign;
}
