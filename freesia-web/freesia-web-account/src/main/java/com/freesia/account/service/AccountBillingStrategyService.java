package com.freesia.account.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.dto.AccountBillingStrategyDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 业务逻辑接口
 * @date 2026-02-17
 */
public interface AccountBillingStrategyService {
    /**
     * 保存记账账单策略表信息
     *
     * @param accountBillingStrategyDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountBillingStrategyDto saveUpdate(AccountBillingStrategyDto accountBillingStrategyDto);

    /**
     * 批量保存记账账单策略表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountBillingStrategyDto> saveUpdateBatch(List<AccountBillingStrategyDto> list);

    /**
     * 查询记账账单策略表信息
     *
     * @param accountBillingStrategyDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<AccountBillingStrategyDto> findPage(AccountBillingStrategyDto accountBillingStrategyDto, PageQuery pageQuery);

    /**
     * 条件查询记账账单策略表信息
     *
     * @param accountBillingStrategyDto 查询条件
     * @return 记账账单策略表信息
     */
    AccountBillingStrategyDto findOne(AccountBillingStrategyDto accountBillingStrategyDto);

    /**
     * 条件查询记账账单策略表信息
     *
     * @param accountBillingStrategyDto 查询条件
     * @return 记账账单策略表信息
     */
    List<AccountBillingStrategyDto> findList(AccountBillingStrategyDto accountBillingStrategyDto);

    /**
     * 删除记账账单策略表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
