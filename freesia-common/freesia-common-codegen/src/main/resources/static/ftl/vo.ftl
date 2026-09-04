package ${packageName}.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 值对象
 * @date ${date}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "${dataBaseDto.comment} 值对象")
public class ${dataBaseDto.className}Vo extends BaseVo {
<#if dataBaseDto.fieldList ??>
    <#list dataBaseDto.fieldList as field>
    @Schema(description = "${field.remark ! ''}")
    @JsonAlias(value = {"${field.fieldName}"})
    private ${field.javaType} ${field.fieldName};
    </#list>
</#if>
}
