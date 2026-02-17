package com.freesia.account.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.account.dto.AccountBillingStrategyDto;
import com.freesia.account.po.AccountBillingStrategyPo;
import com.freesia.account.vo.AccountBillingStrategyVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 MapStruct转换器
 * @date 2026-02-17
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountBillingStrategyConverter extends MapStructConverter<AccountBillingStrategyVo, AccountBillingStrategyDto, AccountBillingStrategyPo> {
}
