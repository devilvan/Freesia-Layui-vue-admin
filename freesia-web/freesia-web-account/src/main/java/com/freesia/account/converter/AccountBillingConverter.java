package com.freesia.account.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.account.dto.AccountBillingDto;
import com.freesia.account.po.AccountBillingPo;
import com.freesia.account.vo.AccountBillingVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单表 MapStruct转换器
 * @date 2026-02-17
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountBillingConverter extends MapStructConverter<AccountBillingVo, AccountBillingDto, AccountBillingPo> {
}
