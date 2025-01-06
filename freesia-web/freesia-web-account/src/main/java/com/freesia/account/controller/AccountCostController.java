package com.freesia.account.controller;

import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.vo.AccountCostVo;
import com.freesia.constant.Constants;
import com.freesia.controller.BaseController;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.util.UString;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        Date[] dateRange = parseDateRange(accountCostVo.getPaymentTimeRange(), UString.SEPARATOR, Constants.SDF_YMDHMS);
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
}
