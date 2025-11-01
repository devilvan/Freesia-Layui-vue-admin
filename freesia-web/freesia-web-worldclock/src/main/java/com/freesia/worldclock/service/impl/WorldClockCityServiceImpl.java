package com.freesia.worldclock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.worldclock.dto.FindCitySunriseSunsetReqDto;
import com.freesia.worldclock.dto.WorldClockCityDto;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;
import com.freesia.worldclock.entity.FindCitySunriseSunsetEntity;
import com.freesia.worldclock.mapper.WorldClockCityMapper;
import com.freesia.worldclock.po.WorldClockCityPo;
import com.freesia.worldclock.repository.WorldClockCityRepository;
import com.freesia.worldclock.service.WorldClockCityService;
import com.freesia.worldclock.service.WorldClockSunriseSunsetService;
import com.freesia.worldclock.util.SunriseSunsetCalculatorUtil;
import com.freesia.worldclock.util.TimeZoneConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 业务逻辑类
 * @date 2025-10-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorldClockCityServiceImpl extends ServiceImpl<WorldClockCityMapper, WorldClockCityPo> implements WorldClockCityService {
    private final WorldClockCityRepository worldClockCityRepository;
    private final WorldClockCityMapper worldClockCityMapper;
    private final WorldClockSunriseSunsetService worldClockSunriseSunsetService;

    @Override
    public WorldClockCityDto saveUpdate(WorldClockCityDto worldClockCityDto) {
        WorldClockCityPo worldClockCityPo = new WorldClockCityPo();
        UCopy.fullCopy(worldClockCityDto, worldClockCityPo);
        WorldClockCityDto resultDto = new WorldClockCityDto();
        UCopy.fullCopy(worldClockCityRepository.saveAndFlush(worldClockCityPo), resultDto);
        return resultDto;
    }

    @Override
    public List<WorldClockCityDto> saveUpdateBatch(List<WorldClockCityDto> list) {
        List<WorldClockCityPo> worldClockCityPoList = UCopy.fullCopyList(list, WorldClockCityPo.class);
        return UCopy.fullCopyList(worldClockCityRepository.saveAllAndFlush(worldClockCityPoList), WorldClockCityDto.class);
    }

    @Override
    public TableResult<WorldClockCityDto> findPageWorldClockCity(WorldClockCityDto worldClockCityDto, PageQuery pageQuery) {
        LambdaQueryWrapper<WorldClockCityPo> wrapper = new LambdaQueryWrapper<WorldClockCityPo>()
                .eq(WorldClockCityPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(worldClockCityDto.getId()), WorldClockCityPo::getId, worldClockCityDto.getId());
        Page<WorldClockCityPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, WorldClockCityDto.class));
    }

    @Override
    public WorldClockCityDto findWorldClockCity(WorldClockCityDto worldClockCityDto) {
        LambdaQueryWrapper<WorldClockCityPo> wrapper = new LambdaQueryWrapper<WorldClockCityPo>()
                .eq(WorldClockCityPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(worldClockCityDto.getId()), WorldClockCityPo::getId, worldClockCityDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), WorldClockCityDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorldClockCity(List<Long> idList) {
        removeBatchByIds(idList);
    }

    /**
     * 为指定城市生成全年日出日落数据
     *
     * @param cityId 城市ID
     * @param year   年份
     */
    public void generateYearlyDataForCity(Long cityId, int year) {
        // 获取城市信息
        WorldClockCityPo city = getById(cityId);
        if (city == null) {
            throw new IllegalArgumentException("城市ID不存在: " + cityId);
        }
        // 删除该城市该年的现有数据
        deleteExistingData(cityId, year);

        // 生成全年数据
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<WorldClockSunriseSunsetDto> dataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            SunriseSunsetCalculatorUtil.SunriseSunsetResult result =
                    SunriseSunsetCalculatorUtil.calculateSunriseSunset(
                            city.getLatitude(),
                            city.getLongitude(),
                            date,
                            city.getTimezone()
                    );

            WorldClockSunriseSunsetDto data = new WorldClockSunriseSunsetDto(
                    cityId, date, result.getSunrise(), result.getSunset()
            );
            dataList.add(data);
            // 每100条数据批量插入一次
            if (dataList.size() >= 100) {
                worldClockSunriseSunsetService.saveUpdateBatch(dataList);
                dataList.clear();
            }
        }
        // 插入剩余数据
        if (!dataList.isEmpty()) {
            worldClockSunriseSunsetService.saveUpdateBatch(dataList);
        }
        log.info("成功生成 {} 年城市 {} 的日出日落数据", year, city.getCityName());
    }

    /**
     * 为所有城市生成全年日出日落数据
     *
     * @param year 年份
     */
    @Override
    public void generateYearlyDataForAllCities(int year) {
        WorldClockCityDto worldClockCityDto = new WorldClockCityDto();
        List<WorldClockCityDto> worldClockCityPoList = findListWorldClockCity(worldClockCityDto);
        for (WorldClockCityDto dto : worldClockCityPoList) {
            generateYearlyDataForCity(dto.getId(), year);
        }
        log.info("成功生成 {} 年所有城市的日出日落数据", year);
    }

    @Override
    public List<WorldClockCityDto> findListWorldClockCity(WorldClockCityDto worldClockCityDto) {
        List<WorldClockCityPo> worldClockCityPoList = worldClockCityMapper.findListWorldClockCity(worldClockCityDto);
        return UCopy.fullCopyList(worldClockCityPoList, WorldClockCityDto.class);
    }


    @Override
    public List<FindCitySunriseSunsetEntity> findCitySunriseSunset(FindCitySunriseSunsetReqDto findCitySunriseSunsetReqDto) {
        List<FindCitySunriseSunsetEntity> findCitySunriseSunsetEntityList = worldClockCityMapper.findCitySunriseSunset(findCitySunriseSunsetReqDto);
        if (UEmpty.isNotEmpty(findCitySunriseSunsetEntityList)) {
            for (FindCitySunriseSunsetEntity record : findCitySunriseSunsetEntityList) {
                // 转换为当地时间
                String timezone = record.getTimezone();
                LocalTime sunriseLocal = TimeZoneConverter.utcToLocalTime(record.getSunriseTime(), record.getDate(), timezone);
                LocalTime sunsetLocal = TimeZoneConverter.utcToLocalTime(record.getSunsetTime(), record.getDate(), timezone);
                // 计算当地日长
                int dayLength = TimeZoneConverter.calculateLocalDayLength(sunriseLocal, sunsetLocal);
                record.setSunriseTimeLocal(sunriseLocal);
                record.setSunsetTimeLocal(sunsetLocal);
                record.setDayLengthMinutes(dayLength);
            }
        }
        return findCitySunriseSunsetEntityList;
    }

    /**
     * 删除现有数据
     */
    private void deleteExistingData(Long cityId, int year) {
        worldClockCityRepository.deleteExistingData(cityId, year);
    }
}
