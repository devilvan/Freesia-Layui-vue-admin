package com.freesia.account.converter;

import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.po.AccountCostUserAllocPo;
import com.freesia.account.vo.AccountCostUserAllocVo;
import com.freesia.convert.MapStructConverter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 记账-费用分摊 转换器
 * @date 2026-01-17
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountCostUserAllocConverter extends MapStructConverter<AccountCostUserAllocVo, AccountCostUserAllocDto, AccountCostUserAllocPo> {
}
