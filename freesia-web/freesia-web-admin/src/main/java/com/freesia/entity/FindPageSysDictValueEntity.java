package com.freesia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询字典值数据 持久层传输类
 * @date 2023-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FindPageSysDictValueEntity extends BaseEntity {
    @Schema(description = "字典值ID")
    private Long valueId;
    @Schema(description = "字典值名")
    private String valueName;
    @Schema(description = "内部排序")
    private Integer orderNum;
    @Schema(description = "是否默认（0-否，1-是）")
    private String isDefault;
    @Schema(description = "状态")
    private String valueStatus;

}
