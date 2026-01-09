package com.freesia.worldclock.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;
import com.freesia.worldclock.mapper.WorldClockSunriseSunsetMapper;
import com.freesia.worldclock.po.WorldClockSunriseSunsetPo;
import com.freesia.worldclock.repository.WorldClockSunriseSunsetRepository;
import com.freesia.worldclock.service.WorldClockSunriseSunsetService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 业务逻辑类
 * @date 2025-10-31
 */
@Service
@RequiredArgsConstructor
public class WorldClockSunriseSunsetServiceImpl extends BaseServiceImpl<WorldClockSunriseSunsetMapper, WorldClockSunriseSunsetPo, WorldClockSunriseSunsetDto> implements WorldClockSunriseSunsetService {
    private final WorldClockSunriseSunsetRepository worldClockSunriseSunsetRepository;


    @Override
    protected JpaRepository<WorldClockSunriseSunsetPo, Long> getRepository() {
        return worldClockSunriseSunsetRepository;
    }

    @Override
    protected Class<WorldClockSunriseSunsetDto> getDtoClass() {
        return WorldClockSunriseSunsetDto.class;
    }

    @Override
    protected Class<WorldClockSunriseSunsetPo> getPoClass() {
        return WorldClockSunriseSunsetPo.class;
    }

    @Override
    protected Wrapper<WorldClockSunriseSunsetPo> buildQueryWrapper(@NonNull WorldClockSunriseSunsetDto worldClockSunriseSunsetDto) {
        return new LambdaQueryWrapper<WorldClockSunriseSunsetPo>()
                .eq(WorldClockSunriseSunsetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(worldClockSunriseSunsetDto.getId()), WorldClockSunriseSunsetPo::getId, worldClockSunriseSunsetDto.getId());
    }
}
