package ${packageName}.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 数据传输对象
 * @date ${date}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "${dataBaseDto.comment} 数据传输对象")
public class ${dataBaseDto.className}Dto extends BaseDto {
<#if dataBaseDto.fieldList ??>
    <#list dataBaseDto.fieldList as field>
    @Schema(description = "${field.remark ! ''}")
    private <#if field.columnType == 'BIT'>Boolean<#else>${field.javaType}</#if> ${field.fieldName};
    </#list>
</#if>
}
