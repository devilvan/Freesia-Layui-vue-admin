package com.freesia.account.service;

import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 业务逻辑接口
 * @date 2025-03-04
 */
public interface AccountBudgetService {
    /**
     * 保存开销-预算表信息
     *
     * @param accountBudgetDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountBudgetDto saveUpdate(AccountBudgetDto accountBudgetDto);

    /**
     * 批量保存开销-预算表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountBudgetDto> saveUpdateBatch(List<AccountBudgetDto> list);

    /**
     * 查询开销-预算表信息
     *
     * @param accountBudgetDto 查询条件
     * @param pageQuery        分页条件
     * @return 分页信息
     */
    TableResult<AccountBudgetDto> findPageAccountBudget(AccountBudgetDto accountBudgetDto, PageQuery pageQuery);

    /**
     * 条件查询开销-预算表信息
     *
     * @param accountBudgetDto 查询条件
     * @return 开销-预算表信息
     */
    AccountBudgetDto findAccountBudget(AccountBudgetDto accountBudgetDto);

    /**
     * 删除开销-预算表信息
     *
     * @param idList 主键
     */
    void deleteAccountBudget(List<Long> idList);
}
