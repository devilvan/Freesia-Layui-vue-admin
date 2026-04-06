package com.freesia.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SysColumnMiddleConverter;
import com.freesia.dto.SysColumnMiddleDto;
import com.freesia.mapper.SysColumnMiddleMapper;
import com.freesia.po.SysColumnMiddlePo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.repository.SysColumnMiddleRepository;
import com.freesia.service.SysColumnMiddleService;
import com.freesia.util.UEmpty;
import com.freesia.vo.SysColumnMiddleVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    public List<SysColumnMiddleDto> saveUpdateBatch(List<SysColumnMiddleDto> list) {
        List<SysColumnMiddleDto> sysColumnMiddleDtoList = super.saveUpdateBatch(list);
        if (UEmpty.isNotEmpty(sysColumnMiddleDtoList)) {
            SysColumnMiddleDto sysColumnMiddleDto = sysColumnMiddleDtoList.get(0);
            Long headerId = sysColumnMiddleDto.getHeaderId();
            String cacheKey = CacheConstant.SYS_COLUMN_MIDDLE + '@' + headerId;
            URedis.put(cacheKey, headerId.toString(), sysColumnMiddleDtoList);
            URedis.expire(cacheKey, Duration.parse("PT" + "5H" + RandomUtil.randomInt(2, 11) + "M"));
            return sysColumnMiddleDtoList;
        }
        return null;
    }

    @Override
    public TableResult<SysColumnMiddleDto> findPage(SysColumnMiddleDto dto, PageQuery pageQuery) {
        Page<SysColumnMiddlePo> page = sysColumnMiddleMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysColumnMiddleConverter.convertPagePo2Dto(page));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SysColumnMiddleDto> findCacheList(SysColumnMiddleDto dto) {
        Long headerId = dto.getHeaderId();
        String cacheKey = CacheConstant.SYS_COLUMN_MIDDLE + '@' + headerId;
        List<SysColumnMiddleDto> sysColumnMiddleDtoList = (List<SysColumnMiddleDto>) URedis.hashGet(cacheKey, headerId.toString());
        if (UEmpty.isNotEmpty(sysColumnMiddleDtoList)) {
            return sysColumnMiddleDtoList;
        }
        List<SysColumnMiddleDto> list = super.findList(dto);
        if (UEmpty.isNotEmpty(list)) {
            URedis.put(cacheKey, headerId.toString(), list);
            URedis.expire(cacheKey, Duration.parse("P1DT" + RandomUtil.randomInt(2, 11) + "M"));
            return list;
        }
        return null;
    }

    @Override
    public SysColumnMiddleDto findOne(SysColumnMiddleDto dto) {
        return sysColumnMiddleConverter.convertPo2Dto(sysColumnMiddleMapper.findOne(dto));
    }
}
