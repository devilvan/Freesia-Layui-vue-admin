package com.freesia.worldclock.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.redis.util.URedis;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.worldclock.converter.WorldClockCityConverter;
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
import com.freesia.worldclock.vo.WorldClockCityVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 城市表 业务逻辑类
 * @date 2025-10-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorldClockCityServiceImpl extends BaseServiceImpl<WorldClockCityMapper, WorldClockCityVo, WorldClockCityDto, WorldClockCityPo> implements WorldClockCityService {
    private final WorldClockCityRepository worldClockCityRepository;
    private final WorldClockCityMapper worldClockCityMapper;
    private final WorldClockSunriseSunsetService worldClockSunriseSunsetService;
    private final WorldClockCityConverter worldClockCityConverter;


    @Override
    protected MapStructConverter<WorldClockCityVo, WorldClockCityDto, WorldClockCityPo> getMapStructConverter() {
        return worldClockCityConverter;
    }

    @Override
    protected JpaRepository<WorldClockCityPo, Long> getRepository() {
        return worldClockCityRepository;
    }

    @Override
    protected Class<WorldClockCityDto> getDtoClass() {
        return WorldClockCityDto.class;
    }

    @Override
    protected Class<WorldClockCityPo> getPoClass() {
        return WorldClockCityPo.class;
    }

    @Override
    protected Wrapper<WorldClockCityPo> buildQueryWrapper(@NonNull WorldClockCityDto worldClockCityDto) {
        return new LambdaQueryWrapper<WorldClockCityPo>()
                .eq(WorldClockCityPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(worldClockCityDto.getId()), WorldClockCityPo::getId, worldClockCityDto.getId());
    }

    /**
     * 为指定城市生成全年日出日落数据
     *
     * @param cityId 城市ID
     * @param year   年份
     */
    @Override
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
        List<WorldClockCityDto> worldClockCityPoList = this.findList(worldClockCityDto);
        for (WorldClockCityDto dto : worldClockCityPoList) {
            generateYearlyDataForCity(dto.getId(), year);
        }
        log.info("成功生成 {} 年所有城市的日出日落数据", year);
    }

    @Override
    public List<WorldClockCityDto> findList(WorldClockCityDto worldClockCityDto) {
        List<WorldClockCityPo> worldClockCityPoList = worldClockCityMapper.findListWorldClockCity(worldClockCityDto);
        return worldClockCityConverter.convertBatchPo2Dto(worldClockCityPoList);
    }


    @Override
    public List<FindCitySunriseSunsetEntity> findCitySunriseSunset(FindCitySunriseSunsetReqDto findCitySunriseSunsetReqDto) {
        String cacheKey = "findCitySunriseSunset:" + findCitySunriseSunsetReqDto.getDate();
        List<String> cityNameList = findCitySunriseSunsetReqDto.getCityNameList();
        List<FindCitySunriseSunsetEntity> findCitySunriseSunsetEntityList = URedis.get(cacheKey);
        if (UEmpty.isNotEmpty(findCitySunriseSunsetEntityList)) {
            findCitySunriseSunsetEntityList = filterCityNameList(cityNameList, findCitySunriseSunsetEntityList);
            return findCitySunriseSunsetEntityList;
        }
        findCitySunriseSunsetEntityList = Optional.ofNullable(worldClockCityMapper.findCitySunriseSunset(findCitySunriseSunsetReqDto))
                .orElseGet(ArrayList::new);
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
        if (UEmpty.isNotEmpty(findCitySunriseSunsetEntityList)) {
            URedis.set(cacheKey, findCitySunriseSunsetEntityList, Duration.ofDays(1));
        }
        return filterCityNameList(cityNameList, findCitySunriseSunsetEntityList);
    }

    private static List<FindCitySunriseSunsetEntity> filterCityNameList(List<String> cityNameList, List<FindCitySunriseSunsetEntity> findCitySunriseSunsetEntityList) {
        if (UEmpty.isNotEmpty(cityNameList)) {
            findCitySunriseSunsetEntityList = findCitySunriseSunsetEntityList.stream()
                    .filter(item -> cityNameList.contains(item.getCityName()))
                    .collect(Collectors.toList());
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
