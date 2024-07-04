package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.TenantModule;
import com.freesia.dto.SysTenantDto;
import com.freesia.entity.FindSysTenantEntity;
import com.freesia.exception.ServiceException;
import com.freesia.exception.TenantException;
import com.freesia.mapper.SysTenantMapper;
import com.freesia.po.SysTenantPo;
import com.freesia.po.SysTenantUserPk;
import com.freesia.po.SysTenantUserPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysTenantRepository;
import com.freesia.service.SysTenantService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 租户信息表 业务逻辑类
 * @date 2024-02-03
 */
@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenantPo> implements SysTenantService {
    private final SysTenantRepository sysTenantRepository;
    private final SysTenantMapper sysTenantMapper;

    @Override
    public SysTenantDto saveUpdate(SysTenantDto sysTenantDto) {
        SysTenantPo sysTenantPo = new SysTenantPo();
        if (UEmpty.isEmpty(sysTenantDto.getId())) {
            int flag = Convert.toInt(sysTenantMapper.findExistCode(sysTenantDto.getCode()), 0);
            if (flag != 0) {
                throw new ServiceException(TenantModule.TENANT_MANAGEMENT, "tenant.code.exists", sysTenantDto.getCode());
            }
            UCopy.fullCopy(sysTenantDto, sysTenantPo);
            return UCopy.copyPo2Dto(sysTenantRepository.saveAndFlush(sysTenantPo), SysTenantDto.class);
        }
        Wrapper<SysTenantPo> queryWrapper = new LambdaQueryWrapper<SysTenantPo>()
                .eq(SysTenantPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysTenantPo::getId, sysTenantDto.getId());
        sysTenantPo = getOne(queryWrapper);
        UCopy.halfCopy(sysTenantDto, sysTenantPo);
        return UCopy.copyPo2Dto(sysTenantRepository.saveAndFlush(sysTenantPo), SysTenantDto.class);
    }

    @Override
    public List<SysTenantDto> saveUpdateBatch(List<SysTenantDto> list) {
        List<SysTenantPo> sysTenantPoList = UCopy.fullCopyList(list, SysTenantPo.class);
        return UCopy.fullCopyList(sysTenantRepository.saveAllAndFlush(sysTenantPoList), SysTenantDto.class);
    }

    @Override
    public TableResult<SysTenantDto> findPageSysTenant(SysTenantDto sysTenant, PageQuery pageQuery) {
        LambdaQueryWrapper<SysTenantPo> wrapper = new LambdaQueryWrapper<SysTenantPo>()
                .eq(SysTenantPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysTenant.getId()), SysTenantPo::getId, sysTenant.getId());
        Page<SysTenantPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, SysTenantDto.class));
    }

    @Override
    public FindSysTenantEntity findSysTenant(SysTenantDto sysTenant) {
        LambdaQueryWrapper<SysTenantPo> wrapper = new LambdaQueryWrapper<SysTenantPo>()
                .eq(SysTenantPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysTenant.getId()), SysTenantPo::getId, sysTenant.getId())
                .likeRight(UEmpty.isNotEmpty(sysTenant.getName()), SysTenantPo::getName, sysTenant.getName());
        SysTenantPo sysTenantPo = getOne(wrapper);
        FindSysTenantEntity findSysTenantEntity = new FindSysTenantEntity();
        UCopy.fullCopy(sysTenantPo, findSysTenantEntity);
        return findSysTenantEntity;
    }

    @Override
    public void deleteSysTenant(List<Long> idList) {
        sysTenantRepository.updateLogicDel(idList);
    }

    @Override
    public void assignTenant2User(Long tenantId, List<Long> userIdList) {
        SysTenantPo sysTenantPo = sysTenantRepository.findById(tenantId)
                .orElseThrow(() -> new ServiceException(TenantModule.TENANT_MANAGEMENT, "tenant.query.failed", tenantId));
        Set<SysTenantUserPo> sysTenantUserPoSet = new HashSet<>();
        for (Long userId : userIdList) {
            SysTenantUserPo sysTenantUserPo = new SysTenantUserPo(new SysTenantUserPk(tenantId, userId));
            sysTenantUserPoSet.add(sysTenantUserPo);
        }
        sysTenantPo.setSysTenantUserPoSet(sysTenantUserPoSet);
        sysTenantRepository.save(sysTenantPo);
    }

    @Override
    public void cancelAssignUser(Long tenantId, List<Long> userIdList) {
        sysTenantRepository.cancelAssignUser(tenantId, userIdList);
    }

    @Override
    public List<SysTenantDto> findListSysTenantByUserId(Long userId) {
        List<SysTenantPo> sysTenantPoList = sysTenantMapper.findListSysTenantByUserId(userId);
        return UCopy.fullCopyList(sysTenantPoList, SysTenantDto.class);
    }

    @Override
    public SysTenantDto findSysTenantById(Long tenantId) {
        SysTenantPo sysTenantPo = sysTenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantException("tenant.query.failed", tenantId));
        return UCopy.copyPo2Dto(sysTenantPo, SysTenantDto.class);
    }
}
