package com.freesia.icon.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 通用图标表 值对象
 * @date 2025-03-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用图标表 值对象")
public class CommonIconVo extends BaseVo {
    @Schema(description = "图标名称")
    @JSONField(alternateNames = {"name"})
    private String name;
    @Schema(description = "文件ID")
    @JSONField(alternateNames = {"fileId"})
    private Long fileId;
    @Schema(description = "图标所属分区")
    @JSONField(alternateNames = {"iconPartition"})
    private String iconPartition;
    @Schema(description = "排序")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
