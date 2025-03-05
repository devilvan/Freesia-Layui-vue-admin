package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.mapper.AccountBudgetMapper;
import com.freesia.account.po.AccountBudgetPo;
import com.freesia.account.repository.AccountBudgetRepository;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 业务逻辑类
 * @date 2025-03-04
 */
@Service
@RequiredArgsConstructor
public class AccountBudgetServiceImpl extends ServiceImpl<AccountBudgetMapper, AccountBudgetPo> implements AccountBudgetService {
    private final AccountBudgetRepository accountBudgetRepository;
    private final AccountBudgetMapper accountBudgetMapper;

    @Override
    public AccountBudgetDto saveUpdate(AccountBudgetDto accountBudgetDto) {
        AccountBudgetPo accountBudgetPo = UCopy.copyDto2Po(accountBudgetDto, AccountBudgetPo.class);
        accountBudgetPo = accountBudgetRepository.saveAndFlush(accountBudgetPo);
        return UCopy.copyPo2Dto(accountBudgetPo, AccountBudgetDto.class);
    }

    @Override
    public List<AccountBudgetDto> saveUpdateBatch(List<AccountBudgetDto> list) {
        List<AccountBudgetPo> accountBudgetPoList = UCopy.fullCopyList(list, AccountBudgetPo.class);
        return UCopy.fullCopyList(accountBudgetRepository.saveAllAndFlush(accountBudgetPoList), AccountBudgetDto.class);
    }

    @Override
    public TableResult<AccountBudgetDto> findPageAccountBudget(AccountBudgetDto accountBudget, PageQuery pageQuery) {
        LambdaQueryWrapper<AccountBudgetPo> wrapper = new LambdaQueryWrapper<AccountBudgetPo>()
                .eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountBudget.getId()), AccountBudgetPo::getId, accountBudget.getId());
        Page<AccountBudgetPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, AccountBudgetDto.class));
    }

    @Override
    public AccountBudgetDto findAccountBudget(AccountBudgetDto accountBudget) {
        LambdaQueryWrapper<AccountBudgetPo> wrapper = new LambdaQueryWrapper<AccountBudgetPo>()
                .eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountBudget.getId()), AccountBudgetPo::getId, accountBudget.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), AccountBudgetDto.class);
    }

    @Override
    public void deleteAccountBudget(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
