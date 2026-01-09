package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.constant.MenuPermission;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.UrlConfigDto;
import com.freesia.service.UrlConfigService;
import com.freesia.vo.UrlConfigVo;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 控制器
 * @date 2024-01-24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/urlConfigController")
@Tag(name = "UrlConfigController", description = "URL配置信息表 控制器")
public class UrlConfigController extends BaseController {
    private final UrlConfigService urlConfigService;

    /**
     * 保存URL配置信息表信息
     *
     * @return 形式返回
     */
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.COMMON_URL_ADD),
            @SaCheckPermission(value = MenuPermission.COMMON_URL_EDIT),
    })
    @Operation(summary = "保存URL配置信息表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody UrlConfigVo urlConfigVo) {
        UrlConfigDto urlConfigDto = UCopy.copyVo2Dto(urlConfigVo, UrlConfigDto.class);
        urlConfigService.saveUpdate(urlConfigDto);
        return R.ok();
    }

    /**
     * 批量保存URL配置信息表信息
     *
     * @return 形式返回
     */
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.COMMON_URL_ADD),
            @SaCheckPermission(value = MenuPermission.COMMON_URL_EDIT),
    })
    @Operation(summary = "保存URL配置信息表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<UrlConfigVo> urlConfigVoList) {
        List<UrlConfigDto> urlConfigDtoList = UCopy.fullCopyList(urlConfigVoList, UrlConfigDto.class);
        urlConfigService.saveUpdateBatch(urlConfigDtoList);
        return R.ok();
    }

    /**
     * 查询URL配置信息表分页信息
     *
     * @param urlConfigVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @SaCheckPermission(value = MenuPermission.COMMON_URL_INDEX)
    @Operation(summary = "查询URL配置信息表分页信息")
    @GetMapping(value = "findPageUrlConfig")
    public TableResult<UrlConfigDto> findPageUrlConfig(UrlConfigVo urlConfigVo, PageQuery pageQuery) {
        UrlConfigDto urlConfigDto = UCopy.copyVo2Dto(urlConfigVo, UrlConfigDto.class);
        return urlConfigService.findPage(urlConfigDto, pageQuery);
    }

    /**
     * 条件查询URL配置信息表
     *
     * @param urlConfigVo 查询条件
     * @return 形式返回
     */
    @SaCheckPermission(value = MenuPermission.COMMON_URL_INDEX)
    @Operation(summary = "条件查询URL配置信息表")
    @GetMapping(value = "findUrlConfig")
    public R<UrlConfigDto> findUrlConfig(UrlConfigVo urlConfigVo) {
        UrlConfigDto urlConfigDto = UCopy.copyVo2Dto(urlConfigVo, UrlConfigDto.class);
        UrlConfigDto tableResult = urlConfigService.findOne(urlConfigDto);
        return R.ok(tableResult);
    }

    /**
     * 根据ID查询URL配置信息表
     *
     * @param code 配置标识
     * @return 形式返回
     */
    @Operation(summary = "（缓存）根据配置标识查询URL配置信息表")
    @GetMapping(value = "findCacheUrlConfigById")
    public R<UrlConfigDto> findCacheUrlConfigById(String code) {
        UrlConfigDto tableResult = urlConfigService.findCacheUrlConfigByCode(code);
        return R.ok(tableResult);
    }

    /**
     * 删除URL配置信息表
     *
     * @param code 配置标识
     * @return 形式返回
     */
    @Validated
    @SaCheckPermission(value = MenuPermission.COMMON_URL_DELETE)
    @Operation(summary = "删除URL配置信息表")
    @DeleteMapping(value = "deleteUrlConfig")
    public R<Void> deleteUrlConfig(@NotNull @RequestParam Long id, @NotEmpty @RequestParam String code) {
        urlConfigService.deleteUrlConfig(id, code);
        return R.ok();
    }
}
