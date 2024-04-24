package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSONObject;
import com.freesia.annotation.Encrypt;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.MenuPermission;
import com.freesia.constant.UserModule;
import com.freesia.constant.UserType;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.*;
import com.freesia.excel.constant.ExcelSuffix;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.util.UExcel;
import com.freesia.exception.OssException;
import com.freesia.exception.ServiceException;
import com.freesia.exception.UserException;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysUserService;
import com.freesia.util.*;
import com.freesia.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
public class SysUserController {
    private final SysUserService sysUserService;

    @Operation(summary = "获取用户列表分页")
    @GetMapping("findPageSysUserList")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_INDEX})
    public TableResult<FindPageSysUserListEntity> findPageSysUserList(SysUserVo sysUserVo, PageQuery pageQuery) {
        SysUserDto sysUserDto = new SysUserDto();
        UCopy.fullCopy(sysUserVo, sysUserDto);
        return sysUserService.findPageSysUserList(sysUserDto, pageQuery);
    }

    @Operation(summary = "获取部门下的用户")
    @GetMapping("findPageSysUserByDept")
    public TableResult<FindPageSysUserByDeptEntity> findPageSysUserByDept(SysUserVo sysUserVo, PageQuery pageQuery) {
        SysUserDto sysUserDto = new SysUserDto();
        UCopy.fullCopy(sysUserVo, sysUserDto);
        sysUserDto.setTenantId(USecurity.getTenantId());
        return sysUserService.findPageSysUserByDept(sysUserDto, pageQuery);
    }

    @Encrypt
    @Operation(summary = "查询用户信息")
    @GetMapping("findCurrentUserProfile")
    public R<FindCurrentUserProfileEntity> findCurrentUserProfile() {
        SysUserDto sysUserDto = sysUserService.findCurrentUserProfile(USecurity.getUserId());
        FindCurrentUserProfileEntity findCurrentUserProfileEntity = new FindCurrentUserProfileEntity();
        UCopy.fullCopy(sysUserDto, findCurrentUserProfileEntity);
        return R.ok(findCurrentUserProfileEntity);
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("saveUserInfo")
    public R<Void> saveUserInfo(@RequestBody String request) {
        SysUserVo sysUserVo = UCrypt.aesDecryptJSON(request, SysUserVo.class);
        SysUserDto sysUserDto = UCopy.copyVo2Dto(sysUserVo, SysUserDto.class);
        sysUserService.saveUserInfo(sysUserDto);
        return R.ok();
    }

    @Operation(summary = "根据用户ID查询【分配用户】加载数据")
    @GetMapping("findUserRolesByUserId")
    public R<FindUserRolesByUserIdEntity> findUserRolesByUserId(@RequestParam Long userId) {
        FindUserRolesByUserIdEntity findUserRolesByUserIdEntity = sysUserService.findUserRolesByUserId(userId);
        return R.ok(findUserRolesByUserIdEntity);
    }

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
        SysTenantDto sysTenantDto = UCopy.copyVo2Dto(sysTenantVo, SysTenantDto.class);
        return sysUserService.findPageAllowAssignUserByTenantId(sysTenantDto, pageQuery);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_USER_IMPORT_USER)
    @Operation(summary = "用户导入")
    @PostMapping(value = "userImport")
    public R<Void> userImport(@RequestPart("file[]") MultipartFile file, @RequestParam String avatar) {
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.') + 1))
                .orElseThrow(() -> new OssException("oss.file.required"));
        if (!ExcelSuffix.includeBySuffix(suffix)) {
            throw new UserException("user.import.suffix.invalid", suffix);
        }
        try {
            UExcel.read(file.getInputStream(), SysUserImportEntity.class, new BaseImportEntityListener<>() {
                @Override
                public void invoke(SysUserImportEntity sysUserImportEntity, AnalysisContext context) {
                    cachedDataList.add(sysUserImportEntity);
                    if (cachedDataList.size() >= BATCH_COUNT) {
                        transactionSaveSysUser();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    transactionSaveSysUser();
                }

                private void transactionSaveSysUser() {
                    List<SysUserDto> sysUserDtoList = UCollection.optimizeInitialCapacityArrayList(cachedDataList.size());
                    for (SysUserImportEntity sysUserImportEntity : cachedDataList) {
                        // 数据校验
                        errorMsg.addAll(USpringValidation.errorMsg(sysUserImportEntity));
                        SysUserDto sysUserDto = buildSysUserDto(sysUserImportEntity);
                        sysUserDtoList.add(sysUserDto);
                    }
                    if (UEmpty.isNotEmpty(errorMsg)) {
                        throw new ServiceException(UCollection.join(errorMsg, "\n"));
                    }
                    if (sysUserDtoList.size() > 0) {
                        // 过滤相同用户名的数据
                        sysUserDtoList = sysUserDtoList.stream()
                                .filter(UCopy.distinctByKey(SysUserDto::getUserName))
                                .collect(Collectors.toList());
                        // 查询是否有重复用户名
                        final List<String> distinctUserNameList = sysUserDtoList.stream().map(SysUserDto::getUserName).collect(Collectors.toList());
                        List<SysUserDto> distinctSysUserDtoList = sysUserService.findDistinctUserNameList(distinctUserNameList);
                        if (UEmpty.isNotEmpty(distinctSysUserDtoList)) {
                            final List<String> nonUniqueUserNameList = distinctSysUserDtoList.stream().map(SysUserDto::getUserName).collect(Collectors.toList());
                            final String nonUniqueUserNameJoin = StrUtil.join("\n", nonUniqueUserNameList);
                            throw new ServiceException(UserModule.SubModule.USER_IMPORT, "user.name.not.unique", nonUniqueUserNameJoin);
                        }
                        // 保存
                        sysUserService.saveUpdateBatch(sysUserDtoList);
                    }
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }

                private SysUserDto buildSysUserDto(SysUserImportEntity sysUserImportEntity) {
                    SysUserDto sysUserDto = new SysUserDto();
                    sysUserDto.setUserName(sysUserImportEntity.getUserName());
                    sysUserDto.setNickName(sysUserImportEntity.getNickName());
                    sysUserDto.setEmail(sysUserImportEntity.getEmail());
                    sysUserDto.setTelNo(sysUserImportEntity.getTelNo());
                    sysUserDto.setPassword(BCrypt.hashpw(sysUserImportEntity.getPassword(), BCrypt.gensalt()));
                    sysUserDto.setAccountStatus(FlagConstant.ENABLED);
                    sysUserDto.setRemark(sysUserImportEntity.getRemark());
                    sysUserDto.setAvatar(avatar);
                    final String gender = sysUserImportEntity.getGender();
                    if (UEmpty.isNotEmpty(gender)) {
                        sysUserDto.setGender(gender);
                    } else {
                        sysUserDto.setGender("U");
                    }
                    if (UEmpty.isNotEmpty(sysUserImportEntity.getUserType())) {
                        final UserType userType = UserType.getInstanceByKey(sysUserImportEntity.getUserType());
                        sysUserDto.setUserType(userType.getUserType());
                    } else {
                        sysUserDto.setUserType(UserType.SYS_USER.getUserType());
                    }
                    return sysUserDto;
                }
            }, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
            R<Void> failed = R.failed();
            failed.setMsg(UMessage.message("upload.failed"));
            return failed;
        }
        return R.ok();
    }

    //        @SaCheckPermission(value = MenuPermission.SYSTEM_USER_UPLOAD_AVATAR)
    @SaIgnore
    @Operation(summary = "用户头像上传")
    @PostMapping(value = "uploadAvatar")
    public R<Void> uploadAvatar(@RequestPart("file[]") MultipartFile file, @NotEmpty(message = "{not.null}") @RequestParam String id) {
        return R.ok();
    }

    @Encrypt
    @Operation(summary = "根据用户ID查询该用户的修改信息")
    @GetMapping(value = "findEditUserById")
    @SaCheckPermission(value = MenuPermission.SYSTEM_USER_EDIT)
    public R<SysUserDto> findEditUserById(@NotEmpty(message = "{not.null}") @RequestParam String id) {
        final SysUserDto sysUserDto = sysUserService.findUserById(Long.valueOf(id));
        return R.ok(sysUserDto);
    }

    @SaIgnore
    @Operation(summary = "新增用户")
    @PostMapping("addUser")
    public R<Void> addUser(@RequestBody String request) {
//        AddUserVo addUserVo = UCrypt.aesDecryptJSON(request, AddUserVo.class);
        AddUserVo addUserVo = JSONObject.parseObject(request, AddUserVo.class);
        List<String> errorMsg = USpringValidation.errorMsg(addUserVo);
        if (UEmpty.isNotEmpty(errorMsg)) {
            R<Void> r = R.failed();
            String join = String.join("\n", errorMsg);
            log.warn(join);
            r.setMsg(join);
            return r;
        }
        return R.ok();
    }
}
