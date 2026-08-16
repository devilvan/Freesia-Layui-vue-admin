package com.freesia.account;

import com.freesia.account.constant.CostType;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.entity.FindPageAccountCostEntity;
import com.freesia.account.service.AccountCostService;
import com.freesia.util.UEmpty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Bliss.Wu
 * @Description 记账报表任务 帮助类
 * @date 2026-03-02
 */
public class AccountReportSchedulerHelper {

    /**
     * 查询账单记录集合
     *
     * @param userId          用户ID
     * @param billingTimeFrom 报表时间从
     * @param billingTimeTo   报表时间到
     * @return 账单记录
     */
    public static List<FindPageAccountCostEntity> findListAccountCost(AccountCostService accountCostService, Long userId, Date billingTimeFrom, Date billingTimeTo) {
        AccountCostDto accountCostDto = new AccountCostDto();
        accountCostDto.setUserId(userId);
        accountCostDto.setPaymentTimeFrom(billingTimeFrom);
        accountCostDto.setPaymentTimeTo(billingTimeTo);
        return accountCostService.findListAccountCost(accountCostDto);
    }

    /**
     * 构建收支金额
     *
     * @param userId                    用户ID
     * @param findAccountCostEntityList 账单记录
     * @param reportPo                  待修改的报表实体
     */
    public static void buildReportOutlayIncome(Long userId, List<FindPageAccountCostEntity> findAccountCostEntityList, AccountReportDto reportPo) {
        // 赋值收支金额
        BigDecimal outlay = BigDecimal.ZERO;
        BigDecimal income = BigDecimal.ZERO;
        if (UEmpty.isNotEmpty(findAccountCostEntityList)) {
            for (FindPageAccountCostEntity entity : findAccountCostEntityList) {
                String paymentSign = entity.getPaymentSign();
                CostType costType = CostType.getInstanceByCode(paymentSign);
                if (costType == null) {
                    continue;
                }
                if (costType.equals(CostType.INCOME)) {
                    // 收入金额直接获取
                    income = income.add(entity.getOutlay());
                } else if (costType.equals(CostType.EXPENSE)) {
                    // 支出金额，先判断是否存在关联分摊
                    List<AccountCostUserAllocDto> accountCostUserAllocDtoList = entity.getAccountCostUserAllocDtoList();
                    if (UEmpty.isNotEmpty(accountCostUserAllocDtoList)) {
                        if (Objects.equals(entity.getUserId(), userId)) {
                            // 本人为记录人：记录金额扣除其他分摊人分摊金额合计，剩余部分由本人承担
                            BigDecimal otherAllocSum = accountCostUserAllocDtoList.stream()
                                    .filter(Objects::nonNull)
                                    .filter(allocDto -> !Objects.equals(allocDto.getUserId(), userId))
                                    .map(AccountCostUserAllocDto::getAmount)
                                    .filter(Objects::nonNull)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal recordOutlay = entity.getOutlay();
                            BigDecimal outlayTmp = recordOutlay == null ? BigDecimal.ZERO : recordOutlay.subtract(otherAllocSum).max(BigDecimal.ZERO);
                            outlay = outlay.add(outlayTmp);
                        } else {
                            // 本人为被分摊人：支出金额取本人分摊金额
                            BigDecimal outlayTmp = accountCostUserAllocDtoList.stream()
                                    .filter(Objects::nonNull)
                                    .filter(allocDto -> Objects.equals(allocDto.getUserId(), userId))
                                    .findFirst()
                                    .map(AccountCostUserAllocDto::getAmount)
                                    .orElse(BigDecimal.ZERO);
                            outlay = outlay.add(outlayTmp);
                        }
                    } else {
                        outlay = outlay.add(entity.getOutlay());
                    }
                }
            }
            reportPo.setOutlay(outlay);
            reportPo.setIncomeAmount(income);
        }
    }
}
