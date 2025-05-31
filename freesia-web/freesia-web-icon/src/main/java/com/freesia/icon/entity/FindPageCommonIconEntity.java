package com.freesia.icon.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.dto.BaseDto;
import com.freesia.oss.annotation.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Bliss.Wu
 * @Description 查询通用图标表分页信息 实体类
 * @date 2025-03-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageCommonIconEntity extends BaseDto {
    @Schema(description = "图标名称")
    private String name;
    @Schema(description = "文件ID")
    private Long fileId;
    @Schema(description = "文件名称")
    private String fileName;
    @Schema(description = "图标所属分区")
    private String iconPartition;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "备注")
    private String remark;
    @Domain
    @Schema(description = "URL")
    private String url;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date createTime;
    @Schema(description = "修改时间")
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date modifyTime;
}
