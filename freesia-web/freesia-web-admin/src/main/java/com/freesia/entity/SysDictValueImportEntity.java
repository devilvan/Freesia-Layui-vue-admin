package com.freesia.entity;

import com.freesia.excel.pojo.BaseImportEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author Evad.Wu
 * @Description 字典键导入 传输实体
 * {@link com.freesia.controller.SysDictController#importSysDictValue}
 * @date 2024-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictValueImportEntity extends BaseImportEntity {
    @NotEmpty(message = "{not.empty}")
    @Schema(description = "字典值名")
    private String valueName;
    @NotEmpty(message = "{not.empty}")
    @Schema(description = "字典值")
    private String value;
}
