package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.AdminConstant;
import com.freesia.dto.SysDictDto;
import com.freesia.dto.SysDictKeyDto;
import com.freesia.dto.SysDictValueDto;
import com.freesia.entity.FindPageSysDictKeyEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysDictKeyService;
import com.freesia.service.SysDictValueService;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import com.freesia.vo.SysDictKeyVo;
import com.freesia.vo.SysDictValueVo;
import com.freesia.vo.SysDictVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 控制器
 * @date 2023-09-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysDictController")
@Tag(name = "SysDictController", description = "字典键信息表 控制器")
public class SysDictController {
    private final SysDictKeyService sysDictKeyService;
    private final SysDictValueService sysDictValueService;

    @Operation(summary = "查询字典键数据列表")
    @GetMapping(value = "findSysDictKeyList")
    public R<List<SysDictKeyDto>> findSysDictKeyList(SysDictKeyVo sysDictKeyVo) {
        SysDictKeyDto sysDictKeyDto = new SysDictKeyDto();
        UCopy.fullCopy(sysDictKeyVo, sysDictKeyDto);
        List<SysDictKeyDto> sysDictKeyEntityList = sysDictKeyService.findSysDictKeyList(sysDictKeyDto);
        return R.ok(sysDictKeyEntityList);
    }

    @Operation(summary = "查询字典值分页数据")
    @GetMapping(value = "findPageSysDictValue")
    public TableResult<SysDictValueDto> findPageSysDictValue(SysDictVo sysDictValueVo, PageQuery pageQuery) {
        SysDictValueDto sysDictValueDto = new SysDictValueDto();
        UCopy.fullCopy(sysDictValueVo, sysDictValueDto);
        return sysDictValueService.findPageSysDictValue(sysDictValueDto, pageQuery);
    }

    @Operation(summary = "查询字典值列表数据")
    @GetMapping(value = "findSysDictValueList")
    public R<List<SysDictValueDto>> findSysDictValueList(@Valid SysDictVo sysDictValueVo) {
        SysDictValueDto sysDictValueDto = new SysDictValueDto();
        UCopy.fullCopy(sysDictValueVo, sysDictValueDto);
        List<SysDictValueDto> sysDictValueDtoList = sysDictValueService.findSysDictValueList(sysDictValueDto);
        return R.ok(sysDictValueDtoList);
    }

    @Operation(summary = "（缓存）查询字典值列表数据")
    @GetMapping(value = "findCacheSysDictValueList")
    public R<List<SysDictValueDto>> findCacheSysDictValueList(@RequestParam String dictKey) {
        List<SysDictValueDto> sysDictValueDtoList = sysDictValueService.findCacheSysDictValueList(dictKey);
        return R.ok(sysDictValueDtoList);
    }

    @Operation(summary = "查询字典数据的分页信息")
    @GetMapping(value = "findPageSysDictList")
    public TableResult<FindPageSysDictKeyEntity> findPageSysDictList(SysDictVo sysDictVo, PageQuery pageQuery) {
        SysDictDto sysDictDto = new SysDictDto();
        UCopy.fullCopy(sysDictVo, sysDictDto);
        return sysDictKeyService.findPageSysDictList(sysDictDto, pageQuery);
    }

    @Operation(summary = "保存字典键数据")
    @PostMapping(value = "saveSysDictKeyList")
    public R<Void> saveSysDictKeyList(@RequestBody String request) {
        List<SysDictKeyVo> sysDictKeyVoList = JSONObject.parseArray(request, SysDictKeyVo.class);
        List<SysDictKeyDto> sysDictKeyDtoList = UCopy.fullCopyList(sysDictKeyVoList, SysDictKeyDto.class);
        sysDictKeyService.saveUpdateBatch(sysDictKeyDtoList);
        return R.ok();
    }

    @Operation(summary = "保存字典值数据")
    @PostMapping(value = "saveSysDictValueList")
    @SaCheckRole(value = AdminConstant.ADMIN)
    public R<Void> saveSysDictValueList(@RequestBody String request) {
        List<SysDictValueVo> sysDictValueVoList = JSONObject.parseArray(request, SysDictValueVo.class);
        List<SysDictValueDto> sysDictValueDtoList = UCopy.fullCopyList(sysDictValueVoList, SysDictValueDto.class);
        sysDictValueService.saveUpdateBatch(sysDictValueDtoList);
        return R.ok();
    }

    @Operation(summary = "保存字典键数据")
    @PostMapping(value = "saveSysDictKey")
    public R<SysDictKeyDto> saveSysDictKey(@RequestBody String request) {
        SysDictKeyVo sysDictKeyVo = JSONObject.parseObject(request, SysDictKeyVo.class);
        SysDictKeyDto sysDictKeyDto = UCopy.copyVo2Dto(sysDictKeyVo, SysDictKeyDto.class);
        sysDictKeyDto = sysDictKeyService.saveSysDictKey(sysDictKeyDto);
        return R.ok(sysDictKeyDto);
    }

    @Operation(summary = "保存字典值数据")
    @PostMapping(value = "saveSysDictValue")
    public R<SysDictValueDto> saveSysDictValue(@RequestBody String request) {
        SysDictValueVo sysDictValueVo = JSONObject.parseObject(request, SysDictValueVo.class);
        SysDictValueDto sysDictValueDto = UCopy.copyVo2Dto(sysDictValueVo, SysDictValueDto.class);
        sysDictValueDto = sysDictValueService.saveSysDictValue(sysDictValueDto);
        return R.ok(sysDictValueDto);
    }

    @Operation(summary = "删除字典键")
    @PutMapping(value = "deleteSysDictValueList")
    public R<Void> deleteSysDictValueList(@RequestBody String request) {
        List<Long> idList = JSONObject.parseArray(request, Long.class);
        sysDictValueService.deleteSysDictValueList(idList);
        return R.ok();
    }

    @Operation(summary = "启用/禁用字典键")
    @PutMapping(value = "enableSysDictValueList")
    public R<Void> enableSysDictValueList(@RequestBody String request) {
        List<Long> idList = JSONObject.parseArray(request, Long.class);
        sysDictValueService.enableSysDictValueList(idList);
        return R.ok(null, idList.size());
    }

    @Operation(summary = "刷新字典缓存值")
    @DeleteMapping(value = "flushCacheSysDictValue")
    public R<Void> flushCacheSysDictValue(@RequestParam String dictKey) {
        try {
            sysDictValueService.flushCacheSysDictValue(dictKey);
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed();
        }
        return R.failed();
    }
}
