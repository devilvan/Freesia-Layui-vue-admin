package ${packageName}.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 值对象
 * @date ${date}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "${dataBaseDto.comment} 值对象")
public class ${dataBaseDto.className}Vo {
<#if dataBaseDto.fieldList ??>
    <#list dataBaseDto.fieldList as field>
    @Schema(description = "${field.remark ! ''}")
    @JSONField(alternateNames = {"${field.fieldName}"})
    private <#if field.columnType == 'BIT'>Boolean<#else>${field.javaType}</#if> ${field.fieldName};
    </#list>
</#if>
}
