package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.account.converter.AccountBillingStrategyConverter;
import com.freesia.account.dto.AccountBillingStrategyDto;
import com.freesia.account.mapper.AccountBillingStrategyMapper;
import com.freesia.account.po.AccountBillingStrategyPo;
import com.freesia.account.repository.AccountBillingStrategyRepository;
import com.freesia.account.service.AccountBillingStrategyService;
import com.freesia.account.vo.AccountBillingStrategyVo;
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
 * @Description 记账账单策略表 业务逻辑类
 * @date 2026-02-17
 */
@Service
@RequiredArgsConstructor
public class AccountBillingStrategyServiceImpl extends BaseServiceImpl<AccountBillingStrategyMapper, AccountBillingStrategyVo, AccountBillingStrategyDto, AccountBillingStrategyPo> implements AccountBillingStrategyService {
    private final AccountBillingStrategyRepository accountBillingStrategyRepository;
    private final AccountBillingStrategyConverter accountBillingStrategyConverter;

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
            .eq(UEmpty.isNotEmpty(accountBillingStrategyDto.getId()), AccountBillingStrategyPo::getId, accountBillingStrategyDto.getId());
    }

    @Override
    protected MapStructConverter<AccountBillingStrategyVo, AccountBillingStrategyDto, AccountBillingStrategyPo> getMapStructConverter() {
        return accountBillingStrategyConverter;
    }
}
