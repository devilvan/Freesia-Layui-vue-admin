package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.account.converter.AccountReportConverter;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.mapper.AccountReportMapper;
import com.freesia.account.po.AccountReportPo;
import com.freesia.account.repository.AccountReportRepository;
import com.freesia.account.service.AccountReportService;
import com.freesia.account.vo.AccountReportVo;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author Evad.Wu
 * @Description 记账报表表 业务逻辑类
 * @date 2026-02-25
 */
@Service
@RequiredArgsConstructor
public class AccountReportServiceImpl extends BaseServiceImpl<AccountReportMapper, AccountReportVo, AccountReportDto, AccountReportPo> implements AccountReportService {
    private final AccountReportRepository accountReportRepository;
    private final AccountReportConverter accountReportConverter;
    private final AccountReportMapper accountReportMapper;

    @Override
    protected MapStructConverter<AccountReportVo, AccountReportDto, AccountReportPo> getMapStructConverter() {
        return accountReportConverter;
    }

    @Override
    protected JpaRepository<AccountReportPo, Long> getRepository() {
        return accountReportRepository;
    }

    @Override
    protected Class<AccountReportDto> getDtoClass() {
        return AccountReportDto.class;
    }

    @Override
    protected Class<AccountReportPo> getPoClass() {
        return AccountReportPo.class;
    }

    @Override
    protected Wrapper<AccountReportPo> buildQueryWrapper(@NonNull AccountReportDto accountReportDto) {
        return new LambdaQueryWrapper<AccountReportPo>()
                .eq(AccountReportPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountReportDto.getId()), AccountReportPo::getId, accountReportDto.getId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getRemark()), AccountReportPo::getRemark, accountReportDto.getRemark())
                .eq(UEmpty.isNotEmpty(accountReportDto.getUserId()), AccountReportPo::getUserId, accountReportDto.getUserId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBudgetId()), AccountReportPo::getBudgetId, accountReportDto.getBudgetId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getStrategyId()), AccountReportPo::getStrategyId, accountReportDto.getStrategyId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getTitle()), AccountReportPo::getTitle, accountReportDto.getTitle())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBudgetType()), AccountReportPo::getBudgetType, accountReportDto.getBudgetType())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBudgetAmount()), AccountReportPo::getBudgetAmount, accountReportDto.getBudgetAmount())
                .eq(UEmpty.isNotEmpty(accountReportDto.getOutlay()), AccountReportPo::getOutlay, accountReportDto.getOutlay())
                .eq(UEmpty.isNotEmpty(accountReportDto.getIncomeAmount()), AccountReportPo::getIncomeAmount, accountReportDto.getIncomeAmount())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBillingTime()), AccountReportPo::getBillingTime, accountReportDto.getBillingTime())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBillingTimeFrom()), AccountReportPo::getBillingTimeFrom, accountReportDto.getBillingTimeFrom())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBillingTimeTo()), AccountReportPo::getBillingTimeTo, accountReportDto.getBillingTimeTo())
                .eq(UEmpty.isNotEmpty(accountReportDto.getRecalculateFlag()), AccountReportPo::getRecalculateFlag, accountReportDto.getRecalculateFlag())
                ;
    }

    @Override
    public Boolean findExist(AccountReportDto accountReportDto) {
        return accountReportMapper.findExist(accountReportDto);
    }
}
