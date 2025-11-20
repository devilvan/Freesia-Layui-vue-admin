<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${packageName}.mapper.${dataBaseDto.className}Mapper">
    <resultMap id="BaseResultMap" type="${packageName}.po.${dataBaseDto.className}Po">
        <id column="ID" property="id" />
        <result column="CREATOR" property="creator" />
        <result column="CREATE_TIME" property="createTime" />
        <result column="MODIFIER" property="modifier" />
        <result column="MODIFY_TIME" property="modifyTime" />
        <result column="LOGIC_DEL" property="logicDel" />
        <result column="REC_VER" property="recVer" />
        <result column="BUILD_IN" property="buildIn" />
        <result column="TENANT_ID" property="tenantId" />
        <#list dataBaseDto.fieldList as field>
        <result column="${field.columnName}" property="${field.fieldName}" />
        </#list>
    </resultMap>
    <sql id="Base_Column_List">
        ID, CREATOR, CREATE_TIME, MODIFIER, MODIFY_TIME, LOGIC_DEL, REC_VER, BUILD_IN, TENANT_ID,<#list dataBaseDto.fieldList as field>${field.columnName}<#if field_has_next>, </#if></#list>
    </sql>
    <select id="findPage${dataBaseDto.className}" resultMap="BaseResultMap">
        SELECT
        <include refid="Base_Column_List"/>
        FROM ${dataBaseDto.tableName}
        WHERE
        LOGIC_DEL = 0
        <#list dataBaseDto.fieldList as field>
        <#if field.javaType == "String">
        <if test="dto.${field.fieldName} != null and dto.${field.fieldName} != ''">
            AND ${field.columnName} = <#noparse>#{</#noparse>dto.${field.fieldName}<#noparse>}</#noparse>
        </if>
        <#else>
        <if test="dto.${field.fieldName} != null">
            AND ${field.columnName} = <#noparse>#{</#noparse>dto.${field.fieldName}<#noparse>}</#noparse>
        </if>
        </#if>
        </#list>
        ORDER BY create_time DESC
    </select>
    <select id="findList${dataBaseDto.className}" resultMap="BaseResultMap">
        SELECT
        <include refid="Base_Column_List"/>
        FROM ${tableName}
        WHERE LOGIC_DEL = 0
        <#list dataBaseDto.fieldList as field>
        <#if field.javaType == "String">
        <if test="dto.${field.fieldName} != null and dto.${field.fieldName} != ''">
            AND ${field.columnName} = <#noparse>#{</#noparse>dto.${field.fieldName}<#noparse>}</#noparse>
        </if>
        <#else>
        <if test="dto.${field.fieldName} != null">
            AND ${field.columnName} = <#noparse>#{</#noparse>dto.${field.fieldName}<#noparse>}</#noparse>
        </if>
        </#if>
        </#list>
        ORDER BY MODIFY_TIME DESC
    </select>
    <insert id="insertBatch" parameterType="java.util.List">
        INSERT INTO ${dataBaseDto.tableName}
        (
        <#list dataBaseDto.fieldList as field>
        <#if field.columnName != 'ID'>
            ${field.columnName}<#if field_has_next>, </#if>
        </#if>
        </#list>
        )
        VALUES
        <foreach collection="list" item="item" separator=",">
            (
            <#list dataBaseDto.fieldList as field>
            <#if field.columnName != 'ID'>
                <#noparse>#{</#noparse>item.${field.fieldName}<#noparse>}</#noparse><#if field_has_next>, </#if>
            </#if>
            </#list>
            )
        </foreach>
    </insert>
    <update id="updateBatch" parameterType="java.util.List">
        <foreach collection="list" item="item" separator=";">
            UPDATE ${dataBaseDto.tableName}
            <set>
<#list dataBaseDto.fieldList as field>
    <#if field.columnName != 'ID'>
        <#if field.javaType == "String">
            <if test="item.${field.fieldName} != null and item.${field.fieldName} != ''">
                ${field.columnName} = <#noparse>#{</#noparse>item.${field.fieldName}<#noparse>}</#noparse>,
            </if>
        <#else>
            <if test="item.${field.fieldName} != null">
                ${field.columnName} = <#noparse>#{</#noparse>item.${field.fieldName}<#noparse>}</#noparse>,
            </if>
        </#if>
    </#if>
</#list>
            MODIFY_TIME = NOW()
            </set>
            WHERE LOGIC_DEL = 0 AND ID = <#noparse>#{</#noparse>item.id<#noparse>}</#noparse>
        </foreach>
    </update>
</mapper>
