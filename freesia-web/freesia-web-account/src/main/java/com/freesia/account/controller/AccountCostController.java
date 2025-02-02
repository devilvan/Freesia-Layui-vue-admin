package com.freesia.account.controller;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.freesia.account.constant.DateScope;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.AccountCostExportEntity;
import com.freesia.account.entity.AccountCostImportEntity;
import com.freesia.account.listener.AccountsImportListener;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.vo.AccountCostVo;
import com.freesia.account.vo.FindCostLineChartVo;
import com.freesia.account.vo.FindCostSumCalendarNeaerYearVo;
import com.freesia.constant.Constants;
import com.freesia.controller.BaseController;
import com.freesia.entity.EchartCalendarOptionEntity;
import com.freesia.entity.EchartLineOptionEntity;
import com.freesia.entity.EchartPieOptionEntity;
import com.freesia.excel.constant.ExcelSuffix;
import com.freesia.excel.handler.ExcelExportHandler;
import com.freesia.excel.pojo.ExcelExportDto;
import com.freesia.excel.util.UExcel;
import com.freesia.exception.ServiceException;
import com.freesia.exception.UserException;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.oss.exception.OssException;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.util.UString;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 开销表 控制器
 * @date 2024-12-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountCostController")
@Tag(name = "AccountCostController", description = "开销表 控制器")
public class AccountCostController extends BaseController {
    private final AccountCostService accountCostService;

    private static void doAccountsExport(List<AccountCostExportEntity> accountCostExportEntityList, Date[] dates) {
        ExcelExportDto<AccountCostExportEntity> excelExportDto = new ExcelExportDto<>();
        excelExportDto.setExportPath("C:\\Mine\\");
        excelExportDto.setFileName("export");
        excelExportDto.setSuffix(ExcelTypeEnum.XLSX);
        excelExportDto.setClassType(AccountCostExportEntity.class);
        excelExportDto.setList(accountCostExportEntityList);
        ExcelWriter excelWriter = ExcelExportHandler.buildExcelWriter(excelExportDto);
        String dateFrom = Constants.SDF_YMD.format(dates[0]);
        String dateTo = Constants.SDF_YMD.format(dates[1]);
        try {
            excelWriter.write(() -> accountCostExportEntityList, ExcelExportHandler.buildWriteSheet(0, dateFrom + "到" + dateTo + "记账合计"));
        } finally {
            excelWriter.finish();
        }
    }

    /**
     * 保存开销表信息
     *
     * @param accountCostVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存开销表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody AccountCostVo accountCostVo) {
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        Long tenantId = USecurity.getTenantId();
        accountCostDto.setTenantId(tenantId);
        accountCostService.saveUpdate(accountCostDto);
        return R.ok();
    }

    /**
     * 批量保存开销表信息
     *
     * @param accountCostVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存开销表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<AccountCostVo> accountCostVoList) {
        List<AccountCostDto> accountCostDtoList = UCopy.fullCopyList(accountCostVoList, AccountCostDto.class);
        Long tenantId = USecurity.getTenantId();
        for (AccountCostDto accountCostDto : accountCostDtoList) {
            accountCostDto.setTenantId(tenantId);
        }
        accountCostService.saveUpdateBatch(accountCostDtoList);
        return R.ok();
    }

    /**
     * 查询开销表分页信息
     *
     * @param accountCostVo 查询条件
     * @param pageQuery     分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询开销表分页信息")
    @GetMapping(value = "findPageAccountCost")
    public TableResult<AccountCostDto> findPageAccountCost(AccountCostVo accountCostVo, PageQuery pageQuery) {
        Date[] dateRange = parseDateRange(accountCostVo.getPaymentTimeRange(), Constants.SDF_YMDHMS, UString.SEPARATOR);
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        accountCostDto.setPaymentTimeFrom(dateRange[0]);
        accountCostDto.setPaymentTimeTo(dateRange[1]);
        return accountCostService.findPageAccountCost(accountCostDto, pageQuery);
    }

    /**
     * 条件查询开销表
     *
     * @param accountCostVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询开销表")
    @GetMapping(value = "findAccountCost")
    public R<AccountCostDto> findAccountCost(AccountCostVo accountCostVo) {
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        AccountCostDto tableResult = accountCostService.findAccountCost(accountCostDto);
        return R.ok(tableResult);
    }

    /**
     * 删除开销表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除开销表")
    @PostMapping(value = "deleteAccountCost")
    public R<Void> deleteAccountCost(@RequestBody List<Long> idList) {
        accountCostService.deleteAccountCost(idList);
        return R.ok();
    }

    /**
     * 导入开支数据
     *
     * @param file 文件
     * @return
     */
    @Idempotent
//    @SaCheckPermission(value = MenuPermission.SYSTEM_USER_IMPORT_USER)
    @Operation(summary = "导入开支数据")
    @PostMapping(value = "accountsImport")
    public R<Void> accountsImport(@RequestPart("file[]") MultipartFile file) {
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.') + 1))
                .orElseThrow(() -> new OssException("oss.file.required"));
        if (!ExcelSuffix.includeBySuffix(suffix)) {
            throw new UserException("import.suffix.invalid", suffix);
        }
        try {
            UExcel.read(file.getInputStream(), AccountCostImportEntity.class, ExcelSuffix.getInstanceBySuffix(suffix).getExcelTypeEnum(),
                    new AccountsImportListener<>(accountCostService), 0, null);
        } catch (IOException e) {
            e.printStackTrace();
            R<Void> failed = R.failed();
            failed.setMsg(UMessage.message("upload.failed"));
            return failed;
        }
        return R.ok();
    }

    /**
     * 记账导出
     *
     * @param accountsExportVo 查询入参
     * @return 形式返回
     */
    @Idempotent
//    @SaCheckPermission(value = MenuPermission.SYSTEM_USER_IMPORT_USER)
    @Operation(summary = "记账导出")
    @GetMapping(value = "accountsExport")
    public R<Void> accountsExport(AccountCostVo accountsExportVo) {
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountsExportVo, AccountCostDto.class);
        accountCostDto.setTenantId(USecurity.getTenantId());
        Date[] dates = parseDateRange(accountsExportVo.getPaymentTimeRange());
        accountCostDto.setPaymentTimeFrom(dates[0]);
        accountCostDto.setPaymentTimeTo(dates[1]);
        List<AccountCostExportEntity> accountCostExportEntityList = accountCostService.findBuildListAccountsExport(accountCostDto);
        doAccountsExport(accountCostExportEntityList, dates);
        return R.ok();
    }

    @Operation(summary = "饼图-查询各类型开销比例")
    @GetMapping(value = "findCostTypeRatePie")
    public R<EchartPieOptionEntity> findCostTypeRatePie(AccountCostVo accountCostVo) {
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        if (UEmpty.isEmpty(accountCostVo.getPaymentTimeRange())) {
            Date[] dates = defaultDateRange(7);
            accountCostDto.setPaymentTimeFrom(dates[0]);
            accountCostDto.setPaymentTimeTo(dates[1]);
        } else {
            Date[] dates = parseDateRange(accountCostVo.getPaymentTimeRange(), Constants.SDF_YMDHMS);
            accountCostDto.setPaymentTimeFrom(dates[0]);
            accountCostDto.setPaymentTimeTo(dates[1]);
        }
        EchartPieOptionEntity echartPieOptionEntity = accountCostService.findCostTypeRatePie(accountCostDto);
        return R.ok(echartPieOptionEntity);
    }

    @Validated
    @Operation(summary = "折线图-根据时间查询")
    @GetMapping(value = "findCostLineChart")
    public R<EchartLineOptionEntity> findCostLineChart(FindCostLineChartVo findCostLineChartVo) {
        findCostLineChartVo = Optional.ofNullable(findCostLineChartVo).orElseGet(FindCostLineChartVo::new);
        DateScope dateScope = Optional.of(findCostLineChartVo)
                .map(m -> DateScope.getInstanceByCode(m.getDateScope()))
                .orElse(DateScope.WEEK);
        String code = dateScope.getCode();
        findCostLineChartVo.setDateScope(code);
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(findCostLineChartVo, AccountCostDto.class);
        String dateValue = findCostLineChartVo.getDateValue();
        if (DateScope.MONTH.getCode().equals(code) || DateScope.YEAR.getCode().equals(code)) {
            if (UEmpty.isEmpty(dateValue)) {
                throw new ServiceException(UMessage.message("account.dateValue.not.empty"));
            }
            String[] yearMonth = dateValue.split("-");
            if (DateScope.YEAR.getCode().equals(code)) {
                if (yearMonth.length == 1) {
                    accountCostDto.setYear(Integer.parseInt(yearMonth[0]));
                } else {
                    throw new ServiceException(UMessage.message("account.query.year.invalid", String.join(",", yearMonth)));
                }
            }
            if (DateScope.MONTH.getCode().equals(code)) {
                if (yearMonth.length == 2) {
                    accountCostDto.setYear(Integer.parseInt(yearMonth[0]));
                    accountCostDto.setMonth(Integer.parseInt(yearMonth[1]));
                } else {
                    throw new ServiceException(UMessage.message("account.query.month.invalid", String.join(",", yearMonth)));
                }
            }
        }
        if (DateScope.WEEK.getCode().equals(code)) {
            Date[] dates = defaultDateRange(7);
            accountCostDto.setPaymentTimeFrom(dates[0]);
            accountCostDto.setPaymentTimeTo(dates[1]);
        }
        EchartLineOptionEntity echartLineOptionEntity = accountCostService.findCostLineChart(accountCostDto);
        return R.ok(echartLineOptionEntity);
    }

    @Validated
    @Operation(summary = "日历-查询近一年支出")
    @GetMapping(value = "findCostSumCalendarNearYear")
    public R<EchartCalendarOptionEntity> findCostSumCalendarNearYear(FindCostSumCalendarNeaerYearVo findCostSumCalendarNeaerYearVo) {
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(findCostSumCalendarNeaerYearVo, AccountCostDto.class);
        Date[] dates = defaultDateRange(365);
        accountCostDto.setPaymentTimeFrom(dates[0]);
        accountCostDto.setPaymentTimeTo(dates[1]);
        EchartCalendarOptionEntity echartCalendarOptionEntity = accountCostService.findCostSumCalendarNearYear(accountCostDto);
        return R.ok(echartCalendarOptionEntity);
    }
}
