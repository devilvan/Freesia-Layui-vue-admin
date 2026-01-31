package com.freesia.worldclock.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.worldclock.converter.WorldClockCityConverter;
import com.freesia.worldclock.dto.FindCitySunriseSunsetReqDto;
import com.freesia.worldclock.dto.WorldClockCityDto;
import com.freesia.worldclock.entity.FindCitySunriseSunsetEntity;
import com.freesia.worldclock.mapper.WorldClockCityMapper;
import com.freesia.worldclock.po.WorldClockCityPo;
import com.freesia.worldclock.repository.WorldClockCityRepository;
import com.freesia.worldclock.service.WorldClockCityService;
import com.freesia.worldclock.util.SunriseSunsetCalculatorUtil;
import com.freesia.worldclock.util.TimeZoneConverter;
import com.freesia.worldclock.vo.WorldClockCityVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
                .eq(UEmpty.isNotEmpty(worldClockCityDto.getCityName()), WorldClockCityPo::getCityName, worldClockCityDto.getCityName())
                .in(UEmpty.isNotEmpty(worldClockCityDto.getCityNameList()), WorldClockCityPo::getCityName, worldClockCityDto.getCityNameList())
                .eq(UEmpty.isNotEmpty(worldClockCityDto.getId()), WorldClockCityPo::getId, worldClockCityDto.getId());
    }

    @Override
    public List<FindCitySunriseSunsetEntity> findCitySunriseSunset(FindCitySunriseSunsetReqDto findCitySunriseSunsetReqDto) {
        LocalDate localDate = findCitySunriseSunsetReqDto.getDate();
        String cacheKey = "findCitySunriseSunset:" + localDate;
        List<String> cityNameList = findCitySunriseSunsetReqDto.getCityNameList();
        List<FindCitySunriseSunsetEntity> findCitySunriseSunsetEntityList = new ArrayList<>();
        WorldClockCityDto worldClockCityDto = new WorldClockCityDto();
        worldClockCityDto.setCityNameList(cityNameList);
        List<WorldClockCityDto> worldClockCityDtoList = this.findList(worldClockCityDto);
        if (UEmpty.isEmpty(worldClockCityDtoList)) {
            return null;
        }
        for (WorldClockCityDto record : worldClockCityDtoList) {
            FindCitySunriseSunsetEntity findCitySunriseSunsetEntity = new FindCitySunriseSunsetEntity();
            findCitySunriseSunsetEntity.setCityId(record.getId());
            findCitySunriseSunsetEntity.setCityName(record.getCityName());
            findCitySunriseSunsetEntity.setTimezone(record.getTimezone());
            findCitySunriseSunsetEntity.setDate(localDate);
            // 转换为当地时间
            String timezone = record.getTimezone();
            SunriseSunsetCalculatorUtil.SunriseSunsetResult result =
                    SunriseSunsetCalculatorUtil.calculateSunriseSunset(
                            record.getLatitude(),
                            record.getLongitude(),
                            localDate,
                            record.getTimezone()
                    );
            LocalTime sunriseLocal = TimeZoneConverter.utcToLocalTime(result.getSunrise(), localDate, timezone);
            LocalTime sunsetLocal = TimeZoneConverter.utcToLocalTime(result.getSunset(), localDate, timezone);
            // 计算当地日长
            int dayLength = TimeZoneConverter.calculateLocalDayLength(sunriseLocal, sunsetLocal);
            findCitySunriseSunsetEntity.setSunriseTime(result.getSunrise());
            findCitySunriseSunsetEntity.setSunsetTime(result.getSunset());
            findCitySunriseSunsetEntity.setSunriseTimeLocal(sunriseLocal);
            findCitySunriseSunsetEntity.setSunsetTimeLocal(sunsetLocal);
            findCitySunriseSunsetEntity.setDayLengthMinutes(dayLength);
            findCitySunriseSunsetEntityList.add(findCitySunriseSunsetEntity);
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
}
