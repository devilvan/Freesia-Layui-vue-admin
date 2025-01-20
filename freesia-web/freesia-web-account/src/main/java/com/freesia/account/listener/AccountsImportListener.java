package com.freesia.account.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSONObject;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.AccountCostImportEntity;
import com.freesia.account.service.AccountCostService;
import com.freesia.constant.FlagConstant;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.pojo.BaseImportEntity;
import com.freesia.exception.ServiceException;
import com.freesia.satoken.constant.UserType;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCollection;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.validation.util.USpringValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 用户导入 Excel数理类
 * @date 2024-08-02
 */
@Slf4j
@AllArgsConstructor
public class AccountsImportListener<T extends BaseImportEntity> extends BaseImportEntityListener<T> {
    private final AccountCostService accountCostService;

    @Override
    public void invoke(T accountCostImportEntity, AnalysisContext context) {
        cachedDataList.add(accountCostImportEntity);
        if (cachedDataList.size() >= BATCH_COUNT) {
            transactionSaveSysUser();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        transactionSaveSysUser();
    }

    private void transactionSaveSysUser() {
        List<AccountCostDto> accountCostDtoList = UCollection.optimizeInitialCapacityArrayList(cachedDataList.size());
        for (T accountCostImportEntity : cachedDataList) {
            // 数据校验
            errorMsg.addAll(USpringValidation.errorMsg(accountCostImportEntity));
            AccountCostDto sysUserDto = buildAccountCostDto((AccountCostImportEntity) accountCostImportEntity);
            accountCostDtoList.add(sysUserDto);
        }
        if (UEmpty.isNotEmpty(errorMsg)) {
            throw new ServiceException(UCollection.join(errorMsg, "\n"));
        }
        if (UEmpty.isNotEmpty(accountCostDtoList)) {
            // 保存
            accountCostService.saveUpdateBatch(accountCostDtoList);
        }
        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    private AccountCostDto buildAccountCostDto(AccountCostImportEntity accountCostImportEntity) {
        AccountCostDto accountCostDto = new AccountCostDto();
        accountCostDto.setTenantId(USecurity.getTenantId());
        accountCostDto.setCostDesc(accountCostImportEntity.getCostDesc());
        accountCostDto.setOutlay(accountCostImportEntity.getOutlay());
        String icon = accountCostImportEntity.getCostType();
        String costType = Optional.ofNullable(icon).map(m -> {
            String[] split = m.split("_");
            if (split.length > 0) {
                return split[0];
            }
            return null;
        }).orElse("");
        accountCostDto.setCostType(costType);
        accountCostDto.setIcon(icon);
        accountCostDto.setPaymentSign(accountCostImportEntity.getPaymentSign());
        accountCostDto.setPaymentTime(accountCostImportEntity.getPaymentTime());
        accountCostDto.setRemark(accountCostImportEntity.getRemark());
        return accountCostDto;
    }
}
