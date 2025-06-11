package com.freesia.icon.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.MenuPermission;
import com.freesia.controller.BaseController;
import com.freesia.dto.SysOssDto;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.entity.CommonIconSaveUpdateEntity;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.icon.entity.FindPageCommonIconEntity;
import com.freesia.icon.service.CommonIconService;
import com.freesia.icon.vo.CommonIconVo;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.json.util.UJSON;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 通用图标表 控制器
 * @date 2025-03-21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/commonIconController")
@Tag(name = "CommonIconController", description = "通用图标表 控制器")
public class CommonIconController extends BaseController {
    private final CommonIconService commonIconService;
    private final SysOssService sysOssService;
    private final TransactionTemplate transactionTemplate;

    /**
     * 保存通用图标表信息
     *
     * @param fileList 上传的文件
     * @param request  待保存对象（JSON串）
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存通用图标表信息")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_ADD),
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_EDIT),
    })
    @PostMapping(value = "saveUpdate", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public R<CommonIconSaveUpdateEntity> saveUpdate(@RequestPart(value = "file[]", required = false) List<MultipartFile> fileList,
                                                    @RequestParam(value = "commonIconVo") String request) {
        CommonIconVo commonIconVo = UJSON.parseObject(request, CommonIconVo.class);
        // 如果是修改，但未上传新的图片，则允许为空
        if (UEmpty.isEmpty(fileList) && UEmpty.isNull(commonIconVo.getId())) {
            return R.failed(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("oss.file.required"));
        }
        if (UEmpty.isNotEmpty(fileList) && fileList.size() > 1) {
            return R.failed(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("oss.upload.only.one"));
        }
        List<SysOssDto> sysOssDtoList = new ArrayList<>();
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        // 文件新增
        if (UEmpty.isNull(commonIconVo.getId())) {
            sysOssDtoList = sysOssService.upload(fileList, "icon");
            if (UEmpty.isNotEmpty(sysOssDtoList)) {
                SysOssDto sysOssDto = sysOssDtoList.get(0);
                commonIconDto.setFileId(sysOssDto.getId());
            } else {
                return R.failed(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("file.upload.failed"));
            }
        } else {
            // 如果编辑文件
            if (UEmpty.isNotEmpty(fileList)) {
                sysOssDtoList = sysOssService.upload(fileList, "icon");
                if (UEmpty.isNotEmpty(sysOssDtoList)) {
                    SysOssDto sysOssDto = sysOssDtoList.get(0);
                    commonIconDto.setFileId(sysOssDto.getId());
                } else {
                    return R.failed(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("file.upload.failed"));
                }
            }
        }
        // 保存
        commonIconDto = commonIconService.saveUpdate(commonIconDto);
        CommonIconSaveUpdateEntity commonIconSaveUpdateEntity = new CommonIconSaveUpdateEntity(sysOssDtoList, commonIconDto);
        return R.ok(commonIconSaveUpdateEntity);
    }

    /**
     * 批量保存通用图标表信息
     *
     * @param fileList 上传的文件
     * @param request  待保存对象（JSON串）
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "批量保存通用图标表信息")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_ADD),
            @SaCheckPermission(value = MenuPermission.COMMON_ICON_EDIT),
    })
    @PostMapping(value = "saveUpdateBatch", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public R<Void> saveUpdateBatch(@NotNull @RequestPart(value = "file[]") List<MultipartFile> fileList,
                                   @RequestParam(value = "commonIconVo") String request) {
        CommonIconVo commonIconVo = UJSON.parseObject(request, CommonIconVo.class);
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        return transactionTemplate.execute(status -> {
            List<SysOssDto> sysOssDtoList = sysOssService.upload(fileList, "icon");
            if (UEmpty.isNotEmpty(sysOssDtoList)) {
                List<CommonIconDto> toSaveCommonIconDtoList = buildCommonIconDtoList(sysOssDtoList, commonIconDto);
                commonIconService.saveUpdateBatch(toSaveCommonIconDtoList);
            } else {
                return R.failed();
            }
            return R.ok();
        });
    }

    /**
     * 查询通用图标表分页信息
     *
     * @param commonIconVo 查询条件
     * @param pageQuery    分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询通用图标表分页信息")
    @GetMapping(value = "findPageCommonIcon")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_INDEX})
    public TableResult<FindPageCommonIconEntity> findPageCommonIcon(CommonIconVo commonIconVo, PageQuery pageQuery) {
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        return commonIconService.findPageCommonIcon(commonIconDto, pageQuery);
    }

    /**
     * 条件查询通用图标表
     *
     * @param commonIconVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询通用图标表")
    @GetMapping(value = "findCommonIcon")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_INDEX})
    public R<FindCommonIconEntity> findCommonIcon(CommonIconVo commonIconVo) {
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        FindCommonIconEntity tableResult = commonIconService.findCommonIcon(commonIconDto);
        return R.ok(tableResult);
    }

    /**
     * 条件查询通用图标表
     *
     * @param commonIconVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询通用图标表")
    @PostMapping(value = "findListCommonIcon")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_INDEX})
    public R<List<FindCommonIconEntity>> findListCommonIcon(@RequestBody CommonIconVo commonIconVo) {
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        commonIconDto.setIdList(commonIconVo.getIdList());
        List<FindCommonIconEntity> list = commonIconService.findListCommonIcon(commonIconDto);
        return R.ok(list);
    }

    /**
     * 删除通用图标表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除通用图标表")
    @PostMapping(value = "deleteCommonIcon")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_DELETE})
    public R<Void> deleteCommonIcon(@RequestBody List<Long> idList) {
        commonIconService.deleteCommonIcon(idList);
        return R.ok();
    }

    /**
     * 查询通用图标选择器
     *
     * @param commonIconVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "查询通用图标选择器")
    @GetMapping(value = "findCommonIconPicker")
    @SaCheckPermission(value = {MenuPermission.COMMON_ICON_INDEX})
    public R<Map<String, List<FindCommonIconEntity>>> findCommonIconPicker(CommonIconVo commonIconVo) {
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        Map<String, List<FindCommonIconEntity>> resultMap = commonIconService.findCommonIconPicker(commonIconDto);
        return R.ok(resultMap);
    }

    private List<CommonIconDto> buildCommonIconDtoList(List<SysOssDto> sysOssDtoList, CommonIconDto dto) {
        List<CommonIconDto> commonIconDtoList = new ArrayList<>();
        for (SysOssDto sysOssDto : sysOssDtoList) {
            CommonIconDto commonIconDto = buildCommonIconDto(dto, sysOssDto);
            commonIconDtoList.add(commonIconDto);
        }
        return commonIconDtoList;
    }

    private static CommonIconDto buildCommonIconDto(CommonIconDto dto, SysOssDto sysOssDto) {
        CommonIconDto commonIconDto = new CommonIconDto();
        String originalName = sysOssDto.getOriginalName();
        int idx = originalName.lastIndexOf("/");
        int startIndex = idx > 0 ? idx + 1 : 0;
        String name = originalName.substring(startIndex, originalName.lastIndexOf("."));
        commonIconDto.setName(name);
        commonIconDto.setFileId(sysOssDto.getId());
        commonIconDto.setIconPartition(dto.getIconPartition());
        commonIconDto.setRemark(name);
        return commonIconDto;
    }
}
