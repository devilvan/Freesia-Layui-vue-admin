package com.freesia.controller;

import cn.hutool.http.HttpStatus;
import com.freesia.converter.SysColumnHeaderConverter;
import com.freesia.dto.SysColumnDetailDto;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.dto.SysColumnMiddleDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysColumnDetailService;
import com.freesia.service.SysColumnHeaderService;
import com.freesia.service.SysColumnMiddleService;
import com.freesia.util.UCollection;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.vo.DefaultColumnVo;
import com.freesia.vo.R;
import com.freesia.vo.SysColumnHeaderVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Evad.Wu
 * @Description 系统列头表 控制器
 * @date 2026-03-27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysColumnHeaderController")
@Tag(name = "SysColumnHeaderController", description = "系统列头表 控制器")
public class SysColumnHeaderController extends BaseController {
    private final SysColumnHeaderService sysColumnHeaderService;
    private final SysColumnHeaderConverter sysColumnHeaderConverter;
    private final SysColumnDetailService sysColumnDetailService;
    private final SysColumnMiddleService sysColumnMiddleService;

    /**
     * 保存系统列头表信息
     *
     * @param sysColumnHeaderVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列头表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysColumnHeaderVo sysColumnHeaderVo) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        sysColumnHeaderService.saveUpdate(sysColumnHeaderDto);
        return R.ok();
    }

    /**
     * 批量保存系统列头表信息
     *
     * @param sysColumnHeaderVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存系统列头表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysColumnHeaderVo> sysColumnHeaderVoList) {
        List<SysColumnHeaderDto> sysColumnHeaderDtoList = sysColumnHeaderConverter.convertBatchVo2Dto(sysColumnHeaderVoList);
        sysColumnHeaderService.saveUpdateBatch(sysColumnHeaderDtoList);
        return R.ok();
    }

    /**
     * 查询系统列头表分页信息
     *
     * @param sysColumnHeaderVo 查询条件
     * @param pageQuery         分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询系统列头表分页信息")
    @GetMapping(value = "findPageSysColumnHeader")
    public TableResult<SysColumnHeaderDto> findPageSysColumnHeader(SysColumnHeaderVo sysColumnHeaderVo, PageQuery pageQuery) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        return sysColumnHeaderService.findPage(sysColumnHeaderDto, pageQuery);
    }


    /**
     * 条件查询系统列头表
     *
     * @param sysColumnHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统列头表")
    @GetMapping(value = "findListSysColumnHeader")
    public R<List<SysColumnHeaderDto>> findListSysColumnHeader(SysColumnHeaderVo sysColumnHeaderVo) {
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        List<SysColumnHeaderDto> sysColumnHeaderDtoList = sysColumnHeaderService.findList(sysColumnHeaderDto);
        return R.ok(sysColumnHeaderDtoList);
    }

    /**
     * 删除系统列头表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除系统列头表")
    @PostMapping(value = "deleteSysColumnHeader")
    public R<Void> deleteSysColumnHeader(@RequestBody List<Long> idList) {
        sysColumnHeaderService.deleteBatch(idList);
        return R.ok();
    }

    /**
     * 条件查询系统列头表
     *
     * @param sysColumnHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询系统列头表")
    @PostMapping(value = "findSysColumnHeader")
    public R<SysColumnHeaderDto> findSysColumnHeader(@RequestBody SysColumnHeaderVo sysColumnHeaderVo) {
        String errorMsg = validFindSysColumnHeader(sysColumnHeaderVo);
        if (UEmpty.isNotEmpty(errorMsg)) {
            return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, errorMsg);
        }
        Long userId = USecurity.getUserId();
        List<DefaultColumnVo> defaultColumnVoList = sysColumnHeaderVo.getDefaultColumnVoList();
        SysColumnHeaderDto sysColumnHeaderDto = sysColumnHeaderConverter.convertVo2Dto(sysColumnHeaderVo);
        SysColumnHeaderDto findHeader = sysColumnHeaderService.findOne(sysColumnHeaderDto);
        if (findHeader != null) {
            SysColumnMiddleDto sysColumnMiddleDto = new SysColumnMiddleDto();
            Long headerId = findHeader.getId();
            sysColumnMiddleDto.setHeaderId(headerId);
            List<SysColumnMiddleDto> sysColumnMiddleDtoList = sysColumnMiddleService.findCacheList(sysColumnMiddleDto);
            if (UEmpty.isNotEmpty(sysColumnMiddleDtoList)) {
                Map<String, SysColumnMiddleDto> middleNameMap = sysColumnMiddleDtoList.stream().collect(Collectors.toMap(SysColumnMiddleDto::getName, item -> item));
                SysColumnDetailDto sysColumnDetailDto = new SysColumnDetailDto();
                sysColumnDetailDto.setHeaderId(headerId);
                sysColumnDetailDto.setUserId(userId);
                sysColumnDetailDto.setCustomSlot(sysColumnMiddleDto.getCustomSlot());
                List<SysColumnDetailDto> sysColumnDetailDtoList = sysColumnDetailService.findCacheList(sysColumnDetailDto);
                if (UEmpty.isNotEmpty(sysColumnDetailDtoList)) {
                    sysColumnDetailDtoList = sysColumnDetailDtoList.stream().map(dto -> {
                        dto.setCustomSlot(middleNameMap.get(dto.getName()).getCustomSlot());
                        dto.setTotalRow(middleNameMap.get(dto.getName()).getTotalRow());
                        return dto;
                    }).collect(Collectors.toList());
                    findHeader.setSysColumnDetailDtoList(sysColumnDetailDtoList);
                    return R.ok(findHeader);
                } else {
                    // 无缓存
                    List<SysColumnDetailDto> list = buildSysColumnDetailDtoList(defaultColumnVoList, userId, headerId, middleNameMap);
                    if (UEmpty.isNotEmpty(list)) {
                        findHeader.setSysColumnDetailDtoList(sysColumnDetailService.saveUpdateBatch(list));
                        return R.ok(findHeader);
                    }
                }
            } else {
                // 无则生成中间表数据
                sysColumnMiddleDtoList = buildSysColumnMiddleDto(defaultColumnVoList, headerId);
                if (UEmpty.isNotEmpty(sysColumnMiddleDtoList)) {
                    sysColumnMiddleService.saveUpdateBatch(sysColumnMiddleDtoList);
                }
                Map<String, SysColumnMiddleDto> middleNameMap = sysColumnMiddleDtoList.stream().collect(Collectors.toMap(SysColumnMiddleDto::getName, item -> item));
                // 生成明细数据
                List<SysColumnDetailDto> list = buildSysColumnDetailDtoList(defaultColumnVoList, userId, headerId, middleNameMap);
                if (UEmpty.isNotEmpty(list)) {
                    List<SysColumnDetailDto> sysColumnDetailDtoList = sysColumnDetailService.saveUpdateBatch(list);
                    for (SysColumnDetailDto dto : sysColumnDetailDtoList) {
                        dto.setCustomSlot(middleNameMap.get(dto.getName()).getCustomSlot());
                        dto.setTotalRow(middleNameMap.get(dto.getName()).getTotalRow());
                    }
                    findHeader.setSysColumnDetailDtoList(sysColumnDetailDtoList);
                    return R.ok(findHeader);
                }
            }
        } else {
            SysColumnHeaderDto beforeSaveSysColumnHeaderDto = buildSysColumnHeaderDto(sysColumnHeaderDto);
            findHeader = sysColumnHeaderService.saveUpdate(beforeSaveSysColumnHeaderDto);
            if (findHeader != null) {
                Long headerId = findHeader.getId();
                // 无则生成中间表数据
                List<SysColumnMiddleDto> sysColumnMiddleDtoList = buildSysColumnMiddleDto(defaultColumnVoList, headerId);
                if (UEmpty.isNotEmpty(sysColumnMiddleDtoList)) {
                    sysColumnMiddleService.saveUpdateBatch(sysColumnMiddleDtoList);
                }
                Map<String, SysColumnMiddleDto> middleNameMap = sysColumnMiddleDtoList.stream().collect(Collectors.toMap(SysColumnMiddleDto::getName, item -> item));
                // 生成明细数据
                List<SysColumnDetailDto> list = buildSysColumnDetailDtoList(defaultColumnVoList, userId, headerId, middleNameMap);
                if (UEmpty.isNotEmpty(list)) {
                    List<SysColumnDetailDto> sysColumnDetailDtoList = sysColumnDetailService.saveUpdateBatch(list);
                    for (SysColumnDetailDto dto : sysColumnDetailDtoList) {
                        dto.setCustomSlot(middleNameMap.get(dto.getName()).getCustomSlot());
                        dto.setTotalRow(middleNameMap.get(dto.getName()).getTotalRow());
                    }
                    findHeader.setSysColumnDetailDtoList(sysColumnDetailDtoList);
                    return R.ok(findHeader);
                }
            }
        }
        return R.ok(findHeader);
    }

    private SysColumnHeaderDto buildSysColumnHeaderDto(SysColumnHeaderDto sysColumnHeaderDto) {
        SysColumnHeaderDto beforeSaveSysColumnHeaderDto = new SysColumnHeaderDto();
        beforeSaveSysColumnHeaderDto.setName(sysColumnHeaderDto.getName());
        beforeSaveSysColumnHeaderDto.setDescription(sysColumnHeaderDto.getDescription());
        beforeSaveSysColumnHeaderDto.setEnabled(true);
        beforeSaveSysColumnHeaderDto.setResizeFlag(false);
        beforeSaveSysColumnHeaderDto.setAutoColsWidthFlag(false);
        beforeSaveSysColumnHeaderDto.setDefaultToolBarFlag(false);
        return beforeSaveSysColumnHeaderDto;
    }

    private List<SysColumnMiddleDto> buildSysColumnMiddleDto(List<DefaultColumnVo> defaultColumnVoList, Long headerId) {
        List<SysColumnMiddleDto> list = UCollection.optimizeInitialCapacityArrayList(defaultColumnVoList.size());
        int orderNum = 10;
        for (DefaultColumnVo item : defaultColumnVoList) {
            SysColumnMiddleDto sysColumnMiddleDto = new SysColumnMiddleDto();
            sysColumnMiddleDto.setHeaderId(headerId);
            sysColumnMiddleDto.setTitle(item.getTitle());
            sysColumnMiddleDto.setName(item.getKey());
            sysColumnMiddleDto.setEnabled(true);
            sysColumnMiddleDto.setCustomSlot(item.getCustomSlot());
            sysColumnMiddleDto.setTotalRow(item.getTotalRow());
            list.add(sysColumnMiddleDto);
            orderNum += 10;
        }
        return list;
    }

    private List<SysColumnDetailDto> buildSysColumnDetailDtoList(List<DefaultColumnVo> defaultColumnVoList, Long userId, Long headerId, Map<String, SysColumnMiddleDto> middleNameMap) {
        List<SysColumnDetailDto> list = UCollection.optimizeInitialCapacityArrayList(defaultColumnVoList.size());
        int orderNum = 10;
        for (DefaultColumnVo item : defaultColumnVoList) {
            SysColumnDetailDto columnDetailDto = new SysColumnDetailDto();
            columnDetailDto.setUserId(userId);
            columnDetailDto.setHeaderId(headerId);
            SysColumnMiddleDto sysColumnMiddleDto = middleNameMap.get(item.getKey());
            columnDetailDto.setMiddleId(sysColumnMiddleDto.getHeaderId());
            columnDetailDto.setTitle(item.getTitle());
            columnDetailDto.setName(item.getKey());
            columnDetailDto.setEnabled(true);
            columnDetailDto.setFixed(item.getFixed());
            columnDetailDto.setEllipsisTooltip(item.getEllipsisTooltip());
            columnDetailDto.setWidth(item.getWidth());
            columnDetailDto.setMinWidth(item.getMinWidth());
            columnDetailDto.setOrderNum(orderNum);
            columnDetailDto.setSorted(item.getSorted());
            columnDetailDto.setResizeFlag(item.getResizeFlag());
            columnDetailDto.setCustomSlot(item.getCustomSlot());
            list.add(columnDetailDto);
            orderNum += 10;
        }
        return list;
    }

    /**
     * 校验参数
     *
     * @param sysColumnHeaderVo 入参
     * @return 错误信息
     */
    private String validFindSysColumnHeader(SysColumnHeaderVo sysColumnHeaderVo) {
        String name = sysColumnHeaderVo.getName();
        if (UEmpty.isEmpty(name)) {
            return UMessage.message("column.name.not.empty");
        }
        List<DefaultColumnVo> defaultColumnVoList = sysColumnHeaderVo.getDefaultColumnVoList();
        if (UEmpty.isEmpty(defaultColumnVoList)) {
            return UMessage.message("column.default.not.empty");
        }
        return null;
    }
}
