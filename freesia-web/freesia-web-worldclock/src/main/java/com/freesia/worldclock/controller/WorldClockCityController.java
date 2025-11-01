package com.freesia.worldclock.controller;

import com.freesia.controller.BaseController;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import com.freesia.worldclock.dto.FindCitySunriseSunsetReqDto;
import com.freesia.worldclock.dto.WorldClockCityDto;
import com.freesia.worldclock.entity.FindCitySunriseSunsetEntity;
import com.freesia.worldclock.service.WorldClockCityService;
import com.freesia.worldclock.vo.WorldClockCityVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 控制器
 * @date 2025-10-31
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/worldClockCityController")
@Tag(name = "WorldClockCityController", description = "城市表 控制器")
public class WorldClockCityController extends BaseController {
    private final WorldClockCityService worldClockCityService;

    /**
     * 保存城市表信息
     *
     * @param worldClockCityVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存城市表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody WorldClockCityVo worldClockCityVo) {
        WorldClockCityDto worldClockCityDto = UCopy.copyVo2Dto(worldClockCityVo, WorldClockCityDto.class);
        worldClockCityService.saveUpdate(worldClockCityDto);
        return R.ok();
    }

    /**
     * 批量保存城市表信息
     *
     * @param worldClockCityVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存城市表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<WorldClockCityVo> worldClockCityVoList) {
        List<WorldClockCityDto> worldClockCityDtoList = UCopy.fullCopyList(worldClockCityVoList, WorldClockCityDto.class);
        worldClockCityService.saveUpdateBatch(worldClockCityDtoList);
        return R.ok();
    }

    /**
     * 查询城市表分页信息
     *
     * @param worldClockCityVo 查询条件
     * @param pageQuery        分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询城市表分页信息")
    @GetMapping(value = "findPageWorldClockCity")
    public TableResult<WorldClockCityDto> findPageWorldClockCity(WorldClockCityVo worldClockCityVo, PageQuery pageQuery) {
        WorldClockCityDto worldClockCityDto = UCopy.copyVo2Dto(worldClockCityVo, WorldClockCityDto.class);
        return worldClockCityService.findPageWorldClockCity(worldClockCityDto, pageQuery);
    }

    /**
     * 条件查询城市表
     *
     * @param worldClockCityVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询城市表")
    @GetMapping(value = "findWorldClockCity")
    public R<WorldClockCityDto> findWorldClockCity(WorldClockCityVo worldClockCityVo) {
        WorldClockCityDto worldClockCityDto = UCopy.copyVo2Dto(worldClockCityVo, WorldClockCityDto.class);
        WorldClockCityDto tableResult = worldClockCityService.findWorldClockCity(worldClockCityDto);
        return R.ok(tableResult);
    }

    /**
     * 删除城市表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除城市表")
    @PostMapping(value = "deleteWorldClockCity")
    public R<Void> deleteWorldClockCity(@RequestBody List<Long> idList) {
        worldClockCityService.deleteWorldClockCity(idList);
        return R.ok();
    }


    /**
     * 条件查询城市日出日落时间表
     *
     * @param worldClockSunriseSunsetVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询城市日出日落时间表")
    @GetMapping(value = "findCitySunriseSunset")
    public R<List<FindCitySunriseSunsetEntity>> findCitySunriseSunset(FindCitySunriseSunsetReqDto findCitySunriseSunsetReqDto) {
        List<FindCitySunriseSunsetEntity> findCitySunriseSunsetEntityList = worldClockCityService.findCitySunriseSunset(findCitySunriseSunsetReqDto);
        return R.ok(findCitySunriseSunsetEntityList);
    }
}
