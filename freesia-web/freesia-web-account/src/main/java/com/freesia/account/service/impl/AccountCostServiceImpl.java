package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.mapper.AccountCostMapper;
import com.freesia.account.po.AccountCostPo;
import com.freesia.account.repository.AccountCostRepository;
import com.freesia.account.service.AccountCostService;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销表 业务逻辑类
 * @date 2024-12-14
 */
@Service
@RequiredArgsConstructor
public class AccountCostServiceImpl extends ServiceImpl<AccountCostMapper, AccountCostPo> implements AccountCostService {
    private final AccountCostRepository accountCostRepository;

    @Override
    public AccountCostDto saveUpdate(AccountCostDto accountCostDto) {
        AccountCostPo accountCostPo = new AccountCostPo();
        UCopy.fullCopy(accountCostDto, accountCostPo);
        return UCopy.copyPo2Dto(accountCostRepository.saveAndFlush(accountCostPo), AccountCostDto.class);
    }

    @Override
    public List<AccountCostDto> saveUpdateBatch(List<AccountCostDto> list) {
        List<AccountCostPo> accountCostPoList = UCopy.fullCopyList(list, AccountCostPo.class);
        return UCopy.fullCopyList(accountCostRepository.saveAllAndFlush(accountCostPoList), AccountCostDto.class);
    }

    @Override
    public TableResult<AccountCostDto> findPageAccountCost(AccountCostDto accountCost, PageQuery pageQuery) {
        LambdaQueryWrapper<AccountCostPo> wrapper = new LambdaQueryWrapper<AccountCostPo>()
                .eq(AccountCostPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCost.getId()), AccountCostPo::getId, accountCost.getId())
                .like(UEmpty.isNotEmpty(accountCost.getCostDesc()), AccountCostPo::getCostDesc, accountCost.getCostDesc())
                .like(UEmpty.isNotEmpty(accountCost.getRemark()), AccountCostPo::getRemark, accountCost.getRemark())
                .eq(UEmpty.isNotEmpty(accountCost.getPaymentSign()), AccountCostPo::getPaymentSign, accountCost.getPaymentSign())
                .between(UEmpty.isNotEmpty(accountCost.getPaymentTimeFrom()) && UEmpty.isNotEmpty(accountCost.getPaymentTimeTo()),
                        AccountCostPo::getPaymentTime,
                        accountCost.getPaymentTimeFrom(),
                        accountCost.getPaymentTimeTo())
                .orderByDesc(AccountCostPo::getPaymentTime);
        Page<AccountCostPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, AccountCostDto.class));
    }

    @Override
    public AccountCostDto findAccountCost(AccountCostDto accountCost) {
        Long tenantId = USecurity.getTenantId();
        LambdaQueryWrapper<AccountCostPo> wrapper = new LambdaQueryWrapper<AccountCostPo>()
                .eq(AccountCostPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCost.getId()), AccountCostPo::getId, accountCost.getId())
                .eq(UEmpty.isNotEmpty(tenantId), AccountCostPo::getTenantId, tenantId);
        return UCopy.copyPo2Dto(getOne(wrapper), AccountCostDto.class);
    }

    @Override
    @Transactional
    public void deleteAccountCost(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
