import {BaseEntity, BaseVo} from "@/types/common";

export interface ${dataBaseDto.className}Vo extends BaseVo {
<#list dataBaseDto.fieldList as field>
    ${field.fieldName}?: <#if field.columnType == 'BIT'>boolean<#elseif field.columnType == 'DATE'>Date<#elseif field.columnType == 'INT' || field.columnType == 'BIGINT' || field.columnType == 'DECIMAL' || field.columnType == 'NUMERIC'>number<#else>string</#if>;
</#list>
}

export interface ${dataBaseDto.className}Entity extends BaseEntity {
<#list dataBaseDto.fieldList as field>
    ${field.fieldName}?: <#if field.columnType == 'BIT'>boolean<#elseif field.columnType == 'DATE'>Date<#elseif field.columnType == 'INT' || field.columnType == 'BIGINT' || field.columnType == 'DECIMAL' || field.columnType == 'NUMERIC'>number<#else>string</#if>;
</#list>
}