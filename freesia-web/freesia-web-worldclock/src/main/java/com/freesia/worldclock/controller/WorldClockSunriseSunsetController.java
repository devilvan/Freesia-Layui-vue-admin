package com.freesia.worldclock.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.worldclock.vo.WorldClockSunriseSunsetVo;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;
import com.freesia.worldclock.service.WorldClockSunriseSunsetService;
import com.freesia.controller.BaseController;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 控制器
 * @date 2025-10-31
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/worldClockSunriseSunsetController")
@Tag(name = "WorldClockSunriseSunsetController", description = "日出日落时间表 控制器")
public class WorldClockSunriseSunsetController extends BaseController {
    private final WorldClockSunriseSunsetService worldClockSunriseSunsetService;

    /**
     * 保存日出日落时间表信息
     *
     * @param worldClockSunriseSunsetVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存日出日落时间表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody WorldClockSunriseSunsetVo worldClockSunriseSunsetVo) {
        WorldClockSunriseSunsetDto worldClockSunriseSunsetDto = UCopy.copyVo2Dto(worldClockSunriseSunsetVo, WorldClockSunriseSunsetDto.class);
        worldClockSunriseSunsetService.saveUpdate(worldClockSunriseSunsetDto);
        return R.ok();
    }

    /**
     * 批量保存日出日落时间表信息
     *
     * @param worldClockSunriseSunsetVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存日出日落时间表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<WorldClockSunriseSunsetVo> worldClockSunriseSunsetVoList) {
        List<WorldClockSunriseSunsetDto> worldClockSunriseSunsetDtoList = UCopy.fullCopyList(worldClockSunriseSunsetVoList, WorldClockSunriseSunsetDto.class);
        worldClockSunriseSunsetService.saveUpdateBatch(worldClockSunriseSunsetDtoList);
        return R.ok();
    }

    /**
     * 查询日出日落时间表分页信息
     *
     * @param worldClockSunriseSunsetVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询日出日落时间表分页信息")
    @GetMapping(value = "findPageWorldClockSunriseSunset")
    public TableResult<WorldClockSunriseSunsetDto> findPageWorldClockSunriseSunset(WorldClockSunriseSunsetVo worldClockSunriseSunsetVo, PageQuery pageQuery) {
        WorldClockSunriseSunsetDto worldClockSunriseSunsetDto = UCopy.copyVo2Dto(worldClockSunriseSunsetVo, WorldClockSunriseSunsetDto.class);
        return worldClockSunriseSunsetService.findPageWorldClockSunriseSunset(worldClockSunriseSunsetDto, pageQuery);
    }

    /**
     * 条件查询日出日落时间表
     *
     * @param worldClockSunriseSunsetVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询日出日落时间表")
    @GetMapping(value = "findWorldClockSunriseSunset")
    public R<WorldClockSunriseSunsetDto> findWorldClockSunriseSunset(WorldClockSunriseSunsetVo worldClockSunriseSunsetVo) {
        WorldClockSunriseSunsetDto worldClockSunriseSunsetDto = UCopy.copyVo2Dto(worldClockSunriseSunsetVo, WorldClockSunriseSunsetDto.class);
        WorldClockSunriseSunsetDto tableResult = worldClockSunriseSunsetService.findWorldClockSunriseSunset(worldClockSunriseSunsetDto);
        return R.ok(tableResult);
    }

    /**
     * 删除日出日落时间表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除日出日落时间表")
    @PostMapping(value = "deleteWorldClockSunriseSunset")
    public R<Void> deleteWorldClockSunriseSunset(@RequestBody List<Long> idList) {
        worldClockSunriseSunsetService.deleteWorldClockSunriseSunset(idList);
        return R.ok();
    }
}
