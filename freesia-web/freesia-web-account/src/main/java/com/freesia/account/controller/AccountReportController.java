package com.freesia.account.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.account.converter.AccountReportConverter;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.scheduler.GenerateReportTaskScheduler;
import com.freesia.account.scheduler.RecalculateReportScheduler;
import com.freesia.account.service.AccountReportService;
import com.freesia.account.vo.AccountReportVo;
import com.freesia.constant.Constants;
import com.freesia.controller.BaseController;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UEmpty;
import com.freesia.util.UString;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表表 控制器
 * @date 2026-02-25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountReportController")
@Tag(name = "AccountReportController", description = "记账报表表 控制器")
public class AccountReportController extends BaseController {
    private final AccountReportService accountReportService;
    private final AccountReportConverter accountReportConverter;
    private final GenerateReportTaskScheduler generateReportTaskScheduler;
    private final RecalculateReportScheduler recalculateReportScheduler;

    /**
     * 保存记账报表表信息
     *
     * @param accountReportVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账报表表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountReportVo accountReportVo) {
        AccountReportDto accountReportDto = accountReportConverter.convertVo2Dto(accountReportVo);
        accountReportService.saveUpdate(accountReportDto);
        return R.ok();
    }

    /**
     * 批量保存记账报表表信息
     *
     * @param accountReportVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存记账报表表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountReportVo> accountReportVoList) {
        List<AccountReportDto> accountReportDtoList = accountReportConverter.convertBatchVo2Dto(accountReportVoList);
        accountReportService.saveUpdateBatch(accountReportDtoList);
        return R.ok();
    }

    /**
     * 查询记账报表表分页信息
     *
     * @param accountReportVo 查询条件
     * @param pageQuery       分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询记账报表表分页信息")
    @GetMapping(value = "findPageAccountReport")
    public TableResult<AccountReportDto> findPageAccountReport(AccountReportVo accountReportVo, PageQuery pageQuery) {
        String billingTimeRange = accountReportVo.getBillingTimeRange();
        if (UEmpty.isNotEmpty(billingTimeRange)) {
            Date[] dateRange = parseDateRange(billingTimeRange, Constants.SDF_YMDHMS, UString.SEPARATOR);
            accountReportVo.setBillingTimeFrom(dateRange[0]);
            accountReportVo.setBillingTimeTo(dateRange[1]);
        }
        AccountReportDto accountReportDto = accountReportConverter.convertVo2Dto(accountReportVo);
        return accountReportService.findPage(accountReportDto, pageQuery);
    }

    /**
     * 条件查询记账报表表
     *
     * @param accountReportVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询记账报表表")
    @GetMapping(value = "findAccountReport")
    public R<AccountReportDto> findAccountReport(AccountReportVo accountReportVo) {
        AccountReportDto accountReportDto = accountReportConverter.convertVo2Dto(accountReportVo);
        accountReportDto = accountReportService.findOne(accountReportDto);
        return R.ok(accountReportDto);
    }

    /**
     * 条件查询记账报表表
     *
     * @param accountReportVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询记账报表表")
    @GetMapping(value = "findListAccountReport")
    public R<List<AccountReportDto>> findListAccountReport(AccountReportVo accountReportVo) {
        AccountReportDto accountReportDto = accountReportConverter.convertVo2Dto(accountReportVo);
        List<AccountReportDto> accountReportDtoList = accountReportService.findList(accountReportDto);
        return R.ok(accountReportDtoList);
    }

    /**
     * 删除记账报表表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除记账报表表")
    @PostMapping(value = "deleteAccountReport")
    public R<Void> deleteAccountReport(@RequestBody List<Long> idList) {
        accountReportService.deleteBatch(idList);
        return R.ok();
    }

    /**
     * 生成报表任务
     *
     * @return 形式返回
     */
    @SaIgnore
    @Operation(summary = "生成报表任务")
    @PostMapping(value = "generateReportTask")
    public R<Void> generateReportTask() {
        generateReportTaskScheduler.generateReportTask();
        return R.ok();
    }

    /**
     * 重算报表数据
     *
     * @return 形式返回
     */
    @SaIgnore
    @Operation(summary = "重算报表数据")
    @PostMapping(value = "recalculateReport")
    public R<Void> recalculateReport() {
        recalculateReportScheduler.recalculateDayReport();
        recalculateReportScheduler.recalculateWeekReport();
        recalculateReportScheduler.recalculateMonthReport();
        recalculateReportScheduler.recalculateYearReport();
        return R.ok();
    }
}
