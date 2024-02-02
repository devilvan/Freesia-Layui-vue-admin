package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SysTenantDto;
import com.freesia.po.SysTenantPo;
import com.freesia.service.SysTenantService;
import com.freesia.mapper.SysTenantMapper;
import com.freesia.repository.SysTenantRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 业务逻辑类
 * @date 2024-02-03
 */
@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenantPo> implements SysTenantService {
    private final SysTenantRepository sysTenantRepository;

    @Override
    public SysTenantDto saveUpdate(SysTenantDto sysTenantDto) {
        SysTenantPo sysTenantPo = new SysTenantPo();
        UCopy.fullCopy(sysTenantDto, sysTenantPo);
        SysTenantDto resultDto = new SysTenantDto();
        UCopy.fullCopy(sysTenantRepository.saveAndFlush(sysTenantPo), resultDto);
        return resultDto;
    }

    @Override
    public List<SysTenantDto> saveUpdateBatch(List<SysTenantDto> list) {
        List<SysTenantPo> sysTenantPoList = UCopy.fullCopyList(list, SysTenantPo.class);
        return UCopy.fullCopyList(sysTenantRepository.saveAllAndFlush(sysTenantPoList), SysTenantDto.class);
    }

    @Override
    public TableResult<SysTenantDto> findPageSysTenant(SysTenantDto sysTenant, PageQuery pageQuery) {
        LambdaQueryWrapper<SysTenantPo> wrapper = new LambdaQueryWrapper<SysTenantPo>()
                .eq(SysTenantPo::getLogicDel, FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(sysTenant.getId()), SysTenantPo::getId, sysTenant.getId());
        Page<SysTenantPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, SysTenantDto.class));
    }

    @Override
    public SysTenantDto findSysTenant(SysTenantDto sysTenant) {
        LambdaQueryWrapper<SysTenantPo> wrapper = new LambdaQueryWrapper<SysTenantPo>()
            .eq(SysTenantPo::getLogicDel, FlagConstant.ENABLED)
            .eq(UEmpty.isNotEmpty(sysTenant.getId()), SysTenantPo::getId, sysTenant.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), SysTenantDto.class);
    }

    @Override
    public void deleteSysTenant(Long id) {
        removeById(id);
    }
}
