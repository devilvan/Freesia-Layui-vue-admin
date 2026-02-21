package com.freesia.account.controller;

import com.freesia.account.dto.AccountBillingStrategyDto;
import com.freesia.account.service.AccountBillingStrategyService;
import com.freesia.account.vo.AccountBillingStrategyVo;
import com.freesia.controller.BaseController;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 控制器
 * @date 2026-02-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountBillingStrategyController")
@Tag(name = "AccountBillingStrategyController", description = "记账账单策略表 控制器")
public class AccountBillingStrategyController extends BaseController {
    private final AccountBillingStrategyService accountBillingStrategyService;

    /**
     * 保存记账账单策略表信息
     *
     * @param accountBillingStrategyVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账账单策略表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountBillingStrategyVo accountBillingStrategyVo) {
        AccountBillingStrategyDto accountBillingStrategyDto = UCopy.copyVo2Dto(accountBillingStrategyVo, AccountBillingStrategyDto.class);
        accountBillingStrategyDto.setUserId(USecurity.getUserId());
        accountBillingStrategyDto.setTenantId(USecurity.getTenantId());
        accountBillingStrategyService.saveUpdate(accountBillingStrategyDto);
        return R.ok();
    }

    /**
     * 批量保存记账账单策略表信息
     *
     * @param accountBillingStrategyVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账账单策略表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountBillingStrategyVo> accountBillingStrategyVoList) {
        List<AccountBillingStrategyDto> accountBillingStrategyDtoList = UCopy.fullCopyList(accountBillingStrategyVoList, AccountBillingStrategyDto.class);
        accountBillingStrategyService.saveUpdateBatch(accountBillingStrategyDtoList);
        return R.ok();
    }

    /**
     * 查询记账账单策略表分页信息
     *
     * @param accountBillingStrategyVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询记账账单策略表分页信息")
    @GetMapping(value = "findPageAccountBillingStrategy")
    public TableResult<AccountBillingStrategyDto> findPageAccountBillingStrategy(AccountBillingStrategyVo accountBillingStrategyVo, PageQuery pageQuery) {
        AccountBillingStrategyDto accountBillingStrategyDto = UCopy.copyVo2Dto(accountBillingStrategyVo, AccountBillingStrategyDto.class);
        accountBillingStrategyDto.setUserId(USecurity.getUserId());
        accountBillingStrategyDto.setTenantId(USecurity.getTenantId());
        return accountBillingStrategyService.findPage(accountBillingStrategyDto, pageQuery);
    }

    /**
     * 条件查询记账账单策略表
     *
     * @param accountBillingStrategyVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询记账账单策略表")
    @GetMapping(value = "findAccountBillingStrategy")
    public R<AccountBillingStrategyDto> findAccountBillingStrategy(AccountBillingStrategyVo accountBillingStrategyVo) {
        AccountBillingStrategyDto accountBillingStrategyDto = UCopy.copyVo2Dto(accountBillingStrategyVo, AccountBillingStrategyDto.class);
        accountBillingStrategyDto = accountBillingStrategyService.findOne(accountBillingStrategyDto);
        return R.ok(accountBillingStrategyDto);
    }

    /**
    * 条件查询记账账单策略表
    *
    * @param accountBillingStrategyVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询记账账单策略表")
    @GetMapping(value = "findListAccountBillingStrategy")
    public R<List<AccountBillingStrategyDto>> findListAccountBillingStrategy(AccountBillingStrategyVo accountBillingStrategyVo) {
        AccountBillingStrategyDto accountBillingStrategyDto = UCopy.copyVo2Dto(accountBillingStrategyVo, AccountBillingStrategyDto.class);
        List<AccountBillingStrategyDto> accountBillingStrategyDtoList = accountBillingStrategyService.findList(accountBillingStrategyDto);
        return R.ok(accountBillingStrategyDtoList);
    }

    /**
     * 删除记账账单策略表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除记账账单策略表")
    @PostMapping(value = "deleteAccountBillingStrategy")
    public R<Void> deleteAccountBillingStrategy(@RequestBody List<Long> idList) {
        accountBillingStrategyService.deleteBatch(idList);
        return R.ok();
    }
}
