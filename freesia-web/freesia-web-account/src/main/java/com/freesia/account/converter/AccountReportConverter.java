package com.freesia.account.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.po.AccountReportPo;
import com.freesia.account.vo.AccountReportVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表表 MapStruct转换器
 * @date 2026-02-25
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountReportConverter extends MapStructConverter<AccountReportVo, AccountReportDto, AccountReportPo> {
}
