package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SysSensitiveLogVo extends BaseVo {
    @Schema(description = "操作人姓名")
    @JsonAlias(value = {"operatorName"})
    private String operatorName;
    @Schema(description = "部门名称")
    @JsonAlias(value = {"deptName"})
    private String deptName;
    @Schema(description = "请求类型")
    @JsonAlias(value = {"methodType"})
    private String methodType;
    @Schema(description = "URL")
    @JsonAlias(value = {"url"})
    private String url;
    @Schema(description = "操作时间从")
    @JsonAlias(value = {"operateTimeFrom"})
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date operateTimeFrom;
    @Schema(description = "操作时间到")
    @JsonAlias(value = {"operateTimeTo"})
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date operateTimeTo;
    @Schema(description = "操作模块（见OPERATE_MODULE）")
    @JsonAlias(value = {"module"})
    private String module;
    @Schema(description = "操作子模块（见OPERATE_MODULE）")
    @JsonAlias(value = {"subModule"})
    private String subModule;
    @Schema(description = "操作类型（见OPERATE_TYPE）")
    @JsonAlias(value = {"type"})
    private String type;
    @Schema(description = "操作结果")
    @JsonAlias(value = {"ok"})
    private String result;
    @Schema(description = "操作标识（字段、单号等）")
    @JsonAlias(value = {"sign"})
    private String sign;
}
