package com.freesia.controller;

import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.model.LoginUserModel;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysDeptService;
import com.freesia.util.UCopy;
import com.freesia.util.USecurity;
import com.freesia.vo.R;
import com.freesia.vo.SysDeptVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 部门信息表 控制器
 * @date 2023-09-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysDeptController")
@Tag(name = "SysDeptController", description = "部门信息表 控制器")
public class SysDeptController {
    private final SysDeptService sysDeptService;

    @Operation(summary = "获取部门列表分页")
    @GetMapping("findPageSysDeptList")
    public TableResult<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptVo sysDeptVo, PageQuery pageQuery) {
        SysDeptDto sysDeptDto = new SysDeptDto();
        UCopy.fullCopy(sysDeptVo, sysDeptDto);
        return sysDeptService.findPageSysDeptList(sysDeptDto, pageQuery);
    }

    @Operation(summary = "获取部门下拉树")
    @GetMapping("findDeptTreeList")
    public R<List<FindPageSysDeptListEntity>> findDeptTreeList(SysDeptVo sysDeptVo) {
        SysDeptDto sysDeptDto = new SysDeptDto();
        UCopy.fullCopy(sysDeptVo, sysDeptDto);
        sysDeptDto.setTenantId(USecurity.getTenantId());
        List<FindPageSysDeptListEntity> deptTreeList = sysDeptService.findDeptTreeList(sysDeptDto);
        return R.ok(deptTreeList);
    }

    @SuppressWarnings("ConstantConditions")
    @Operation(summary = "获取部门下拉树")
    @GetMapping("findDeptById")
    public R<SysDeptDto> findDeptById() {
        LoginUserModel loginUser = USecurity.getLoginUser();
        SysDeptDto sysDeptDto = sysDeptService.findDeptById(loginUser.getDeptId());
        return R.ok(sysDeptDto);
    }
}
