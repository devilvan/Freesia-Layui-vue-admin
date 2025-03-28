package com.freesia.icon.controller;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson2.JSONObject;
import com.freesia.controller.BaseController;
import com.freesia.dto.SysOssDto;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.entity.CommonIconSaveUpdateEntity;
import com.freesia.icon.entity.FindPageCommonIconEntity;
import com.freesia.icon.service.CommonIconService;
import com.freesia.icon.vo.CommonIconVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    /**
     * 保存通用图标表信息
     *
     * @param fileList 上传的文件
     * @param request  待保存对象（JSON串）
     * @return 形式返回
     */
    @Operation(summary = "保存通用图标表信息")
    @PostMapping(value = "saveUpdate", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public R<CommonIconSaveUpdateEntity> saveUpdate(@NotNull @RequestPart("file[]") List<MultipartFile> fileList,
                                                    @RequestPart("commonIconVo") String request) {
        if (fileList.size() > 1) {
            return R.failed(HttpStatus.HTTP_BAD_REQUEST, "file.upload.only.one");
        }
        CommonIconVo commonIconVo = JSONObject.parseObject(request, CommonIconVo.class);
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        List<SysOssDto> sysOssDtoList = sysOssService.upload(fileList);
        if (UEmpty.isNotEmpty(sysOssDtoList)) {
            SysOssDto sysOssDto = sysOssDtoList.get(0);
            commonIconDto.setFileId(sysOssDto.getId());
        } else {
            return R.failed(HttpStatus.HTTP_BAD_REQUEST, "file.upload.failed");
        }
        commonIconDto = commonIconService.saveUpdate(commonIconDto);
        CommonIconSaveUpdateEntity commonIconSaveUpdateEntity = new CommonIconSaveUpdateEntity(sysOssDtoList, commonIconDto);
        return R.ok(commonIconSaveUpdateEntity);
    }

    /**
     * 批量保存通用图标表信息
     *
     * @param commonIconVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存通用图标表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<CommonIconVo> commonIconVoList) {
        List<CommonIconDto> commonIconDtoList = UCopy.fullCopyList(commonIconVoList, CommonIconDto.class);
        commonIconService.saveUpdateBatch(commonIconDtoList);
        return R.ok();
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
    public R<CommonIconDto> findCommonIcon(CommonIconVo commonIconVo) {
        CommonIconDto commonIconDto = UCopy.copyVo2Dto(commonIconVo, CommonIconDto.class);
        CommonIconDto tableResult = commonIconService.findCommonIcon(commonIconDto);
        return R.ok(tableResult);
    }

    /**
     * 删除通用图标表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除通用图标表")
    @PostMapping(value = "deleteCommonIcon")
    public R<Void> deleteCommonIcon(@RequestBody List<Long> idList) {
        commonIconService.deleteCommonIcon(idList);
        return R.ok();
    }
}
