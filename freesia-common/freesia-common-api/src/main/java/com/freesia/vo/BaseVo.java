package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description VO通用父类
 * @date 2023-08-24
 */
@Data
@Schema(description = "VO通用父类")
public class BaseVo {
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    @JSONField(format = Constants.YMD_HMS)
    private Date createTime;
    @Schema(description = "修改人")
    private String modifier;
    @Schema(description = "修改时间")
    @JSONField(format = Constants.YMD_HMS)
    private Date modifyTime;
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
    @Schema(description = "版本号")
    private Long recVer;
    @Schema(description = "系统内置（0-否 1-是）")
    private Boolean buildIn;
    @Schema(description = "租户ID")
    private Long tenantId;
}
