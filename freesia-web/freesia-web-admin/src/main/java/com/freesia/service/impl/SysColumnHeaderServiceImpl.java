package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.vo.SysColumnHeaderVo;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.po.SysColumnHeaderPo;
import com.freesia.service.SysColumnHeaderService;
import com.freesia.converter.SysColumnHeaderConverter;
import com.freesia.mapper.SysColumnHeaderMapper;
import com.freesia.repository.SysColumnHeaderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列头表 业务逻辑类
 * @date 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class SysColumnHeaderServiceImpl extends BaseServiceImpl<SysColumnHeaderMapper, SysColumnHeaderVo, SysColumnHeaderDto, SysColumnHeaderPo> implements SysColumnHeaderService {
    private final SysColumnHeaderRepository sysColumnHeaderRepository;
    private final SysColumnHeaderMapper sysColumnHeaderMapper;
    private final SysColumnHeaderConverter sysColumnHeaderConverter;

    @Override
    protected MapStructConverter<SysColumnHeaderVo, SysColumnHeaderDto, SysColumnHeaderPo> getMapStructConverter() {
        return sysColumnHeaderConverter;
    }

    @Override
    protected JpaRepository<SysColumnHeaderPo, Long> getRepository() {
        return sysColumnHeaderRepository;
    }

    @Override
    protected Class<SysColumnHeaderDto> getDtoClass() {
        return SysColumnHeaderDto.class;
    }

    @Override
    protected Class<SysColumnHeaderPo> getPoClass() {
        return SysColumnHeaderPo.class;
    }

    @Override
    protected Wrapper<SysColumnHeaderPo> buildQueryWrapper(@NonNull SysColumnHeaderDto sysColumnHeaderDto) {
        return new LambdaQueryWrapper<SysColumnHeaderPo>()
                .eq(SysColumnHeaderPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getId()), SysColumnHeaderPo::getId, sysColumnHeaderDto.getId())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getComponentId()), SysColumnHeaderPo::getComponentId, sysColumnHeaderDto.getComponentId())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getName()), SysColumnHeaderPo::getName, sysColumnHeaderDto.getName())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getHeight()), SysColumnHeaderPo::getHeight, sysColumnHeaderDto.getHeight())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getMaxHeight()), SysColumnHeaderPo::getMaxHeight, sysColumnHeaderDto.getMaxHeight())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getInitPageSize()), SysColumnHeaderPo::getInitPageSize, sysColumnHeaderDto.getInitPageSize())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getEnabled()), SysColumnHeaderPo::getEnabled, sysColumnHeaderDto.getEnabled())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getResizeFlag()), SysColumnHeaderPo::getResizeFlag, sysColumnHeaderDto.getResizeFlag())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getAutoColsWidthFlag()), SysColumnHeaderPo::getAutoColsWidthFlag, sysColumnHeaderDto.getAutoColsWidthFlag())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getDefaultToolBarFlag()), SysColumnHeaderPo::getDefaultToolBarFlag, sysColumnHeaderDto.getDefaultToolBarFlag())
                .eq(UEmpty.isNotEmpty(sysColumnHeaderDto.getComponent()), SysColumnHeaderPo::getComponent, sysColumnHeaderDto.getComponent())
                ;
    }

    @Override
    public TableResult<SysColumnHeaderDto> findPage(SysColumnHeaderDto dto, PageQuery pageQuery) {
        Page<SysColumnHeaderPo> page = sysColumnHeaderMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysColumnHeaderConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<SysColumnHeaderDto> findList(SysColumnHeaderDto dto) {
        return sysColumnHeaderMapper.findList(dto);
    }

    @Override
    public SysColumnHeaderDto findOne(SysColumnHeaderDto dto) {
        return sysColumnHeaderConverter.convertPo2Dto(sysColumnHeaderMapper.findOne(dto));
    }
}
