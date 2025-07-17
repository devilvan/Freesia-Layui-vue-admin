package com.freesia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description Entity通用父类
 * @date 2023-08-31
 */
@Data
@Schema(description = "Entity通用父类")
public class BaseEntity {
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "版本号")
    private Long recVer;
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
}
