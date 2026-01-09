package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindDeptRolesByDeptIdEntity;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.entity.FindTreeDeptSelectEntity;
import com.freesia.exception.DeptException;
import com.freesia.exception.UserException;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysDeptService;
import com.freesia.util.UCopy;
import com.freesia.crypt.util.UCrypt;
import com.freesia.satoken.util.USecurity;
import com.freesia.vo.DeptAssignRoleVo;
import com.freesia.vo.R;
import com.freesia.vo.SaveDeptVo;
import com.freesia.vo.SysDeptVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 部门信息表 控制器
 * @date 2023-09-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysDeptController")
@Tag(name = "SysDeptController", description = "部门信息表 控制器")
public class SysDeptController extends BaseController {
    private final SysDeptService sysDeptService;

    @Operation(summary = "获取部门列表分页")
    @GetMapping("findPageSysDeptList")
    public TableResult<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptVo sysDeptVo, PageQuery pageQuery) {
        SysDeptDto sysDeptDto = new SysDeptDto();
        UCopy.fullCopy(sysDeptVo, sysDeptDto);
        return sysDeptService.findPageSysDeptList(sysDeptDto, pageQuery);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_INDEX)
    @Operation(summary = "获取部门下拉树")
    @GetMapping("findDeptTreeList")
    public R<List<FindPageSysDeptListEntity>> findDeptTreeList(SysDeptVo sysDeptVo) {
        SysDeptDto sysDeptDto = UCopy.copyVo2Dto(sysDeptVo, SysDeptDto.class);;
        sysDeptDto.setTenantId(USecurity.getTenantId());
        List<FindPageSysDeptListEntity> deptTreeList = sysDeptService.findDeptTreeList(sysDeptDto);
        return R.ok(deptTreeList);
    }

    @Operation(summary = "获取当前用户的部门信息")
    @GetMapping("findDeptById")
    public R<SysDeptDto> findDeptById() {
        LoginUserModel loginUser = USecurity.getLoginUser();
        Long deptId = Optional.ofNullable(loginUser).map(LoginUserModel::getDeptId)
                .orElseThrow(() -> new DeptException("dept.id.required", new Object[] {}));
        SysDeptDto sysDeptDto = new SysDeptDto();
        sysDeptDto.setId(deptId);
        sysDeptDto = sysDeptService.findOne(sysDeptDto);
        return R.ok(sysDeptDto);
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_EDIT)
    })
    @Operation(summary = "保存部门信息")
    @PostMapping("saveDept")
    public R<SysDeptDto> saveDept(@RequestBody String encrypt) {
        SaveDeptVo saveDeptVo = UCrypt.aesDecryptJSON(encrypt, SaveDeptVo.class);
        SysDeptDto sysDeptDto = sysDeptService.saveDept(UCopy.copyVo2Dto(saveDeptVo, SysDeptDto.class));
        return R.ok(sysDeptDto);
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_DELETE)
    @Operation(summary = "删除部门信息")
    @DeleteMapping("deleteDept")
    public R<SysDeptDto> deleteDept(@RequestParam Long deptId) {
        SysDeptDto sysDeptDto = sysDeptService.deleteDept(deptId);
        return R.ok(sysDeptDto);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_INDEX)
    @Operation(summary = "查询部门树下拉框集合")
    @GetMapping(value = "findTreeDeptSelect")
    public R<List<FindTreeDeptSelectEntity>> findTreeDeptSelect() {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseThrow(() -> new UserException("user.info.null", new Object[] {}));
        List<FindTreeDeptSelectEntity> menuList = sysDeptService.findTreeDeptSelect(loginUser);
        return R.ok(menuList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_INDEX)
    @Operation(summary = "查询部门树下拉框集合（分配部门）")
    @GetMapping(value = "findTreeAssignDeptSelect")
    public R<List<FindTreeDeptSelectEntity>> findTreeAssignDeptSelect() {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseThrow(() -> new UserException("user.info.null", new Object[] {}));
        List<FindTreeDeptSelectEntity> menuList = sysDeptService.findTreeAssignDeptSelect(loginUser);
        return R.ok(menuList);
    }

    @Validated
    @Operation(summary = "查询自增排序号")
    @GetMapping(value = "findIncrementOrderNum")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_ADD),
            @SaCheckPermission(value = MenuPermission.SYSTEM_DEPT_EDIT)
    })
    public R<Long> findIncrementOrderNum(@RequestParam("parentId") @NotNull(message = "{not.null}") String parentId) {
        SysDeptDto sysDeptDto = new SysDeptDto();
        sysDeptDto.setParentId(Long.valueOf(parentId));
        Long incrementOrderNum = sysDeptService.findIncrementOrderNum(sysDeptDto);
        return R.ok(incrementOrderNum);
    }

    @Operation(summary = "给部门分配角色")
    @PostMapping("assignRole")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_DEPT_ASSIGN_ROLE})
    public R<Void> assignRole(@RequestBody DeptAssignRoleVo assignRoleVo) {
        Long deptId = assignRoleVo.getDeptId();
        Set<Long> afterRoleIdSet = assignRoleVo.getAfterRoleIdSet();
        sysDeptService.assignRole(deptId, afterRoleIdSet);
        return R.ok();
    }

    @Operation(summary = "根据部门ID查询【分配角色】加载数据")
    @GetMapping("findDeptRolesByDeptId")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_DEPT_ASSIGN_ROLE})
    public R<FindDeptRolesByDeptIdEntity> findDeptRolesByDeptId(@RequestParam Long deptId) {
        FindDeptRolesByDeptIdEntity findDeptRolesByDeptIdEntity = sysDeptService.findDeptRolesByDeptId(deptId);
        return R.ok(findDeptRolesByDeptIdEntity);
    }
}
