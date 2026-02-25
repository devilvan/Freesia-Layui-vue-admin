package com.freesia.account.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.vo.AccountReportStrategyVo;
import com.freesia.account.dto.AccountReportStrategyDto;
import com.freesia.account.service.AccountReportStrategyService;
import com.freesia.account.converter.AccountReportStrategyConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表策略表 控制器
 * @date 2026-02-25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountReportStrategyController")
@Tag(name = "AccountReportStrategyController", description = "记账报表策略表 控制器")
public class AccountReportStrategyController extends BaseController {
    private final AccountReportStrategyService accountReportStrategyService;
    private final AccountReportStrategyConverter accountReportStrategyConverter;

    /**
     * 保存记账报表策略表信息
     *
     * @param accountReportStrategyVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账报表策略表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountReportStrategyVo accountReportStrategyVo) {
        AccountReportStrategyDto accountReportStrategyDto = accountReportStrategyConverter.convertVo2Dto(accountReportStrategyVo);
        accountReportStrategyService.saveUpdate(accountReportStrategyDto);
        return R.ok();
    }

    /**
     * 批量保存记账报表策略表信息
     *
     * @param accountReportStrategyVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账报表策略表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountReportStrategyVo> accountReportStrategyVoList) {
        List<AccountReportStrategyDto> accountReportStrategyDtoList = accountReportStrategyConverter.convertBatchVo2Dto(accountReportStrategyVoList);
        accountReportStrategyService.saveUpdateBatch(accountReportStrategyDtoList);
        return R.ok();
    }

    /**
     * 查询记账报表策略表分页信息
     *
     * @param accountReportStrategyVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询记账报表策略表分页信息")
    @GetMapping(value = "findPageAccountReportStrategy")
    public TableResult<AccountReportStrategyDto> findPageAccountReportStrategy(AccountReportStrategyVo accountReportStrategyVo, PageQuery pageQuery) {
        AccountReportStrategyDto accountReportStrategyDto = accountReportStrategyConverter.convertVo2Dto(accountReportStrategyVo);
        return accountReportStrategyService.findPage(accountReportStrategyDto, pageQuery);
    }

    /**
     * 条件查询记账报表策略表
     *
     * @param accountReportStrategyVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询记账报表策略表")
    @GetMapping(value = "findAccountReportStrategy")
    public R<AccountReportStrategyDto> findAccountReportStrategy(AccountReportStrategyVo accountReportStrategyVo) {
        AccountReportStrategyDto accountReportStrategyDto = accountReportStrategyConverter.convertVo2Dto(accountReportStrategyVo);
        accountReportStrategyDto = accountReportStrategyService.findOne(accountReportStrategyDto);
        return R.ok(accountReportStrategyDto);
    }

    /**
    * 条件查询记账报表策略表
    *
    * @param accountReportStrategyVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询记账报表策略表")
    @GetMapping(value = "findListAccountReportStrategy")
    public R<List<AccountReportStrategyDto>> findListAccountReportStrategy(AccountReportStrategyVo accountReportStrategyVo) {
        AccountReportStrategyDto accountReportStrategyDto = accountReportStrategyConverter.convertVo2Dto(accountReportStrategyVo);
        List<AccountReportStrategyDto> accountReportStrategyDtoList = accountReportStrategyService.findList(accountReportStrategyDto);
        return R.ok(accountReportStrategyDtoList);
    }

    /**
     * 删除记账报表策略表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除记账报表策略表")
    @PostMapping(value = "deleteAccountReportStrategy")
    public R<Void> deleteAccountReportStrategy(@RequestBody List<Long> idList) {
        accountReportStrategyService.deleteBatch(idList);
        return R.ok();
    }
}
