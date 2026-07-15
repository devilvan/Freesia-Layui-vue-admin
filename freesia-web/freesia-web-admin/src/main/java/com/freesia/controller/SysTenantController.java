package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.constant.MenuPermission;
import com.freesia.converter.SysTenantConverter;
import com.freesia.dto.AssignTenantDto;
import com.freesia.dto.SysTenantDto;
import com.freesia.entity.FindSysTenantEntity;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysTenantService;
import com.freesia.util.UCollection;
import com.freesia.util.UEmpty;
import com.freesia.vo.AssignTenantVo;
import com.freesia.vo.R;
import com.freesia.vo.SysTenantVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 控制器
 * @date 2024-01-31
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysTenantController")
@Tag(name = "SysTenantController", description = "租户信息表 控制器")
public class SysTenantController extends BaseController {
    private final SysTenantService sysTenantService;
    private final SysTenantConverter sysTenantConverter;

    /**
     * 保存租户信息表信息
     *
     * @return 形式返回
     */
    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_EDIT),
    })
    @Operation(summary = "保存租户信息表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysTenantVo sysTenantVo) {
        SysTenantDto sysTenantDto = sysTenantConverter.convertVo2Dto(sysTenantVo);
        sysTenantDto.setUserId(USecurity.getUserId());
        sysTenantService.saveUpdate(sysTenantDto);
        return R.ok();
    }

    /**
     * 批量保存租户信息表信息
     *
     * @return 形式返回
     */
    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_EDIT),
    })
    @Operation(summary = "保存租户信息表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysTenantVo> sysTenantVoList) {
        List<SysTenantDto> sysTenantDtoList = sysTenantConverter.convertBatchVo2Dto(sysTenantVoList);
        sysTenantService.saveUpdateBatch(sysTenantDtoList);
        return R.ok();
    }

    /**
     * 查询租户信息表分页信息
     *
     * @param sysTenantVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_INDEX)
    @Operation(summary = "查询租户信息表分页信息")
    @GetMapping(value = "findPageSysTenant")
    public TableResult<SysTenantDto> findPageSysTenant(SysTenantVo sysTenantVo, PageQuery pageQuery) {
        SysTenantDto sysTenantDto = sysTenantConverter.convertVo2Dto(sysTenantVo);
        sysTenantDto.setUserId(USecurity.getUserId());
        return sysTenantService.findPage(sysTenantDto, pageQuery);
    }

    /**
     * 条件查询租户信息表
     *
     * @param sysTenantVo 查询条件
     * @return 形式返回
     */
    @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_INDEX)
    @Operation(summary = "条件查询租户信息表")
    @GetMapping(value = "findSysTenant")
    public R<FindSysTenantEntity> findSysTenant(SysTenantVo sysTenantVo) {
        SysTenantDto sysTenantDto = sysTenantConverter.convertVo2Dto(sysTenantVo);
        return R.ok(sysTenantService.findSysTenant(sysTenantDto));
    }

    /**
     * 删除租户信息表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_DELETE)
    @Operation(summary = "删除租户信息表")
    @PostMapping(value = "deleteSysTenant")
    public R<Void> deleteSysTenant(@RequestBody List<Long> idList) {
        sysTenantService.deleteSysTenant(idList);
        return R.ok();
    }

    /**
     * 分配租户
     *
     * @param assignTenantVo 入参
     * @return 形式返回
     */
    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_TENANT_ASSIGN_USER)
    @Operation(summary = "分配租户")
    @PostMapping(value = "assignTenant")
    public R<Void> assignTenant(@Validated @RequestBody AssignTenantVo assignTenantVo) {
        AssignTenantDto assignTenantDto = sysTenantConverter.convertAssignTenantVo2Dto(assignTenantVo);
        Long tenantId = assignTenantDto.getTenantId();
        List<Long> userIdList = assignTenantDto.getUserIdList();
        if (UEmpty.isNotEmpty(userIdList)) {
            sysTenantService.assignTenant2User(tenantId, userIdList);
        }
        return R.ok();
    }

    /**
     * 取消将租户分配给用户
     *
     * @param assignTenantVo 入参
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "取消将租户分配给用户")
    @PostMapping(value = "cancelTenantAssignUser")
    public R<Void> cancelTenantAssignUser(@Validated @RequestBody AssignTenantVo assignTenantVo) {
        Long tenantId = Long.parseLong(assignTenantVo.getTenantId());
        List<String> userIdList = assignTenantVo.getUserIdList();
        List<Long> userIdLongList = UCollection.optimizeInitialCapacityArrayList(userIdList.size());
        userIdList.forEach(userId -> userIdLongList.add(Long.parseLong(userId)));
        sysTenantService.cancelAssignUser(tenantId, userIdLongList);
        return R.ok();
    }

    /**
     * 租户分配给其他实体（如；用户）后，刷新前端全局变量中的租户信息
     *
     * @return 形式返回
     */
    @Operation(summary = "租户分配给其他实体（如；用户）后，刷新前端全局变量中的租户信息")
    @PutMapping(value = "reloadSysTenant")
    public R<List<SysTenantDto>> reloadSysTenant() {
        Long userId = USecurity.getUserId();
        List<SysTenantDto> sysTenantDtoList = sysTenantService.findListSysTenantByUserId(userId);
        return R.ok(sysTenantDtoList);
    }

    /**
     * 数据复制
     *
     * @param assignTenantVo VO
     * @return DTO
     */
    private AssignTenantDto convertAssignTenantVo2Dto(AssignTenantVo assignTenantVo) {
        AssignTenantDto assignTenantDto = new AssignTenantDto();
        assignTenantDto.setTenantId(Long.parseLong(assignTenantVo.getTenantId()));
        List<String> userIdList = assignTenantVo.getUserIdList();
        List<Long> userIdLongList = UCollection.optimizeInitialCapacityArrayList(userIdList.size());
        for (String userId : userIdList) {
            userIdLongList.add(Long.parseLong(userId));
        }
        assignTenantDto.setUserIdList(userIdLongList);
        return assignTenantDto;
    }
}
