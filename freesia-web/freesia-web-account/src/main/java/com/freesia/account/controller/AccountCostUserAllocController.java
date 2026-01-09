package com.freesia.account.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.account.dto.FindListSysUserByIdDto;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysUserDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.vo.AccountCostUserAllocVo;
import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.service.AccountCostUserAllocService;
import com.freesia.controller.BaseController;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 控制器
 * @date 2025-10-03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountCostUserAllocController")
@Tag(name = "AccountCostUserAllocController", description = "费用分摊表 控制器")
public class AccountCostUserAllocController extends BaseController {
    private final AccountCostUserAllocService accountCostUserAllocService;

    /**
     * 保存费用分摊表信息
     *
     * @param accountCostUserAllocVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存费用分摊表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountCostUserAllocVo accountCostUserAllocVo) {
        AccountCostUserAllocDto accountCostUserAllocDto = UCopy.copyVo2Dto(accountCostUserAllocVo, AccountCostUserAllocDto.class);
        accountCostUserAllocService.saveUpdate(accountCostUserAllocDto);
        return R.ok();
    }

    /**
     * 批量保存费用分摊表信息
     *
     * @param accountCostUserAllocVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存费用分摊表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountCostUserAllocVo> accountCostUserAllocVoList) {
        List<AccountCostUserAllocDto> accountCostUserAllocDtoList = UCopy.fullCopyList(accountCostUserAllocVoList, AccountCostUserAllocDto.class);
        accountCostUserAllocService.saveUpdateBatch(accountCostUserAllocDtoList);
        return R.ok();
    }

    /**
     * 查询费用分摊表分页信息
     *
     * @param accountCostUserAllocVo 查询条件
     * @param pageQuery              分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询费用分摊表分页信息")
    @GetMapping(value = "findPageAccountCostUserAlloc")
    public TableResult<AccountCostUserAllocDto> findPageAccountCostUserAlloc(AccountCostUserAllocVo accountCostUserAllocVo, PageQuery pageQuery) {
        AccountCostUserAllocDto accountCostUserAllocDto = UCopy.copyVo2Dto(accountCostUserAllocVo, AccountCostUserAllocDto.class);
        return accountCostUserAllocService.findPage(accountCostUserAllocDto, pageQuery);
    }

    /**
     * 条件查询费用分摊表
     *
     * @param accountCostUserAllocVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询费用分摊表")
    @GetMapping(value = "findAccountCostUserAlloc")
    public R<AccountCostUserAllocDto> findAccountCostUserAlloc(AccountCostUserAllocVo accountCostUserAllocVo) {
        AccountCostUserAllocDto accountCostUserAllocDto = UCopy.copyVo2Dto(accountCostUserAllocVo, AccountCostUserAllocDto.class);
        AccountCostUserAllocDto tableResult = accountCostUserAllocService.findOne(accountCostUserAllocDto);
        return R.ok(tableResult);
    }

    /**
     * 删除费用分摊表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除费用分摊表")
    @PostMapping(value = "deleteAccountCostUserAlloc")
    public R<Void> deleteAccountCostUserAlloc(@RequestBody List<Long> idList) {
        accountCostUserAllocService.deleteBatch(idList);
        return R.ok();
    }

    @Operation(summary = "新增费用分摊-根据分摊用户ID查询用户信息")
    @GetMapping("findListSysUserById")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_INDEX})
    public R<List<FindListSysUserByIdDto>> findListSysUserById(@RequestParam(value = "idList") List<Long> idList) {
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setIdList(idList);
        List<FindListSysUserByIdDto> list = accountCostUserAllocService.findListSysUserById(idList);
        return R.ok(list);
    }

    @Operation(summary = "修改费用分摊-根据记账ID查询分摊信息")
    @GetMapping("findListAllocByCostId")
    public R<List<FindListSysUserByIdDto>> findListAllocByCostId(@RequestParam(value = "costId") Long costId) {
        AccountCostUserAllocDto accountCostUserAllocDto = new AccountCostUserAllocDto();
        accountCostUserAllocDto.setCostId(costId);
        List<FindListSysUserByIdDto> list = accountCostUserAllocService.findListAllocByCostId(accountCostUserAllocDto);
        return R.ok(list);
    }
}
