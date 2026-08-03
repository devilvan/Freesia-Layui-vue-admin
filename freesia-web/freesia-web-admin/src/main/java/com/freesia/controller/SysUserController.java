package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.constant.MenuPermission;
import com.freesia.converter.SysTenantConverter;
import com.freesia.converter.SysUserConverter;
import com.freesia.crypt.annotation.Encrypt;
import com.freesia.crypt.util.UCrypt;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.*;
import com.freesia.excel.UserImportListener;
import com.freesia.excel.constant.ExcelSuffix;
import com.freesia.excel.util.UExcel;
import com.freesia.exception.UserException;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.oss.exception.OssException;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysUserService;
import com.freesia.util.UMessage;
import com.freesia.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 用户管理 控制器
 * @date 2023-08-30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysUserController")
@Tag(name = "SysUserController", description = "用户信息表 控制器")
public class SysUserController extends BaseController {
    private final SysUserService sysUserService;
    private final SysUserConverter sysUserConverter;
    private final SysTenantConverter sysTenantConverter;

    @Operation(summary = "获取用户列表分页")
    @GetMapping("findPageSysUserList")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_INDEX})
    public TableResult<FindPageSysUserListEntity> findPageSysUserList(SysUserVo sysUserVo, PageQuery pageQuery) {
        SysUserDto sysUserDto = sysUserConverter.convertVo2Dto(sysUserVo);
        return sysUserService.findPageSysUserList(sysUserDto, pageQuery);
    }


    @Operation(summary = "获取部门下的用户")
    @GetMapping("findPageSysUserByDept")
    public TableResult<FindPageSysUserByDeptEntity> findPageSysUserByDept(SysUserVo sysUserVo, PageQuery pageQuery) {
        sysUserVo.setTenantId(USecurity.getTenantId());
        SysUserDto sysUserDto = sysUserConverter.convertVo2Dto(sysUserVo);
        return sysUserService.findPageSysUserByDept(sysUserDto, pageQuery);
    }

    @Encrypt
    @Operation(summary = "查询用户信息")
    @GetMapping("findCurrentUserProfile")
    public R<FindCurrentUserProfileEntity> findCurrentUserProfile() {
        SysUserDto sysUserDto = sysUserService.findCurrentUserProfile(USecurity.getUserId());
        FindCurrentUserProfileEntity findCurrentUserProfileEntity = sysUserConverter.convertDto2FindCurrentUserProfileEntity(sysUserDto);
        return R.ok(findCurrentUserProfileEntity);
    }

    @Idempotent
    @Operation(summary = "修改用户信息")
    @PutMapping("saveUserInfo")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_USER_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_USER_EDIT)
    })
    public R<Void> saveUserInfo(@RequestBody String request) {
        SysUserVo sysUserVo = UCrypt.aesDecryptJSON(request, SysUserVo.class);
        SysUserDto sysUserDto = sysUserConverter.convertVo2Dto(sysUserVo);
        sysUserService.saveUserInfo(sysUserDto);
        return R.ok();
    }

    @Operation(summary = "根据用户ID查询【分配角色】加载数据")
    @GetMapping("findUserRolesByUserId")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_ASSIGN_ROLE})
    public R<FindUserRolesByUserIdEntity> findUserRolesByUserId(@RequestParam Long userId) {
        FindUserRolesByUserIdEntity findUserRolesByUserIdEntity = sysUserService.findUserRolesByUserId(userId);
        return R.ok(findUserRolesByUserIdEntity);
    }

    @Idempotent
    @Operation(summary = "给用户分配角色")
    @PostMapping("assignRole")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_ASSIGN_ROLE})
    public R<Void> assignRole(@RequestBody AssignRoleVo assignRoleVo) {
        Long userId = assignRoleVo.getUserId();
        Set<Long> afterRoleIdSet = assignRoleVo.getAfterRoleIdSet();
        sysUserService.assignRole(userId, afterRoleIdSet);
        return R.ok();
    }

    @Operation(summary = "根据租户ID查询已分配该租户的用户")
    @GetMapping("findPageUserByTenantId")
    public TableResult<SysUserDto> findPageUserByTenantId(SysTenantVo sysTenantVo, PageQuery pageQuery) {
        return sysUserService.findPageUserByTenantId(sysTenantVo.getId(), pageQuery);
    }

    @Operation(summary = "根据租户ID查询可分配该租户的用户")
    @GetMapping("findPageAllowAssignUserByTenantId")
    public TableResult<SysUserDto> findPageAllowAssignUserByTenantId(SysTenantVo sysTenantVo, PageQuery pageQuery) {
        SysTenantDto sysTenantDto = sysTenantConverter.convertVo2Dto(sysTenantVo);
        return sysUserService.findPageAllowAssignUserByTenantId(sysTenantDto, pageQuery);
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_USER_IMPORT_USER)
    @Operation(summary = "用户导入")
    @PostMapping(value = "userImport")
    public R<Void> userImport(@RequestPart("file[]") MultipartFile file, @RequestParam String avatar) {
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.') + 1))
                .orElseThrow(() -> new OssException("oss.file.required"));
        if (!ExcelSuffix.includeBySuffix(suffix)) {
            throw new UserException("import.suffix.invalid", new Object[]{suffix});
        }
        try {
            UExcel.read(file.getInputStream(), SysUserImportEntity.class, ExcelSuffix.getInstanceBySuffix(suffix).getExcelTypeEnum(),
                    new UserImportListener<>(sysUserService, avatar), 0, null);
        } catch (IOException e) {
            R<Void> failed = R.failed();
            failed.setMsg(UMessage.message("file.upload.failed"));
            return failed;
        }
        return R.ok();
    }

    @Idempotent
    @SaCheckLogin
    @Operation(summary = "用户头像更新")
    @PostMapping(value = "avatarUpdate")
    public R<Void> avatarUpdate(@RequestParam String avatar) {
        sysUserService.avatarUpdate(avatar);
        return R.ok();
    }

    @Encrypt
    @Operation(summary = "根据用户ID查询该用户的修改信息")
    @GetMapping(value = "findEditUserById")
    @SaCheckPermission(value = MenuPermission.SYSTEM_USER_EDIT)
    public R<SysUserDto> findEditUserById(@NotEmpty(message = "{not.null}") @RequestParam String id) {
        SysUserDto sysUserDto = sysUserService.findUserById(Long.valueOf(id));
        return R.ok(sysUserDto);
    }

    @Idempotent
    @Operation(summary = "删除用户")
    @PostMapping("deleteUser")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_DELETE})
    public R<List<SysUserDto>> deleteUser(@RequestBody List<Long> idList) {
        List<SysUserDto> sysUserDtoList = sysUserService.deleteUser(idList);
        return R.ok(sysUserDtoList);
    }

    @Operation(summary = "给用户分配部门")
    @PostMapping("assignDept")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_ASSIGN_DEPT})
    public R<Void> assignDept(@RequestBody AssignDeptVo assignDeptVo) {
        List<Long> userIdList = assignDeptVo.getUserIdList();
        Long deptId = assignDeptVo.getDeptId();
        sysUserService.assignDept(userIdList, deptId);
        return R.ok();
    }

    @Operation(summary = "获取用户列表分页（不过滤数据权限）")
    @GetMapping("findPageSysUserWithoutDataScope")
//    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_INDEX})
    public TableResult<FindPageSysUserListEntity> findPageSysUserWithoutDataScope(SysUserVo sysUserVo, PageQuery pageQuery) {
        SysUserDto sysUserDto = sysUserConverter.convertVo2Dto(sysUserVo);
        return sysUserService.findPageSysUserWithoutDataScope(sysUserDto, pageQuery);
    }
}
