package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysDictDto;
import com.freesia.dto.SysDictKeyDto;
import com.freesia.dto.SysDictValueDto;
import com.freesia.entity.FindPageSysDictKeyEntity;
import com.freesia.entity.SysDictValueImportEntity;
import com.freesia.excel.DictValueImportListener;
import com.freesia.excel.constant.ExcelSuffix;
import com.freesia.excel.util.UExcel;
import com.freesia.exception.ServiceException;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.oss.exception.OssException;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysDictKeyService;
import com.freesia.service.SysDictValueService;
import com.freesia.util.UCopy;
import com.freesia.util.UMessage;
import com.freesia.vo.R;
import com.freesia.vo.SysDictKeyVo;
import com.freesia.vo.SysDictValueVo;
import com.freesia.vo.SysDictVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 控制器
 * @date 2023-09-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysDictController")
@Tag(name = "SysDictController", description = "字典键信息表 控制器")
public class SysDictController extends BaseController {
    private final SysDictKeyService sysDictKeyService;
    private final SysDictValueService sysDictValueService;

    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_INDEX)
    @Operation(summary = "查询字典键数据列表")
    @GetMapping(value = "findSysDictKeyList")
    public R<List<SysDictKeyDto>> findSysDictKeyList(SysDictKeyVo sysDictKeyVo) {
        SysDictKeyDto sysDictKeyDto = new SysDictKeyDto();
        UCopy.fullCopy(sysDictKeyVo, sysDictKeyDto);
        List<SysDictKeyDto> sysDictKeyEntityList = sysDictKeyService.findSysDictKeyList(sysDictKeyDto);
        return R.ok(sysDictKeyEntityList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_INDEX)
    @Operation(summary = "查询字典值分页数据")
    @GetMapping(value = "findPageSysDictValue")
    public TableResult<SysDictValueDto> findPageSysDictValue(SysDictVo sysDictValueVo, PageQuery pageQuery) {
        SysDictValueDto sysDictValueDto = new SysDictValueDto();
        UCopy.fullCopy(sysDictValueVo, sysDictValueDto);
        return sysDictValueService.findPageSysDictValue(sysDictValueDto, pageQuery);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_INDEX)
    @Operation(summary = "查询字典值列表数据")
    @GetMapping(value = "findSysDictValueList")
    public R<List<SysDictValueDto>> findSysDictValueList(@Valid SysDictVo sysDictValueVo) {
        SysDictValueDto sysDictValueDto = new SysDictValueDto();
        UCopy.fullCopy(sysDictValueVo, sysDictValueDto);
        List<SysDictValueDto> sysDictValueDtoList = sysDictValueService.findSysDictValueList(sysDictValueDto);
        return R.ok(sysDictValueDtoList);
    }

    @SaCheckLogin
    @Operation(summary = "（缓存）查询字典值列表数据")
    @GetMapping(value = "findCacheSysDictValueList")
    public R<List<SysDictValueDto>> findCacheSysDictValueList(@RequestParam String dictKey) {
        List<SysDictValueDto> sysDictValueDtoList = sysDictValueService.findCacheSysDictValueList(dictKey);
        return R.ok(sysDictValueDtoList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_INDEX)
    @Operation(summary = "查询字典数据的分页信息")
    @GetMapping(value = "findPageSysDictList")
    public TableResult<FindPageSysDictKeyEntity> findPageSysDictList(SysDictVo sysDictVo, PageQuery pageQuery) {
        SysDictDto sysDictDto = new SysDictDto();
        UCopy.fullCopy(sysDictVo, sysDictDto);
        return sysDictKeyService.findPageSysDictList(sysDictDto, pageQuery);
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_EDIT),
    })
    @Operation(summary = "保存字典键数据")
    @PostMapping(value = "saveSysDictKeyList")
    public R<Void> saveSysDictKeyList(@RequestBody String request) {
        List<SysDictKeyVo> sysDictKeyVoList = JSONObject.parseArray(request, SysDictKeyVo.class);
        List<SysDictKeyDto> sysDictKeyDtoList = UCopy.fullCopyList(sysDictKeyVoList, SysDictKeyDto.class);
        sysDictKeyService.saveUpdateBatch(sysDictKeyDtoList);
        return R.ok();
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_KEY_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_KEY_EDIT),
    })
    @Operation(summary = "保存字典键数据")
    @PostMapping(value = "saveSysDictKey")
    public R<SysDictKeyDto> saveSysDictKey(@RequestBody String request) {
        SysDictKeyVo sysDictKeyVo = JSONObject.parseObject(request, SysDictKeyVo.class);
        SysDictKeyDto sysDictKeyDto = UCopy.copyVo2Dto(sysDictKeyVo, SysDictKeyDto.class);
        sysDictKeyDto = sysDictKeyService.saveSysDictKey(sysDictKeyDto);
        return R.ok(sysDictKeyDto);
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_EDIT),
    })
    @Operation(summary = "保存字典值数据")
    @PostMapping(value = "saveSysDictValue")
    public R<SysDictValueDto> saveSysDictValue(@RequestBody String request) {
        SysDictValueVo sysDictValueVo = JSONObject.parseObject(request, SysDictValueVo.class);
        SysDictValueDto sysDictValueDto = UCopy.copyVo2Dto(sysDictValueVo, SysDictValueDto.class);
        sysDictValueDto = sysDictValueService.saveSysDictValue(sysDictValueDto);
        return R.ok(sysDictValueDto);
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_EDIT),
    })
    @Operation(summary = "保存字典值数据")
    @PostMapping(value = "saveSysDictValueList")
    public R<Void> saveSysDictValueList(@RequestBody String request) {
        List<SysDictValueVo> sysDictValueVoList = JSONObject.parseArray(request, SysDictValueVo.class);
        List<SysDictValueDto> sysDictValueDtoList = UCopy.fullCopyList(sysDictValueVoList, SysDictValueDto.class);
        sysDictValueService.saveUpdateBatch(sysDictValueDtoList);
        return R.ok();
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_DELETE)
    @Operation(summary = "删除字典值")
    @PutMapping(value = "deleteSysDictValueList")
    public R<Void> deleteSysDictValueList(@RequestBody String request) {
        List<Long> idList = JSONObject.parseArray(request, Long.class);
        sysDictValueService.deleteSysDictValueList(idList);
        return R.ok();
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_ENABLED)
    @Operation(summary = "启用/禁用字典键")
    @PutMapping(value = "enableSysDictValueList")
    public R<Void> enableSysDictValueList(@RequestBody String request) {
        List<Long> idList = JSONObject.parseArray(request, Long.class);
        sysDictValueService.enableSysDictValueList(idList);
        return R.ok(null, idList.size());
    }

    @Validated
    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_FLUSH_CACHE)
    @Operation(summary = "刷新字典缓存值")
    @DeleteMapping(value = "flushCacheSysDictValue")
    public R<Void> flushCacheSysDictValue(@NotEmpty @RequestParam String dictKey) {
        sysDictValueService.flushCacheSysDictValue(dictKey);
        return R.ok();
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_IMPORT)
    @Operation(summary = "导入字典键")
    @PostMapping(value = "importSysDictValue")
    public R<Void> importSysDictValue(
            @RequestPart("file[]") MultipartFile file,
            @NotEmpty(message = "字典键必须填写") @RequestParam String dictKey,
            @NotNull(message = "字典键ID不能为空") @RequestParam Long keyId) {
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.') + 1))
                .orElseThrow(() -> new OssException("oss.file.required"));
        if (!ExcelSuffix.includeBySuffix(suffix)) {
            throw new ServiceException("import.suffix.invalid", suffix);
        }
        try {
            UExcel.read(file.getInputStream(), SysDictValueImportEntity.class, ExcelSuffix.getInstanceBySuffix(suffix).getExcelTypeEnum(),
                    new DictValueImportListener<>(sysDictValueService, dictKey, keyId));
        } catch (IOException e) {
            e.printStackTrace();
            R<Void> failed = R.failed();
            failed.setMsg(UMessage.message("upload.failed"));
            return failed;
        }
        return R.ok();
    }

    @Validated
    @Operation(summary = "查询自增排序号")
    @GetMapping(value = "findMaxOrderNumByKeyId")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DICT_VALUE_EDIT)
    })
    public R<Integer> findMaxOrderNumByKeyId(@NotNull(message = "{not.null}") @RequestParam("keyId") Long keyId) {
        Integer maxOrderNum = Convert.toInt(sysDictValueService.findMaxOrderNumByKeyId(keyId), 0);
        maxOrderNum = (maxOrderNum / 10) * 10;
        return R.ok(maxOrderNum);
    }
}
