package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.account.converter.AccountReportStrategyConverter;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.AccountReportStrategyDto;
import com.freesia.account.exception.AccountException;
import com.freesia.account.mapper.AccountReportStrategyMapper;
import com.freesia.account.po.AccountReportStrategyPo;
import com.freesia.account.repository.AccountReportStrategyRepository;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.service.AccountReportStrategyService;
import com.freesia.account.vo.AccountReportStrategyVo;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 记账报表策略表 业务逻辑类
 * @date 2026-02-25
 */
@Service
@RequiredArgsConstructor
public class AccountReportStrategyServiceImpl extends BaseServiceImpl<AccountReportStrategyMapper, AccountReportStrategyVo, AccountReportStrategyDto, AccountReportStrategyPo> implements AccountReportStrategyService {
    private final AccountReportStrategyRepository accountReportStrategyRepository;
    private final AccountReportStrategyConverter accountReportStrategyConverter;
    private final AccountBudgetService accountBudgetService;

    @Override
    protected MapStructConverter<AccountReportStrategyVo, AccountReportStrategyDto, AccountReportStrategyPo> getMapStructConverter() {
        return accountReportStrategyConverter;
    }

    @Override
    protected JpaRepository<AccountReportStrategyPo, Long> getRepository() {
    return accountReportStrategyRepository;
    }

    @Override
    protected Class<AccountReportStrategyDto> getDtoClass() {
        return AccountReportStrategyDto.class;
    }

    @Override
    protected Class<AccountReportStrategyPo> getPoClass() {
        return AccountReportStrategyPo.class;
    }

    @Override
    protected Wrapper<AccountReportStrategyPo> buildQueryWrapper(@NonNull AccountReportStrategyDto accountReportStrategyDto) {
        return new LambdaQueryWrapper<AccountReportStrategyPo>()
                .eq(AccountReportStrategyPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getId()), AccountReportStrategyPo::getId, accountReportStrategyDto.getId())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getRemark()), AccountReportStrategyPo::getRemark, accountReportStrategyDto.getRemark())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getUserId()), AccountReportStrategyPo::getUserId, accountReportStrategyDto.getUserId())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getBudgetId()), AccountReportStrategyPo::getBudgetId, accountReportStrategyDto.getBudgetId())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getBudgetType()), AccountReportStrategyPo::getBudgetType, accountReportStrategyDto.getBudgetType())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getGenerateTime()), AccountReportStrategyPo::getGenerateTime, accountReportStrategyDto.getGenerateTime())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getNextGenerateTime()), AccountReportStrategyPo::getNextGenerateTime, accountReportStrategyDto.getNextGenerateTime())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getEnabled()), AccountReportStrategyPo::getEnabled, accountReportStrategyDto.getEnabled())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getWeekBegin()), AccountReportStrategyPo::getWeekBegin, accountReportStrategyDto.getWeekBegin())
                .eq(UEmpty.isNotEmpty(accountReportStrategyDto.getRecalculateFlag()), AccountReportStrategyPo::getRecalculateFlag, accountReportStrategyDto.getRecalculateFlag())
                ;
    }

    @Override
    public AccountReportStrategyDto saveUpdate(AccountReportStrategyDto dto) {
        if (UEmpty.isNull(dto.getId())) {
            // 新增场景，校验是否重复
            Optional.ofNullable(findOne(dto)).ifPresent(accountReportStrategyPo -> {
                throw new AccountException("account.report.strategy.exists", new Object[]{dto.getBudgetType()});
            });
        }
        // 根据用户ID+租户ID+预算类型，查询是否已经配置过预算
        AccountBudgetDto accountBudgetDto = new AccountBudgetDto();
        accountBudgetDto.setUserId(dto.getUserId());
        accountBudgetDto.setTenantId(dto.getTenantId());
        accountBudgetDto.setBudgetType(dto.getBudgetType());
        accountBudgetDto = accountBudgetService.findOne(accountBudgetDto);
        if (UEmpty.isNull(accountBudgetDto)) {
            throw new AccountException("account.budget.not.setup");
        }
        dto.setBudgetId(accountBudgetDto.getId());
        return super.saveUpdate(dto);
    }
}
