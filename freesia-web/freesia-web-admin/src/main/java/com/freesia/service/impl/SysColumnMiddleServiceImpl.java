package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.CacheConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.vo.SysColumnMiddleVo;
import com.freesia.dto.SysColumnMiddleDto;
import com.freesia.po.SysColumnMiddlePo;
import com.freesia.service.SysColumnMiddleService;
import com.freesia.converter.SysColumnMiddleConverter;
import com.freesia.mapper.SysColumnMiddleMapper;
import com.freesia.repository.SysColumnMiddleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 业务逻辑类
 * @date 2026-03-27
 */
@Service
@RequiredArgsConstructor
public class SysColumnMiddleServiceImpl extends BaseServiceImpl<SysColumnMiddleMapper, SysColumnMiddleVo, SysColumnMiddleDto, SysColumnMiddlePo> implements SysColumnMiddleService {
    private final SysColumnMiddleRepository sysColumnMiddleRepository;
    private final SysColumnMiddleMapper sysColumnMiddleMapper;
    private final SysColumnMiddleConverter sysColumnMiddleConverter;

    @Override
    protected MapStructConverter<SysColumnMiddleVo, SysColumnMiddleDto, SysColumnMiddlePo> getMapStructConverter() {
        return sysColumnMiddleConverter;
    }

    @Override
    protected JpaRepository<SysColumnMiddlePo, Long> getRepository() {
    return sysColumnMiddleRepository;
    }

    @Override
    protected Class<SysColumnMiddleDto> getDtoClass() {
        return SysColumnMiddleDto.class;
    }

    @Override
    protected Class<SysColumnMiddlePo> getPoClass() {
        return SysColumnMiddlePo.class;
    }

    @Override
    protected Wrapper<SysColumnMiddlePo> buildQueryWrapper(@NonNull SysColumnMiddleDto sysColumnMiddleDto) {
        return new LambdaQueryWrapper<SysColumnMiddlePo>()
                .eq(SysColumnMiddlePo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysColumnMiddleDto.getId()), SysColumnMiddlePo::getId, sysColumnMiddleDto.getId())
                .eq(UEmpty.isNotEmpty(sysColumnMiddleDto.getHeaderId()), SysColumnMiddlePo::getHeaderId, sysColumnMiddleDto.getHeaderId())
                .eq(UEmpty.isNotEmpty(sysColumnMiddleDto.getTitle()), SysColumnMiddlePo::getTitle, sysColumnMiddleDto.getTitle())
                .eq(UEmpty.isNotEmpty(sysColumnMiddleDto.getName()), SysColumnMiddlePo::getName, sysColumnMiddleDto.getName())
                .eq(UEmpty.isNotEmpty(sysColumnMiddleDto.getEnabled()), SysColumnMiddlePo::getEnabled, sysColumnMiddleDto.getEnabled())
                ;
    }

    @Override
    public TableResult<SysColumnMiddleDto> findPage(SysColumnMiddleDto dto, PageQuery pageQuery) {
        Page<SysColumnMiddlePo> page = sysColumnMiddleMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysColumnMiddleConverter.convertPagePo2Dto(page));
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_COLUMN_MIDDLE, key = "#dto.headerId")
    public List<SysColumnMiddleDto> findCacheList(SysColumnMiddleDto dto) {
        return super.findList(dto);
    }

    @Override
    public SysColumnMiddleDto findOne(SysColumnMiddleDto dto) {
        return sysColumnMiddleConverter.convertPo2Dto(sysColumnMiddleMapper.findOne(dto));
    }
}
