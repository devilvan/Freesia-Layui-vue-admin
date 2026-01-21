package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.AssignTenantDto;
import com.freesia.dto.SysTenantDto;
import com.freesia.entity.FindSysTenantEntity;
import com.freesia.po.SysTenantPo;
import com.freesia.vo.AssignTenantVo;
import com.freesia.vo.SysTenantVo;
import org.mapstruct.*;

/**
 * @author Evad.Wu
 * @Description 租户 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysTenantConverter extends MapStructConverter<SysTenantVo, SysTenantDto, SysTenantPo> {
    FindSysTenantEntity convertPo2FindEntity(SysTenantPo sysTenantPo);

    @Mapping(target = "tenantId", source = "tenantId", qualifiedByName = "stringToLong")
    AssignTenantDto convertAssignTenantVo2Dto(AssignTenantVo assignTenantVo);

    @Named("stringToLong")
    default Long stringToLong(String tenantId) {
        return tenantId != null ? Long.parseLong(tenantId) : null;
    }

    @Mapping(target = "sysTenantUserPoSet", ignore = true)
    void updateSysTenantDto2Po(SysTenantDto sysTenantDto, @MappingTarget SysTenantPo sysTenantPo);
}
