package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.vo.SysClientVo;
import com.freesia.dto.SysClientDto;
import com.freesia.po.SysClientPo;
import com.freesia.service.SysClientService;
import com.freesia.converter.SysClientConverter;
import com.freesia.mapper.SysClientMapper;
import com.freesia.repository.SysClientRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 业务逻辑类
 * @date 2026-03-13
 */
@Service
@RequiredArgsConstructor
public class SysClientServiceImpl extends BaseServiceImpl<SysClientMapper, SysClientVo, SysClientDto, SysClientPo> implements SysClientService {
    private final SysClientRepository sysClientRepository;
    private final SysClientMapper sysClientMapper;
    private final SysClientConverter sysClientConverter;

    @Override
    protected MapStructConverter<SysClientVo, SysClientDto, SysClientPo> getMapStructConverter() {
        return sysClientConverter;
    }

    @Override
    protected JpaRepository<SysClientPo, Long> getRepository() {
    return sysClientRepository;
    }

    @Override
    protected Class<SysClientDto> getDtoClass() {
        return SysClientDto.class;
    }

    @Override
    protected Class<SysClientPo> getPoClass() {
        return SysClientPo.class;
    }

    @Override
    protected Wrapper<SysClientPo> buildQueryWrapper(@NonNull SysClientDto sysClientDto) {
        return new LambdaQueryWrapper<SysClientPo>()
                .eq(SysClientPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysClientDto.getId()), SysClientPo::getId, sysClientDto.getId())
                .eq(UEmpty.isNotEmpty(sysClientDto.getClientId()), SysClientPo::getClientId, sysClientDto.getClientId())
                .eq(UEmpty.isNotEmpty(sysClientDto.getClientKey()), SysClientPo::getClientKey, sysClientDto.getClientKey())
                .eq(UEmpty.isNotEmpty(sysClientDto.getClientSecret()), SysClientPo::getClientSecret, sysClientDto.getClientSecret())
                .eq(UEmpty.isNotEmpty(sysClientDto.getGrantType()), SysClientPo::getGrantType, sysClientDto.getGrantType())
                .eq(UEmpty.isNotEmpty(sysClientDto.getDeviceType()), SysClientPo::getDeviceType, sysClientDto.getDeviceType())
                .eq(UEmpty.isNotEmpty(sysClientDto.getActiveTimeout()), SysClientPo::getActiveTimeout, sysClientDto.getActiveTimeout())
                .eq(UEmpty.isNotEmpty(sysClientDto.getTimeout()), SysClientPo::getTimeout, sysClientDto.getTimeout())
                ;
    }

    @Override
    public TableResult<SysClientDto> findPage(SysClientDto dto, PageQuery pageQuery) {
        Page<SysClientPo> page = sysClientMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysClientConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<SysClientDto> findList(SysClientDto dto) {
        return sysClientMapper.findList(dto);
    }

    @Override
    public SysClientDto findOne(SysClientDto dto) {
        return sysClientConverter.convertPo2Dto(sysClientMapper.findOne(dto));
    }
}
