package com.freesia.account.controller;

import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.FindBudgetCapacityDto;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.vo.AccountBudgetVo;
import com.freesia.account.vo.FindBudgetCapacityVo;
import com.freesia.controller.BaseController;
import com.freesia.entity.EchartCapacityOptionEntity;
import com.freesia.exception.UserException;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.tenant.exception.TenantException;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 控制器
 * @date 2025-03-04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountBudgetController")
@Tag(name = "AccountBudgetController", description = "开销-预算表 控制器")
public class AccountBudgetController extends BaseController {
    private final AccountBudgetService accountBudgetService;

    /**
     * 保存开销-预算表信息
     *
     * @param accountBudgetVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存开销-预算表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountBudgetVo accountBudgetVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        AccountBudgetDto accountBudgetDto = UCopy.copyVo2Dto(accountBudgetVo, AccountBudgetDto.class);
        accountBudgetDto.setUserId(userId);
        accountBudgetService.saveUpdate(accountBudgetDto);
        return R.ok();
    }

    /**
     * 批量保存开销-预算表信息
     * <p>
     * accountBudgetVoList    待保存对象
     *
     * @return 形式返回
     */
    @Operation(summary = "保存开销-预算表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountBudgetVo> accountBudgetVoList) {
        List<AccountBudgetDto> accountBudgetDtoList = UCopy.fullCopyList(accountBudgetVoList, AccountBudgetDto.class);
        accountBudgetService.saveUpdateBatch(accountBudgetDtoList);
        return R.ok();
    }

    /**
     * 查询开销-预算表分页信息
     *
     * @param accountBudgetVo 查询条件
     * @param pageQuery       分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询开销-预算表分页信息")
    @GetMapping(value = "findPageAccountBudget")
    public TableResult<AccountBudgetDto> findPageAccountBudget(AccountBudgetVo accountBudgetVo, PageQuery pageQuery) {
        AccountBudgetDto accountBudgetDto = UCopy.copyVo2Dto(accountBudgetVo, AccountBudgetDto.class);
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        accountBudgetDto.setUserId(userId);
        return accountBudgetService.findPage(accountBudgetDto, pageQuery);
    }

    /**
     * 条件查询开销-预算表
     *
     * @param accountBudgetVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询开销-预算表")
    @GetMapping(value = "findAccountBudget")
    public R<AccountBudgetDto> findAccountBudget(AccountBudgetVo accountBudgetVo) {
        AccountBudgetDto accountBudgetDto = UCopy.copyVo2Dto(accountBudgetVo, AccountBudgetDto.class);
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        accountBudgetDto.setUserId(userId);
        AccountBudgetDto tableResult = accountBudgetService.findOne(accountBudgetDto);
        return R.ok(tableResult);
    }

    /**
     * 删除开销-预算表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除开销-预算表")
    @PostMapping(value = "deleteAccountBudget")
    public R<Void> deleteAccountBudget(@RequestBody List<Long> idList) {
        accountBudgetService.deleteBatch(idList);
        return R.ok();
    }

    @Operation(summary = "容量图-根据预算日期类型查询")
    @GetMapping(value = "findBudgetCapacity")
    public R<List<EchartCapacityOptionEntity>> findBudgetCapacity(FindBudgetCapacityVo findBudgetCapacityVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!findBudgetCapacityVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            findBudgetCapacityVo.setTenantId(tenantId);
        } else {
            findBudgetCapacityVo.setTenantId(null);
        }
        FindBudgetCapacityDto findBudgetCapacityDto = new FindBudgetCapacityDto();
        UCopy.fullCopy(findBudgetCapacityVo, findBudgetCapacityDto);
        findBudgetCapacityDto.setUserId(userId);
        List<EchartCapacityOptionEntity> echartCapacityOptionEntityList = accountBudgetService.findBudgetCapacity(findBudgetCapacityDto);
        return R.ok(echartCapacityOptionEntityList);
    }
}
