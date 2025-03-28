package com.freesia.icon.entity;

import com.freesia.dto.SysOssDto;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Bliss.Wu
 * @Description 保存 实体类
 * @date 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保存 实体类")
public class CommonIconSaveUpdateEntity extends BaseVo {
    /**
     * 文件信息
     */
    @Schema(description = "文件信息")
    private List<SysOssDto> sysOssDtoList;
    /**
     * 保存的数据
     */
    @Schema(description = "保存的数据")
    private CommonIconDto commonIconDto;
}
