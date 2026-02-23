package com.freesia.account.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.vo.AccountBillingVo;
import com.freesia.account.dto.AccountBillingDto;
import com.freesia.account.service.AccountBillingService;
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
 * @Description 记账账单表 控制器
 * @date 2026-02-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountBillingController")
@Tag(name = "AccountBillingController", description = "记账账单表 控制器")
public class AccountBillingController extends BaseController {
    private final AccountBillingService accountBillingService;

    /**
     * 保存记账账单表信息
     *
     * @param accountBillingVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账账单表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountBillingVo accountBillingVo) {
        AccountBillingDto accountBillingDto = UCopy.copyVo2Dto(accountBillingVo, AccountBillingDto.class);
        accountBillingService.saveUpdate(accountBillingDto);
        return R.ok();
    }

    /**
     * 批量保存记账账单表信息
     *
     * @param accountBillingVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账账单表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountBillingVo> accountBillingVoList) {
        List<AccountBillingDto> accountBillingDtoList = UCopy.fullCopyList(accountBillingVoList, AccountBillingDto.class);
        accountBillingService.saveUpdateBatch(accountBillingDtoList);
        return R.ok();
    }

    /**
     * 查询记账账单表分页信息
     *
     * @param accountBillingVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询记账账单表分页信息")
    @GetMapping(value = "findPageAccountBilling")
    public TableResult<AccountBillingDto> findPageAccountBilling(AccountBillingVo accountBillingVo, PageQuery pageQuery) {
        AccountBillingDto accountBillingDto = UCopy.copyVo2Dto(accountBillingVo, AccountBillingDto.class);
        return accountBillingService.findPage(accountBillingDto, pageQuery);
    }

    /**
     * 条件查询记账账单表
     *
     * @param accountBillingVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询记账账单表")
    @GetMapping(value = "findAccountBilling")
    public R<AccountBillingDto> findAccountBilling(AccountBillingVo accountBillingVo) {
        AccountBillingDto accountBillingDto = UCopy.copyVo2Dto(accountBillingVo, AccountBillingDto.class);
        accountBillingDto = accountBillingService.findOne(accountBillingDto);
        return R.ok(accountBillingDto);
    }

    /**
    * 条件查询记账账单表
    *
    * @param accountBillingVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询记账账单表")
    @GetMapping(value = "findListAccountBilling")
    public R<List<AccountBillingDto>> findListAccountBilling(AccountBillingVo accountBillingVo) {
        AccountBillingDto accountBillingDto = UCopy.copyVo2Dto(accountBillingVo, AccountBillingDto.class);
        List<AccountBillingDto> accountBillingDtoList = accountBillingService.findList(accountBillingDto);
        return R.ok(accountBillingDtoList);
    }

    /**
     * 删除记账账单表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除记账账单表")
    @PostMapping(value = "deleteAccountBilling")
    public R<Void> deleteAccountBilling(@RequestBody List<Long> idList) {
        accountBillingService.deleteBatch(idList);
        return R.ok();
    }

    /**
     * 生成账单任务
     *
     * @param accountBillingVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "生成账单任务")
    @GetMapping(value = "generateBillingTask")
    public R<Void> generateBillingTask(AccountBillingVo accountBillingVo) {
        AccountBillingDto accountBillingDto = UCopy.copyVo2Dto(accountBillingVo, AccountBillingDto.class);
        // TODO 查询用户设置的预算
        // TODO 判断Redis是否有该用户的最早的记账时间，无则查询添加并接入Redis缓存，有则跳过
        // TODO 根据最早的记账时间，判断是否需要生成账单
        // TODO 遍历预算，根据预算天数生成账单数据
        return R.ok();
    }
}
