package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.account.vo.AccountBillingVo;
import com.freesia.account.dto.AccountBillingDto;
import com.freesia.account.po.AccountBillingPo;
import com.freesia.account.service.AccountBillingService;
import com.freesia.account.converter.AccountBillingConverter;
import com.freesia.account.mapper.AccountBillingMapper;
import com.freesia.account.repository.AccountBillingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单表 业务逻辑类
 * @date 2026-02-17
 */
@Service
@RequiredArgsConstructor
public class AccountBillingServiceImpl extends BaseServiceImpl<AccountBillingMapper, AccountBillingVo, AccountBillingDto, AccountBillingPo> implements AccountBillingService {
    private final AccountBillingRepository accountBillingRepository;
    private final AccountBillingConverter accountBillingConverter;

    @Override
    protected JpaRepository<AccountBillingPo, Long> getRepository() {
    return accountBillingRepository;
    }

    @Override
    protected Class<AccountBillingDto> getDtoClass() {
        return AccountBillingDto.class;
    }

    @Override
    protected Class<AccountBillingPo> getPoClass() {
        return AccountBillingPo.class;
    }

    @Override
    protected Wrapper<AccountBillingPo> buildQueryWrapper(@NonNull AccountBillingDto accountBillingDto) {
        return new LambdaQueryWrapper<AccountBillingPo>()
            .eq(AccountBillingPo::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(accountBillingDto.getId()), AccountBillingPo::getId, accountBillingDto.getId());
    }

    @Override
    protected MapStructConverter<AccountBillingVo, AccountBillingDto, AccountBillingPo> getMapStructConverter() {
        return accountBillingConverter;
    }
}
