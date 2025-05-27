package com.freesia.icon.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import com.freesia.constant.MenuPermission;
import com.freesia.controller.BaseController;
import com.freesia.exception.ServiceException;
import com.freesia.exception.UserException;
import com.freesia.icon.constant.CommonIconModule;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;
import com.freesia.icon.service.CommonIconTemplateHeaderService;
import com.freesia.icon.vo.CommonIconTemplateHeaderVo;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.pojo.LaySelect;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 控制器
 * @date 2025-04-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/commonIconTemplateHeaderController")
@Tag(name = "CommonIconTemplateHeaderController", description = "通用图标模板头表 控制器")
public class CommonIconTemplateHeaderController extends BaseController {
    private final CommonIconTemplateHeaderService commonIconTemplateHeaderService;

    /**
     * 保存通用图标模板头表信息
     *
     * @param commonIconTemplateHeaderVo 待保存对象
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存通用图标模板头表信息")
    @PostMapping(value = "saveUpdate")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_TEMPLATE_HEADER_ADD),
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_TEMPLATE_HEADER_EDIT),
    })
    public R<Void> saveUpdate(@RequestBody CommonIconTemplateHeaderVo commonIconTemplateHeaderVo) {
        CommonIconTemplateHeaderDto commonIconTemplateHeaderDto = UCopy.copyVo2Dto(commonIconTemplateHeaderVo, CommonIconTemplateHeaderDto.class);
        commonIconTemplateHeaderService.saveUpdate(commonIconTemplateHeaderDto);
        return R.ok();
    }

    /**
     * 批量保存通用图标模板头表信息
     *
     * @param commonIconTemplateHeaderVoList 待保存对象
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存通用图标模板头表信息")
    @PostMapping(value = "saveUpdateBatch")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_TEMPLATE_HEADER_ADD),
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_TEMPLATE_HEADER_EDIT),
    })
    public R<Void> saveUpdateBatch(@RequestBody List<CommonIconTemplateHeaderVo> commonIconTemplateHeaderVoList) {
        List<CommonIconTemplateHeaderDto> commonIconTemplateHeaderDtoList = UCopy.fullCopyList(commonIconTemplateHeaderVoList, CommonIconTemplateHeaderDto.class);
        commonIconTemplateHeaderService.saveUpdateBatch(commonIconTemplateHeaderDtoList);
        return R.ok();
    }

    /**
     * 查询通用图标模板头表分页信息
     *
     * @param commonIconTemplateHeaderVo 查询条件
     * @param pageQuery                  分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询通用图标模板头表分页信息")
    @GetMapping(value = "findPageCommonIconTemplateHeader")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_TEMPLATE_HEADER_INDEX})
    public TableResult<CommonIconTemplateHeaderDto> findPageCommonIconTemplateHeader(CommonIconTemplateHeaderVo commonIconTemplateHeaderVo, PageQuery pageQuery) {
        CommonIconTemplateHeaderDto commonIconTemplateHeaderDto = UCopy.copyVo2Dto(commonIconTemplateHeaderVo, CommonIconTemplateHeaderDto.class);
        return commonIconTemplateHeaderService.findPageCommonIconTemplateHeader(commonIconTemplateHeaderDto, pageQuery);
    }

    /**
     * 条件查询通用图标模板头表
     *
     * @param commonIconTemplateHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询通用图标模板头表")
    @GetMapping(value = "findCommonIconTemplateHeader")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_TEMPLATE_HEADER_INDEX})
    public R<CommonIconTemplateHeaderDto> findCommonIconTemplateHeader(CommonIconTemplateHeaderVo commonIconTemplateHeaderVo) {
        CommonIconTemplateHeaderDto commonIconTemplateHeaderDto = UCopy.copyVo2Dto(commonIconTemplateHeaderVo, CommonIconTemplateHeaderDto.class);
        CommonIconTemplateHeaderDto tableResult = commonIconTemplateHeaderService.findCommonIconTemplateHeader(commonIconTemplateHeaderDto);
        return R.ok(tableResult);
    }

    /**
     * 删除通用图标模板头表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "删除通用图标模板头表")
    @PostMapping(value = "deleteCommonIconTemplateHeader")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_TEMPLATE_HEADER_DELETE})
    public R<Void> deleteCommonIconTemplateHeader(@RequestBody List<Long> idList) {
        commonIconTemplateHeaderService.deleteCommonIconTemplateHeader(idList);
        return R.ok();
    }

    @Operation(summary = "查询自增排序号")
    @GetMapping(value = "findMaxOrderNum")
    public R<Integer> findMaxOrderNum() {
        int maxOrderNum = Convert.toInt(commonIconTemplateHeaderService.findMaxOrderNum(), 0);
        maxOrderNum = (maxOrderNum / 10) * 10 + 10;
        return R.ok(maxOrderNum);
    }

    @Operation(summary = "查询通用图标模板头表下拉数据")
    @GetMapping(value = "findSelectCommonIconHeader")
    public R<List<LaySelect>> findSelectCommonIconHeader() {
        Long userId = USecurity.getUserId();
        List<LaySelect> list = commonIconTemplateHeaderService.findSelectCommonIconHeader(userId);
        return R.ok(list);
    }

    @Validated
    @Operation(summary = "根据用户ID查询开销类型下拉集合")
    @GetMapping(value = "findListSelectCostType")
    public R<List<LaySelect>> findListSelectCostType() {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        List<LaySelect> laySelectList = commonIconTemplateHeaderService.findListSelectCostType(userId);
        return R.ok(laySelectList);
    }
}
