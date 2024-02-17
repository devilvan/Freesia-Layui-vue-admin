package com.freesia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 通用model父类 bean
 * @date 2023-08-17
 */
@Data
@Schema(description = "通用bean父类")
public class BaseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 7123645842241826714L;
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "修改人")
    private String modifier;
    @Schema(description = "修改时间")
    private Date modifyTime;
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
    @Schema(description = "版本号")
    private Long recVer = 0L;
}
