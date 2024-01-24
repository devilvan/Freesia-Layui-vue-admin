package com.freesia.url.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.url.dto.UrlConfigDto;
import com.freesia.url.vo.UrlConfigVo;
import com.freesia.url.service.UrlConfigService;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 控制器
 * @date 2024-01-24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/urlConfigController")
@Tag(name = "UrlConfigController", description = "URL配置信息表 控制器")
public class UrlConfigController {
    private final UrlConfigService urlConfigService;

    /**
    * 查询URL配置信息表分页信息
    *
    * @param urlConfigVo 查询条件
    * @param pageQuery   分页条件
    * @return 形式返回
    */
    @Operation(summary = "查询URL配置信息表分页信息")
    @GetMapping(value = "findPageUrlConfig")
    public R<TableResult<UrlConfigDto>> findPageUrlConfig(UrlConfigVo urlConfigVo, PageQuery pageQuery) {
        UrlConfigDto urlConfigDto = UCopy.copyVo2Dto(urlConfigVo, UrlConfigDto.class);
        TableResult<UrlConfigDto> tableResult = urlConfigService.findPageUrlConfig(urlConfigDto, pageQuery);
        return R.ok(tableResult);
    }

    /**
    * 条件查询URL配置信息表
    *
    * @param urlConfigVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询URL配置信息表")
    @GetMapping(value = "findUrlConfig")
    public R<UrlConfigDto> findUrlConfig(UrlConfigVo urlConfigVo) {
        UrlConfigDto urlConfigDto = UCopy.copyVo2Dto(urlConfigVo, UrlConfigDto.class);
        UrlConfigDto tableResult = urlConfigService.findUrlConfig(urlConfigDto);
        return R.ok(tableResult);
    }

    /**
    * 根据ID查询URL配置信息表
    *
    * @param id 主键
    * @return 形式返回
    */
    @Operation(summary = "（缓存）根据ID查询URL配置信息表")
    @GetMapping(value = "findCacheUrlConfigById")
    public R<UrlConfigDto> findCacheUrlConfigById(Long id) {
        UrlConfigDto tableResult = urlConfigService.findCacheUrlConfigById(id);
        return R.ok(tableResult);
    }

    /**
    * 删除URL配置信息表
    *
    * @param id 主键
    * @return 形式返回
    */
    @Operation(summary = "删除URL配置信息表")
    @PostMapping(value = "deleteUrlConfig")
    public R<Void> deleteUrlConfig(Long id) {
        urlConfigService.deleteUrlConfig(id);
        return R.ok();
    }
}
