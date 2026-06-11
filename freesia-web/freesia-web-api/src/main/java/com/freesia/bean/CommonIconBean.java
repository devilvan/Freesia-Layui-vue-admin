package com.freesia.bean;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标表 服务间数据传输对象
 * @date 2026-06-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标表 服务间数据传输对象")
public class CommonIconBean extends BaseDto {
    @Schema(description = "图标名称")
    private String name;
    @Schema(description = "文件ID")
    private Long fileId;
    @Schema(description = "图标所属分区")
    private String iconPartition;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "主键集合")
    private List<Long> idList;
}
