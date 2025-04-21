package com.freesia.icon.controller;

import com.freesia.controller.BaseController;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.entity.FindCommonIconTemplateDetailEntity;
import com.freesia.icon.entity.FindTreeIconTreeTypeEntity;
import com.freesia.icon.service.CommonIconTemplateDetailService;
import com.freesia.icon.vo.CommonIconTemplateDetailVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 控制器
 * @date 2025-04-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/commonIconTemplateDetailController")
@Tag(name = "CommonIconTemplateDetailController", description = "通用图标模板表 控制器")
public class CommonIconTemplateDetailController extends BaseController {
    private final CommonIconTemplateDetailService commonIconTemplateDetailService;

    /**
     * 保存通用图标模板表信息
     *
     * @param commonIconTemplateDetailVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存通用图标模板表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody CommonIconTemplateDetailVo commonIconTemplateDetailVo) {
        CommonIconTemplateDetailDto commonIconTemplateDetailDto = UCopy.copyVo2Dto(commonIconTemplateDetailVo, CommonIconTemplateDetailDto.class);
        commonIconTemplateDetailService.saveUpdate(commonIconTemplateDetailDto);
        return R.ok();
    }

    /**
     * 批量保存通用图标模板表信息
     *
     * @param commonIconTemplateDetailVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存通用图标模板表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<CommonIconTemplateDetailVo> commonIconTemplateDetailVoList) {
        List<CommonIconTemplateDetailDto> commonIconTemplateDetailDtoList = UCopy.fullCopyList(commonIconTemplateDetailVoList, CommonIconTemplateDetailDto.class);
        commonIconTemplateDetailService.saveUpdateBatch(commonIconTemplateDetailDtoList);
        return R.ok();
    }

    /**
     * 查询通用图标模板表分页信息
     *
     * @param commonIconTemplateDetailVo 查询条件
     * @param pageQuery                  分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询通用图标模板表分页信息")
    @GetMapping(value = "findPageCommonIconTemplateDetail")
    public TableResult<CommonIconTemplateDetailDto> findPageCommonIconTemplateDetail(CommonIconTemplateDetailVo commonIconTemplateDetailVo, PageQuery pageQuery) {
        CommonIconTemplateDetailDto commonIconTemplateDetailDto = UCopy.copyVo2Dto(commonIconTemplateDetailVo, CommonIconTemplateDetailDto.class);
        return commonIconTemplateDetailService.findPageCommonIconTemplateDetail(commonIconTemplateDetailDto, pageQuery);
    }

    /**
     * 条件查询通用图标模板表
     *
     * @param commonIconTemplateDetailVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询通用图标模板表")
    @GetMapping(value = "findCommonIconTemplateDetail")
    public R<FindCommonIconTemplateDetailEntity> findCommonIconTemplateDetail(CommonIconTemplateDetailVo commonIconTemplateDetailVo) {
        CommonIconTemplateDetailDto commonIconTemplateDetailDto = UCopy.copyVo2Dto(commonIconTemplateDetailVo, CommonIconTemplateDetailDto.class);
        FindCommonIconTemplateDetailEntity tableResult = commonIconTemplateDetailService.findCommonIconTemplateDetail(commonIconTemplateDetailDto);
        return R.ok(tableResult);
    }

    /**
     * 删除通用图标模板表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除通用图标模板表")
    @PostMapping(value = "deleteCommonIconTemplateDetail")
    public R<Void> deleteCommonIconTemplateDetail(@RequestBody List<Long> idList) {
        commonIconTemplateDetailService.deleteCommonIconTemplateDetail(idList);
        return R.ok();
    }

    /**
     * 查询通用图标模板明细的节点数据
     *
     * @param commonIconTemplateDetailVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "查询通用图标模板明细的节点数据")
    @GetMapping(value = "findTreeIconTreeType")
    public R<List<FindTreeIconTreeTypeEntity>> findTreeIconTreeType(CommonIconTemplateDetailVo commonIconTemplateDetailVo) {
        CommonIconTemplateDetailDto commonIconTemplateDetailDto = UCopy.copyVo2Dto(commonIconTemplateDetailVo, CommonIconTemplateDetailDto.class);
        List<FindTreeIconTreeTypeEntity> resultEntiytList = commonIconTemplateDetailService.findTreeIconTreeType(commonIconTemplateDetailDto);
        return R.ok(resultEntiytList);
    }
}
