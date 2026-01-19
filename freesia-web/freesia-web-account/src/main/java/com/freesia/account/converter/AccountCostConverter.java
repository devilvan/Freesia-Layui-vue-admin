package com.freesia.account.converter;

import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.dto.FindCostLineChartDto;
import com.freesia.account.dto.FindRankByCostTypeDto;
import com.freesia.account.po.AccountCostPo;
import com.freesia.account.vo.*;
import com.freesia.convert.MapStructConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 记账 转换器
 * @date 2026-01-17
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountCostConverter extends MapStructConverter<AccountCostVo, AccountCostDto, AccountCostPo> {
    @Override
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentTimeTo", ignore = true)
    @Mapping(target = "paymentTimeFrom", ignore = true)
    @Mapping(target = "dateScope", ignore = true)
    @Mapping(target = "accountCostUserAllocDtoList", source = "accountCostUserAllocVoList")
    AccountCostDto convertVo2Dto(AccountCostVo source);

    @Override
    AccountCostPo convertDto2Po(AccountCostDto source);

    @Override
    @Mapping(target = "paymentTimeTo", ignore = true)
    @Mapping(target = "paymentTimeFrom", ignore = true)
    @Mapping(target = "dateScope", ignore = true)
    @Mapping(target = "costTypeList", ignore = true)
    @Mapping(target = "accountCostUserIdList", ignore = true)
    @Mapping(target = "accountCostUserAllocDtoList", ignore = true)
    AccountCostDto convertPo2Dto(AccountCostPo source);

    @Mapping(target = "year", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentTimeTo", ignore = true)
    @Mapping(target = "paymentTimeFrom", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "accountCostUserAllocDtoList", ignore = true)
    FindCostLineChartDto convertFindCostLineChartVo2Dto(FindCostLineChartVo findCostLineChartVo);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentTimeTo", ignore = true)
    @Mapping(target = "paymentTimeFrom", ignore = true)
    @Mapping(target = "dateScope", ignore = true)
    @Mapping(target = "accountCostUserAllocDtoList", ignore = true)
    AccountCostDto convertFindCostSumCalendarNearYearVo2Dto(FindCostSumCalendarNearYearVo findCostSumCalendarNearYearVo);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentTimeTo", ignore = true)
    @Mapping(target = "paymentTimeFrom", ignore = true)
    @Mapping(target = "accountCostUserAllocDtoList", ignore = true)
    FindRankByCostTypeDto convertFindRankByCostTypeVo2Dto(FindRankByCostTypeVo findRankByCostTypeVo);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "recVer", ignore = true)
    @Mapping(target = "paymentTimeTo", ignore = true)
    @Mapping(target = "paymentTimeFrom", ignore = true)
    @Mapping(target = "paymentTime", ignore = true)
    @Mapping(target = "paymentSign", ignore = true)
    @Mapping(target = "outlay", ignore = true)
    @Mapping(target = "modifyTime", ignore = true)
    @Mapping(target = "modifier", ignore = true)
    @Mapping(target = "logicDel", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "icon", ignore = true)
    @Mapping(target = "dateScope", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "costTypeList", ignore = true)
    @Mapping(target = "costType", ignore = true)
    @Mapping(target = "buildIn", ignore = true)
    @Mapping(target = "accountCostUserIdList", ignore = true)
    @Mapping(target = "accountCostUserAllocDtoList", ignore = true)
    AccountCostDto convertFindCacheCostTypeVo2Dto(FindCacheCostTypeVo findCacheCostTypeVo);
}
