package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.account.converter.AccountBillingStrategyConverter;
import com.freesia.account.dto.AccountBillingStrategyDto;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.exception.AccountException;
import com.freesia.account.mapper.AccountBillingStrategyMapper;
import com.freesia.account.po.AccountBillingStrategyPo;
import com.freesia.account.repository.AccountBillingStrategyRepository;
import com.freesia.account.service.AccountBillingStrategyService;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.vo.AccountBillingStrategyVo;
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
 * @Description 记账账单策略表 业务逻辑类
 * @date 2026-02-21
 */
@Service
@RequiredArgsConstructor
public class AccountBillingStrategyServiceImpl extends BaseServiceImpl<AccountBillingStrategyMapper, AccountBillingStrategyVo, AccountBillingStrategyDto, AccountBillingStrategyPo> implements AccountBillingStrategyService {
    private final AccountBillingStrategyRepository accountBillingStrategyRepository;
    private final AccountBillingStrategyConverter accountBillingStrategyConverter;
    private final AccountBudgetService accountBudgetService;

    @Override
    protected MapStructConverter<AccountBillingStrategyVo, AccountBillingStrategyDto, AccountBillingStrategyPo> getMapStructConverter() {
        return accountBillingStrategyConverter;
    }

    @Override
    protected JpaRepository<AccountBillingStrategyPo, Long> getRepository() {
        return accountBillingStrategyRepository;
    }

    @Override
    protected Class<AccountBillingStrategyDto> getDtoClass() {
        return AccountBillingStrategyDto.class;
    }

    @Override
    protected Class<AccountBillingStrategyPo> getPoClass() {
        return AccountBillingStrategyPo.class;
    }

    @Override
    protected Wrapper<AccountBillingStrategyPo> buildQueryWrapper(@NonNull AccountBillingStrategyDto accountBillingStrategyDto) {
        return new LambdaQueryWrapper<AccountBillingStrategyPo>()
                .eq(AccountBillingStrategyPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getId()), AccountBillingStrategyPo::getId, accountBillingStrategyDto.getId())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getRemark()), AccountBillingStrategyPo::getRemark, accountBillingStrategyDto.getRemark())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getUserId()), AccountBillingStrategyPo::getUserId, accountBillingStrategyDto.getUserId())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getTenantId()), AccountBillingStrategyPo::getTenantId, accountBillingStrategyDto.getTenantId())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getBudgetId()), AccountBillingStrategyPo::getBudgetId, accountBillingStrategyDto.getBudgetId())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getBudgetType()), AccountBillingStrategyPo::getBudgetType, accountBillingStrategyDto.getBudgetType())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getGenerateTime()), AccountBillingStrategyPo::getGenerateTime, accountBillingStrategyDto.getGenerateTime())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getNextGenerateTime()), AccountBillingStrategyPo::getNextGenerateTime, accountBillingStrategyDto.getNextGenerateTime())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getEnabled()), AccountBillingStrategyPo::getEnabled, accountBillingStrategyDto.getEnabled())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getWeekBegin()), AccountBillingStrategyPo::getWeekBegin, accountBillingStrategyDto.getWeekBegin())
                .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getRecalculateFlag()), AccountBillingStrategyPo::getRecalculateFlag, accountBillingStrategyDto.getRecalculateFlag())
                ;
    }

    @Override
    public AccountBillingStrategyDto saveUpdate(AccountBillingStrategyDto dto) {
        if (UEmpty.isNull(dto.getId())) {
            // 新增场景，校验是否重复
            Optional.ofNullable(findOne(dto)).ifPresent(accountBillingStrategyPo -> {
                throw new AccountException("account.billing.strategy.exists", new Object[]{dto.getBudgetType()});
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
