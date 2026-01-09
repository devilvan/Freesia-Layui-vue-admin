package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.dto.FindListSysUserByIdDto;
import com.freesia.account.mapper.AccountCostUserAllocMapper;
import com.freesia.account.po.AccountCostUserAllocPo;
import com.freesia.account.repository.AccountCostUserAllocRepository;
import com.freesia.account.service.AccountCostUserAllocService;
import com.freesia.constant.FlagConstant;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.service.SysUserService;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 业务逻辑类
 * @date 2025-10-03
 */
@Service
@RequiredArgsConstructor
public class AccountCostUserAllocServiceImpl extends BaseServiceImpl<AccountCostUserAllocMapper, AccountCostUserAllocPo, AccountCostUserAllocDto> implements AccountCostUserAllocService {
    private final AccountCostUserAllocRepository accountCostUserAllocRepository;
    private final AccountCostUserAllocMapper accountCostUserAllocMapper;
    private final SysUserService sysUserService;


    @Override
    protected JpaRepository<AccountCostUserAllocPo, Long> getRepository() {
        return accountCostUserAllocRepository;
    }

    @Override
    protected Class<AccountCostUserAllocDto> getDtoClass() {
        return AccountCostUserAllocDto.class;
    }

    @Override
    protected Class<AccountCostUserAllocPo> getPoClass() {
        return AccountCostUserAllocPo.class;
    }

    @Override
    protected Wrapper<AccountCostUserAllocPo> buildQueryWrapper(@NonNull AccountCostUserAllocDto accountCostUserAllocDto) {
        return new LambdaQueryWrapper<AccountCostUserAllocPo>()
                .eq(AccountCostUserAllocPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCostUserAllocDto.getId()), AccountCostUserAllocPo::getId, accountCostUserAllocDto.getId());
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
