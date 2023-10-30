package ${packageName}.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 映射
 * @date ${date}
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "${dataBaseDto.tableName}")

@Entity
@Table(name = "${dataBaseDto.tableName}")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "${dataBaseDto.comment} 映射")
public class ${dataBaseDto.className}Po extends BasePo implements Serializable {
<#if dataBaseDto.fieldList ??>
    <#list dataBaseDto.fieldList as field>
    @Schema(description = "${field.remark ! ''}")
    @TableField(value = "${field.columnName}")
    @Column(name = "${field.columnName}", columnDefinition = "<#if field.javaType != 'Date'>${field.columnType}(${field.dataSize}) <#else>${field.columnType} </#if><#if field.nullable = false>NOT NULL </#if>COMMENT '${field.remark}'")
    private <#if field.columnType == 'BIT'>Boolean<#else>${field.javaType}</#if> ${field.fieldName};
    </#list>
</#if>
}
