package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.account.dto.FindListSysUserByIdDto;
import com.freesia.constant.FlagConstant;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.po.AccountCostUserAllocPo;
import com.freesia.account.service.AccountCostUserAllocService;
import com.freesia.account.mapper.AccountCostUserAllocMapper;
import com.freesia.account.repository.AccountCostUserAllocRepository;
import com.freesia.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 业务逻辑类
 * @date 2025-10-03
 */
@Service
@RequiredArgsConstructor
public class AccountCostUserAllocServiceImpl extends ServiceImpl<AccountCostUserAllocMapper, AccountCostUserAllocPo> implements AccountCostUserAllocService {
    private final AccountCostUserAllocRepository accountCostUserAllocRepository;
    private final AccountCostUserAllocMapper accountCostUserAllocMapper;
    private final SysUserService sysUserService;

    @Override
    public AccountCostUserAllocDto saveUpdate(AccountCostUserAllocDto accountCostUserAllocDto) {
        AccountCostUserAllocPo accountCostUserAllocPo = new AccountCostUserAllocPo();
        UCopy.fullCopy(accountCostUserAllocDto, accountCostUserAllocPo);
        AccountCostUserAllocDto resultDto = new AccountCostUserAllocDto();
        UCopy.fullCopy(accountCostUserAllocRepository.saveAndFlush(accountCostUserAllocPo), resultDto);
        return resultDto;
    }

    @Override
    public List<AccountCostUserAllocDto> saveUpdateBatch(List<AccountCostUserAllocDto> list) {
        List<AccountCostUserAllocPo> accountCostUserAllocPoList = UCopy.fullCopyList(list, AccountCostUserAllocPo.class);
        return UCopy.fullCopyList(accountCostUserAllocRepository.saveAll(accountCostUserAllocPoList), AccountCostUserAllocDto.class);
    }

    @Override
    public TableResult<AccountCostUserAllocDto> findPageAccountCostUserAlloc(AccountCostUserAllocDto accountCostUserAllocDto, PageQuery pageQuery) {
        LambdaQueryWrapper<AccountCostUserAllocPo> wrapper = new LambdaQueryWrapper<AccountCostUserAllocPo>()
                .eq(AccountCostUserAllocPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCostUserAllocDto.getId()), AccountCostUserAllocPo::getId, accountCostUserAllocDto.getId());
        Page<AccountCostUserAllocPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPage(pagePo, AccountCostUserAllocDto.class));
    }

    @Override
    public AccountCostUserAllocDto findAccountCostUserAlloc(AccountCostUserAllocDto accountCostUserAllocDto) {
        LambdaQueryWrapper<AccountCostUserAllocPo> wrapper = new LambdaQueryWrapper<AccountCostUserAllocPo>()
                .eq(AccountCostUserAllocPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCostUserAllocDto.getId()), AccountCostUserAllocPo::getId, accountCostUserAllocDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), AccountCostUserAllocDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccountCostUserAlloc(List<Long> idList) {
        removeBatchByIds(idList);
    }

    @Override
    public List<FindListSysUserByIdDto> findListSysUserById(List<Long> idList) {
        List<FindListSysUserByIdDto> findListSysUserByIdDtoList = new ArrayList<>();
        List<FindPageSysUserListEntity> list = sysUserService.findListSysUserById(idList);
        if (UEmpty.isNotEmpty(list)) {
            for (FindPageSysUserListEntity findPageSysUserListEntity : list) {
                FindListSysUserByIdDto findListSysUserByIdDto = new FindListSysUserByIdDto();
                findListSysUserByIdDto.setUserId(findPageSysUserListEntity.getId());
                findListSysUserByIdDto.setUserName(findPageSysUserListEntity.getUserName());
                findListSysUserByIdDto.setNickName(findPageSysUserListEntity.getNickName());
                findListSysUserByIdDto.setAllocFlag(false);
                findListSysUserByIdDtoList.add(findListSysUserByIdDto);
            }
        }
        return findListSysUserByIdDtoList;
    }

    @Override
    public List<FindListSysUserByIdDto> findListAllocByCostId(AccountCostUserAllocDto accountCostUserAllocDto) {
        return accountCostUserAllocMapper.findListAllocByCostId(accountCostUserAllocDto);
    }

    @Override
    public void deleteAccountCostUserAllocByCostId(List<Long> costIdList) {
        accountCostUserAllocRepository.deleteAccountCostUserAllocByCostId(costIdList);
    }
}
