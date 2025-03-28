package com.freesia.icon.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import com.freesia.entity.BaseEntity;
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
public class FindPageCommonIconEntity extends BaseEntity {
    @Schema(description = "图标名称")
    private String name;
    @Schema(description = "文件ID")
    private Long fileId;
    @Schema(description = "图标所属分区")
    private String iconPartition;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "URL")
    private String url;
    @Schema(description = "文件大小")
    private String fileSize;

    @Schema(description = "创建时间")
    @JSONField(format = Constants.YMD_HMS)
    private Date createTime;
    @Schema(description = "修改时间")
    @JSONField(format = Constants.YMD_HMS)
    private Date modifyTime;
}
