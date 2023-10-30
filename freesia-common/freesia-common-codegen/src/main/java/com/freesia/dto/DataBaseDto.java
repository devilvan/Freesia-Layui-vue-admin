package com.freesia.dto;

import com.devilvan.betrice.pojo.BetriceCgField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 数据库表信息 数据传输对象
 * @date 2022-09-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据库表信息 数据传输对象")
public class DataBaseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -179434144799166939L;
    @Schema(description = "表名")
    private String tableName;
    @Schema(description = "类名")
    private String className;
    @Schema(description = "表描述")
    private String comment;
    @Schema(description = "字段值对象")
    private List<BetriceCgField> fieldList;

    public DataBaseDto(String tableName, String className, String comment) {
        this.tableName = tableName;
        this.className = className;
        this.comment = comment;
    }
}
