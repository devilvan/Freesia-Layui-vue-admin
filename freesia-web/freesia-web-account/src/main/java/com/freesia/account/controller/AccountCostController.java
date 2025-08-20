package com.freesia.account.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.freesia.account.constant.DateScope;
import com.freesia.account.constant.MenuPermission;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.dto.FindCostLineChartDto;
import com.freesia.account.dto.FindRankByCostTypeDto;
import com.freesia.account.entity.AccountCostExportEntity;
import com.freesia.account.entity.AccountCostImportEntity;
import com.freesia.account.entity.FindAccountCostEntity;
import com.freesia.account.entity.FindPageAccountCostEntity;
import com.freesia.account.exception.AccountException;
import com.freesia.account.listener.AccountsImportListener;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.vo.AccountCostVo;
import com.freesia.account.vo.FindCostLineChartVo;
import com.freesia.account.vo.FindCostSumCalendarNearYearVo;
import com.freesia.account.vo.FindRankByCostTypeVo;
import com.freesia.constant.Constants;
import com.freesia.controller.BaseController;
import com.freesia.entity.EchartCalendarOptionEntity;
import com.freesia.entity.EchartLineOptionEntity;
import com.freesia.entity.EchartPieOptionEntity;
import com.freesia.entity.EchartStackedHorizontalBarOptionEntity;
import com.freesia.excel.constant.ExcelCellWriteStyle;
import com.freesia.excel.constant.ExcelSuffix;
import com.freesia.excel.handler.ExcelExportHandler;
import com.freesia.excel.util.UExcel;
import com.freesia.exception.ServiceException;
import com.freesia.exception.UserException;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.oss.exception.OssException;
import com.freesia.oss.util.UOssFile;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.tenant.exception.TenantException;
import com.freesia.util.*;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 开销表 控制器
 * @date 2024-12-14
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/accountCostController")
@Tag(name = "AccountCostController", description = "开销表 控制器")
public class AccountCostController extends BaseController {
    private final AccountCostService accountCostService;

    /**
     * 保存开销表信息
     *
     * @param accountCostVo 待保存对象
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存开销表信息")
    @PostMapping(value = "saveUpdate")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_ADD),
            @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_EDIT)
    })
    public R<Void> saveUpdate(@RequestBody AccountCostVo accountCostVo) {
        AccountCostDto accountCostDto = pre2Save(accountCostVo);
        accountCostService.saveUpdate(accountCostDto);
        return R.ok();
    }

    /**
     * 批量保存开销表信息
     *
     * @param accountCostVoList 待保存对象
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "保存开销表信息")
    @PostMapping(value = "saveUpdateBatch")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_ADD),
            @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_EDIT)
    })
    public R<Void> saveUpdateBatch(@RequestBody List<AccountCostVo> accountCostVoList) {
        List<AccountCostDto> accountCostDtoList = pre2Save(accountCostVoList);
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
    @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_INDEX)
    public TableResult<FindPageAccountCostEntity> findPageAccountCost(AccountCostVo accountCostVo, PageQuery pageQuery) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!accountCostVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            accountCostVo.setTenantId(tenantId);
        } else {
            accountCostVo.setTenantId(null);
        }
        Date[] dateRange = parseDateRange(accountCostVo.getPaymentTimeRange(), Constants.SDF_YMDHMS, UString.SEPARATOR);
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        accountCostDto.setUserId(userId);
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
    @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_INDEX)
    public R<FindAccountCostEntity> findAccountCost(AccountCostVo accountCostVo) {
        AccountCostDto accountCostDto = pre2Save(accountCostVo);
        FindAccountCostEntity findAccountCostEntity = accountCostService.findAccountCost(accountCostDto);
        return R.ok(findAccountCostEntity);
    }

    /**
     * 删除开销表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "删除开销表")
    @PostMapping(value = "deleteAccountCost")
    @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_DELETE)
    public R<Void> deleteAccountCost(@RequestBody List<Long> idList) {
        accountCostService.deleteAccountCost(idList);
        return R.ok();
    }

    /**
     * 导入开支数据
     *
     * @param file 文件
     * @return 形式返回
     */
    @Idempotent
    @Operation(summary = "导入开支数据")
    @PostMapping(value = "accountsImport")
    @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_IMPORT)
    public R<Void> accountsImport(@RequestPart("file[]") MultipartFile file) {
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.') + 1))
                .orElseThrow(() -> new OssException("oss.file.required"));
        if (!ExcelSuffix.includeBySuffix(suffix)) {
            throw new UserException("import.suffix.invalid", new Object[]{suffix});
        }
        try {
            UExcel.read(file.getInputStream(), AccountCostImportEntity.class, ExcelSuffix.getInstanceBySuffix(suffix).getExcelTypeEnum(),
                    new AccountsImportListener<>(accountCostService), 0, null);
        } catch (IOException e) {
            R<Void> failed = R.failed();
            failed.setMsg(UMessage.message("file.upload.failed"));
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
    @Operation(summary = "记账导出")
    @GetMapping(value = "accountsExport")
    @SaCheckPermission(value = MenuPermission.ACCOUNT_COST_EXPORT)
    public void accountsExport(AccountCostVo accountsExportVo, HttpServletResponse response) throws IOException {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!accountsExportVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            accountsExportVo.setTenantId(tenantId);
        } else {
            accountsExportVo.setTenantId(null);
        }
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountsExportVo, AccountCostDto.class);
        accountCostDto.setUserId(userId);
        Date[] dates = parseDateRange(accountsExportVo.getPaymentTimeRange());
        accountCostDto.setPaymentTimeFrom(dates[0]);
        accountCostDto.setPaymentTimeTo(dates[1]);
        List<AccountCostExportEntity> accountCostExportEntityList = accountCostService.findBuildListAccountsExport(accountCostDto);
        doAccountExport(response, dates, accountCostExportEntityList);
    }

    @Operation(summary = "饼图-查询各类型开销比例")
    @GetMapping(value = "findCostTypeRatePie")
    public R<EchartPieOptionEntity> findCostTypeRatePie(AccountCostVo accountCostVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!accountCostVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            accountCostVo.setTenantId(tenantId);
        } else {
            accountCostVo.setTenantId(null);
        }
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        accountCostDto.setUserId(userId);
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
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!findCostLineChartVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            findCostLineChartVo.setTenantId(tenantId);
        } else {
            findCostLineChartVo.setTenantId(null);
        }
        Optional.of(findCostLineChartVo).orElseGet(FindCostLineChartVo::new);
        DateScope dateScope = Optional.of(findCostLineChartVo)
                .map(m -> DateScope.getInstanceByCode(m.getDateScope()))
                .orElse(DateScope.WEEK);
        String code = dateScope.getCode();
        findCostLineChartVo.setDateScope(code);
        FindCostLineChartDto findCostLineChartDto = UCopy.copyVo2Dto(findCostLineChartVo, FindCostLineChartDto.class);
        findCostLineChartDto.setUserId(userId);
        String dateValue = findCostLineChartVo.getDateValue();
        if (DateScope.WEEK.getCode().equals(code)) {
            Date[] dates = defaultDateRange(7);
            findCostLineChartDto.setPaymentTimeFrom(dates[0]);
            findCostLineChartDto.setPaymentTimeTo(dates[1]);
        }
        if (UEmpty.isEmpty(dateValue)) {
            if (DateScope.MONTH.getCode().equals(code)) {
                Date date = new Date();
                int month = UCalendar.getMonth(date);
                int year = UCalendar.getYear(date);
                findCostLineChartDto.setYear(year);
                findCostLineChartDto.setMonth(month);
            } else if (DateScope.YEAR.getCode().equals(code)) {
                Date date = new Date();
                int year = UCalendar.getYear(date);
                findCostLineChartDto.setYear(year);
            }
        } else {
            String[] yearMonth = dateValue.split("-");
            if (DateScope.YEAR.getCode().equals(code)) {
                if (yearMonth.length == 1) {
                    findCostLineChartDto.setYear(Integer.parseInt(yearMonth[0]));
                } else {
                    throw new ServiceException(UMessage.message("account.query.year.invalid", String.join(",", yearMonth)));
                }
            }
            if (DateScope.MONTH.getCode().equals(code)) {
                if (yearMonth.length == 2) {
                    findCostLineChartDto.setYear(Integer.parseInt(yearMonth[0]));
                    findCostLineChartDto.setMonth(Integer.parseInt(yearMonth[1]));
                } else {
                    throw new ServiceException(UMessage.message("account.query.month.invalid", String.join(",", yearMonth)));
                }
            }
        }
        EchartLineOptionEntity echartLineOptionEntity = accountCostService.findCostLineChart(findCostLineChartDto);
        return R.ok(echartLineOptionEntity);
    }

    @Validated
    @Operation(summary = "日历-查询近一年支出")
    @GetMapping(value = "findCostSumCalendarNearYear")
    public R<EchartCalendarOptionEntity> findCostSumCalendarNearYear(FindCostSumCalendarNearYearVo findCostSumCalendarNearYearVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!findCostSumCalendarNearYearVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            findCostSumCalendarNearYearVo.setTenantId(tenantId);
        } else {
            findCostSumCalendarNearYearVo.setTenantId(null);
        }
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(findCostSumCalendarNearYearVo, AccountCostDto.class);
        accountCostDto.setUserId(userId);
        Date[] dates = defaultDateRange(365);
        accountCostDto.setPaymentTimeFrom(dates[0]);
        accountCostDto.setPaymentTimeTo(dates[1]);
        EchartCalendarOptionEntity echartCalendarOptionEntity = accountCostService.findCostSumCalendarNearYear(accountCostDto);
        return R.ok(echartCalendarOptionEntity);
    }

    @Operation(summary = "排名-按消费类型排名")
    @GetMapping(value = "findRankByCostType")
    public R<EchartStackedHorizontalBarOptionEntity> findRankByCostType(FindRankByCostTypeVo findRankByCostTypeVo) {
        String dateScope = findRankByCostTypeVo.getDateScope();
        if (UEmpty.isEmpty(dateScope) || null == DateScope.getInstanceByCode(dateScope)) {
            throw new AccountException("dateScope.invalid", new Object[]{dateScope});
        }
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!findRankByCostTypeVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            findRankByCostTypeVo.setTenantId(tenantId);
        } else {
            findRankByCostTypeVo.setTenantId(null);
        }
        FindRankByCostTypeDto findRankByCostTypeDto = UCopy.copyVo2Dto(findRankByCostTypeVo, FindRankByCostTypeDto.class);
        findRankByCostTypeDto.setUserId(userId);
        EchartStackedHorizontalBarOptionEntity echartStackedHorizontalBarOptionEntity = accountCostService.findRankByCostType(findRankByCostTypeDto);
        return R.ok(echartStackedHorizontalBarOptionEntity);
    }

    @Operation(summary = "排名-按消费类型排名")
    @PostMapping(value = "refreshCache")
    public R<Void> refreshCache() {
        accountCostService.refreshCache();
        return R.ok();
    }


    /**
     * excel数据流输出到响应体
     *
     * @param response                    HttpServlet响应体
     * @param dates                       查询数据时间段
     * @param accountCostExportEntityList 导出数据
     * @throws IOException IO流异常
     */
    private void doAccountExport(HttpServletResponse response, Date[]
            dates, List<AccountCostExportEntity> accountCostExportEntityList) throws IOException {
        String dateFrom = Constants.SDF_YMD.format(dates[0]);
        String dateTo = Constants.SDF_YMD.format(dates[1]);
        String title = dateFrom + "到" + dateTo + "记账合计";
        String fileName = URLEncoder.encode(title, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + "." + ExcelSuffix.XLS.getSuffix());
        response.setHeader(UOssFile.DOWNLOAD_FILENAME, fileName + "." + ExcelSuffix.XLS.getSuffix());
        response.setContentType(ExcelUtil.XLS_CONTENT_TYPE);
        response.setCharacterEncoding(Constants.UTF_8);
        EasyExcelFactory.write(response.getOutputStream())
                .head(AccountCostExportEntity.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(ExcelExportHandler.getBasicWriteStrategy())
                .registerWriteHandler(new ExcelCellWriteStyle<>())
                .sheet(0, title)
                .doWrite(accountCostExportEntityList);
    }

    /**
     * 保存记账数据前操作
     *
     * @param accountCostVoList 前端入参
     * @return 组装好的数据
     */
    private List<AccountCostDto> pre2Save(List<AccountCostVo> accountCostVoList) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
        List<AccountCostDto> accountCostDtoList = new ArrayList<>();
        accountCostVoList.forEach(accountCostVo -> {
            AccountCostDto accountCostDto = new AccountCostDto();
            if (!accountCostVo.getAllTenantFlag()) {
                accountCostVo.setTenantId(tenantId);
            } else {
                accountCostVo.setTenantId(null);
            }
            UCopy.fullCopy(accountCostVo, accountCostDto);
            accountCostDto.setUserId(userId);
            accountCostDto.setAccountCostUserIdList(accountCostVo.getAccountCostUserIdList());
            accountCostDtoList.add(accountCostDto);
        });
        return accountCostDtoList;
    }

    /**
     * 保存记账数据前操作
     *
     * @param accountCostVo 前端入参
     * @return 组装好的数据
     */
    private AccountCostDto pre2Save(AccountCostVo accountCostVo) {
        AccountCostDto accountCostDto = UCopy.copyVo2Dto(accountCostVo, AccountCostDto.class);
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        if (!accountCostVo.getAllTenantFlag()) {
            Long tenantId = Optional.ofNullable(USecurity.getTenantId()).orElseThrow(() -> new TenantException("tenant.required", new Object[]{}));
            accountCostDto.setTenantId(tenantId);
        } else {
            accountCostDto.setTenantId(null);
        }
        accountCostDto.setUserId(userId);
        accountCostDto.setAccountCostUserIdList(accountCostVo.getAccountCostUserIdList());
        return accountCostDto;
    }
}
