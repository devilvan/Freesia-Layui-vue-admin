package com.freesia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查询字典键数据 持久层传输类
 * {@link com.freesia.controller.SysDictController#findPageSysDictList}
 * @date 2023-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FindPageSysDictKeyEntity extends BaseEntity {
    @Schema(description = "字典键ID")
    private Long keyId;
    @Schema(description = "字典键名")
    private String keyName;
    @Schema(description = "字典键")
    private String dictKey;
    @Schema(description = "状态")
    private String keyStatus;
    @Schema(description = "字典值集合")
    private List<FindPageSysDictValueEntity> children;
}
