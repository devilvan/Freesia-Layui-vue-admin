package com.freesia.account.converter;

import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.FindBudgetCapacityDto;
import com.freesia.account.po.AccountBudgetPo;
import com.freesia.account.vo.AccountBudgetVo;
import com.freesia.account.vo.FindBudgetCapacityVo;
import com.freesia.convert.MapStructConverter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 记账-预算 转换器
 * @date 2026-01-17
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountBudgetConverter extends MapStructConverter<AccountBudgetVo, AccountBudgetDto, AccountBudgetPo> {
    FindBudgetCapacityDto convertFindBudgetCapacityVo2Dto(FindBudgetCapacityVo findBudgetCapacityVo);
}
