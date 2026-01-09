package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysTenantDto;
import com.freesia.entity.FindSysTenantEntity;
import com.freesia.exception.ServiceException;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysTenantMapper;
import com.freesia.po.SysTenantPo;
import com.freesia.po.SysTenantUserPk;
import com.freesia.po.SysTenantUserPo;
import com.freesia.repository.SysTenantRepository;
import com.freesia.service.SysTenantService;
import com.freesia.tenant.constant.TenantModule;
import com.freesia.tenant.exception.TenantException;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 租户信息表 业务逻辑类
 * @date 2024-02-03
 */
@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl extends BaseServiceImpl<SysTenantMapper, SysTenantPo, SysTenantDto> implements SysTenantService {
    private final SysTenantRepository sysTenantRepository;
    private final SysTenantMapper sysTenantMapper;


    @Override
    protected JpaRepository<SysTenantPo, Long> getRepository() {
        return sysTenantRepository;
    }

    @Override
    protected Class<SysTenantDto> getDtoClass() {
        return SysTenantDto.class;
    }

    @Override
    protected Class<SysTenantPo> getPoClass() {
        return SysTenantPo.class;
    }

    @Override
    protected Wrapper<SysTenantPo> buildQueryWrapper(@NonNull SysTenantDto dto) {
        return new LambdaQueryWrapper<SysTenantPo>()
                .eq(SysTenantPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotNull(dto.getId()), SysTenantPo::getId, dto.getId())
                .likeRight(UEmpty.isNotEmpty(dto.getName()), SysTenantPo::getName, dto.getName());
    }

    @Override
    @LogRecord(module = TenantModule.TENANT_MANAGEMENT, subModule = TenantModule.SubModule.SAVE_TENANT, message = "tenant.save")
    public SysTenantDto saveUpdate(SysTenantDto sysTenantDto) {
        SysTenantPo sysTenantPo = new SysTenantPo();
        if (UEmpty.isEmpty(sysTenantDto.getId())) {
            int flag = Convert.toInt(sysTenantMapper.findExistCode(sysTenantDto.getCode()), 0);
            if (flag != 0) {
                throw new ServiceException(TenantModule.TENANT_MANAGEMENT, "tenant.code.exists", new Object[]{sysTenantDto.getCode()});
            }
            UCopy.fullCopy(sysTenantDto, sysTenantPo);
            return UCopy.copyPo2Dto(sysTenantRepository.saveAndFlush(sysTenantPo), SysTenantDto.class);
        }
        Wrapper<SysTenantPo> queryWrapper = buildQueryWrapper(sysTenantDto);
        sysTenantPo = getOne(queryWrapper);
        UCopy.halfCopy(sysTenantDto, sysTenantPo);
        return UCopy.copyPo2Dto(sysTenantRepository.saveAndFlush(sysTenantPo), SysTenantDto.class);
    }

    @Override
    @LogRecord(module = TenantModule.TENANT_MANAGEMENT, subModule = TenantModule.SubModule.SAVE_TENANT, message = "tenant.save")
    public List<SysTenantDto> saveUpdateBatch(List<SysTenantDto> list) {
        return super.saveUpdateBatch(list);
    }

    @Override
    public FindSysTenantEntity findSysTenant(SysTenantDto sysTenantDto) {
        Wrapper<SysTenantPo> wrapper = buildQueryWrapper(sysTenantDto);
        SysTenantPo sysTenantPo = getOne(wrapper);
        FindSysTenantEntity findSysTenantEntity = new FindSysTenantEntity();
        UCopy.fullCopy(sysTenantPo, findSysTenantEntity);
        return findSysTenantEntity;
    }

    @Override
    @LogRecord(module = TenantModule.TENANT_MANAGEMENT, subModule = TenantModule.SubModule.DELETE_TENANT, message = "tenant.delete")
    public void deleteSysTenant(List<Long> idList) {
        sysTenantRepository.updateLogicDel(idList);
    }

    @Override
    @LogRecord(module = TenantModule.TENANT_MANAGEMENT, subModule = TenantModule.SubModule.ASSIGN_USER, message = "tenant.assignUser")
    public void assignTenant2User(Long tenantId, List<Long> userIdList) {
        SysTenantPo sysTenantPo = sysTenantRepository.findById(tenantId)
                .orElseThrow(() -> new ServiceException(TenantModule.TENANT_MANAGEMENT, "tenant.query.failed", new Object[]{tenantId}));
        Set<SysTenantUserPo> sysTenantUserPoSet = new HashSet<>();
        for (Long userId : userIdList) {
            SysTenantUserPo sysTenantUserPo = new SysTenantUserPo(new SysTenantUserPk(tenantId, userId));
            sysTenantUserPoSet.add(sysTenantUserPo);
        }
        sysTenantPo.setSysTenantUserPoSet(sysTenantUserPoSet);
        sysTenantRepository.save(sysTenantPo);
    }

    @Override
    @LogRecord(module = TenantModule.TENANT_MANAGEMENT, subModule = TenantModule.SubModule.CANCEL_ASSIGN_USER, message = "tenant.cancel.assignUser")
    public void cancelAssignUser(Long tenantId, List<Long> userIdList) {
        sysTenantRepository.cancelAssignUser(tenantId, userIdList);
    }

    @Override
    public List<SysTenantDto> findListSysTenantByUserId(Long userId) {
        List<SysTenantPo> sysTenantPoList = sysTenantMapper.findListSysTenantByUserId(userId);
        return UCopy.fullCopyList(sysTenantPoList, SysTenantDto.class);
    }

    @Override
    public SysTenantDto findOne(SysTenantDto sysTenantDto) {
        SysTenantDto resultSysTenantDto = super.findOne(sysTenantDto);
        if (resultSysTenantDto == null) {
            throw new TenantException("tenant.query.failed", new Object[]{sysTenantDto});
        }
        return resultSysTenantDto;
    }
}
