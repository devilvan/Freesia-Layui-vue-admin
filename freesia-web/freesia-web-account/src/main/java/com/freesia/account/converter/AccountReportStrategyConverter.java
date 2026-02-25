package com.freesia.account.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.account.dto.AccountReportStrategyDto;
import com.freesia.account.po.AccountReportStrategyPo;
import com.freesia.account.vo.AccountReportStrategyVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表策略表 MapStruct转换器
 * @date 2026-02-25
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountReportStrategyConverter extends MapStructConverter<AccountReportStrategyVo, AccountReportStrategyDto, AccountReportStrategyPo> {
}
