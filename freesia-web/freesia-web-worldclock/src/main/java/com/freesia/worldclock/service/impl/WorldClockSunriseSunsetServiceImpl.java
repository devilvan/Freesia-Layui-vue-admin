package com.freesia.worldclock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;
import com.freesia.worldclock.mapper.WorldClockSunriseSunsetMapper;
import com.freesia.worldclock.po.WorldClockSunriseSunsetPo;
import com.freesia.worldclock.repository.WorldClockSunriseSunsetRepository;
import com.freesia.worldclock.service.WorldClockSunriseSunsetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 业务逻辑类
 * @date 2025-10-31
 */
@Service
@RequiredArgsConstructor
public class WorldClockSunriseSunsetServiceImpl extends ServiceImpl<WorldClockSunriseSunsetMapper, WorldClockSunriseSunsetPo> implements WorldClockSunriseSunsetService {
    private final WorldClockSunriseSunsetRepository worldClockSunriseSunsetRepository;
    private final WorldClockSunriseSunsetMapper worldClockSunriseSunsetMapper;

    @Override
    public WorldClockSunriseSunsetDto saveUpdate(WorldClockSunriseSunsetDto worldClockSunriseSunsetDto) {
        WorldClockSunriseSunsetPo worldClockSunriseSunsetPo = new WorldClockSunriseSunsetPo();
        UCopy.fullCopy(worldClockSunriseSunsetDto, worldClockSunriseSunsetPo);
        WorldClockSunriseSunsetDto resultDto = new WorldClockSunriseSunsetDto();
        UCopy.fullCopy(worldClockSunriseSunsetRepository.saveAndFlush(worldClockSunriseSunsetPo), resultDto);
        return resultDto;
    }

    @Override
    public List<WorldClockSunriseSunsetDto> saveUpdateBatch(List<WorldClockSunriseSunsetDto> list) {
        List<WorldClockSunriseSunsetPo> worldClockSunriseSunsetPoList = UCopy.fullCopyList(list, WorldClockSunriseSunsetPo.class);
        return UCopy.fullCopyList(worldClockSunriseSunsetRepository.saveAllAndFlush(worldClockSunriseSunsetPoList), WorldClockSunriseSunsetDto.class);
    }

    @Override
    public TableResult<WorldClockSunriseSunsetDto> findPageWorldClockSunriseSunset(WorldClockSunriseSunsetDto worldClockSunriseSunsetDto, PageQuery pageQuery) {
        LambdaQueryWrapper<WorldClockSunriseSunsetPo> wrapper = new LambdaQueryWrapper<WorldClockSunriseSunsetPo>()
                .eq(WorldClockSunriseSunsetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(worldClockSunriseSunsetDto.getId()), WorldClockSunriseSunsetPo::getId, worldClockSunriseSunsetDto.getId());
        Page<WorldClockSunriseSunsetPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPage(pagePo, WorldClockSunriseSunsetDto.class));
    }

    @Override
    public WorldClockSunriseSunsetDto findWorldClockSunriseSunset(WorldClockSunriseSunsetDto worldClockSunriseSunsetDto) {
        LambdaQueryWrapper<WorldClockSunriseSunsetPo> wrapper = new LambdaQueryWrapper<WorldClockSunriseSunsetPo>()
            .eq(WorldClockSunriseSunsetPo::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(worldClockSunriseSunsetDto.getId()), WorldClockSunriseSunsetPo::getId, worldClockSunriseSunsetDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), WorldClockSunriseSunsetDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorldClockSunriseSunset(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
