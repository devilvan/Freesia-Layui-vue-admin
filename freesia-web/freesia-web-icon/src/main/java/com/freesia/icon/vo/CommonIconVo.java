package com.freesia.icon.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标表 值对象
 * @date 2025-03-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标表 值对象")
public class CommonIconVo extends BaseVo {
    @Schema(description = "图标名称")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "文件ID")
    @JsonAlias(value = {"fileId"})
    private Long fileId;
    @Schema(description = "图标所属分区")
    @JsonAlias(value = {"iconPartition"})
    private String iconPartition;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "主键集合")
    @JsonAlias(value = {"idList"})
    private List<Long> idList;
}
