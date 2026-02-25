package com.freesia.account.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.dto.AccountReportStrategyDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表策略表 业务逻辑接口
 * @date 2026-02-25
 */
public interface AccountReportStrategyService {
    /**
     * 保存记账报表策略表信息
     *
     * @param accountReportStrategyDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountReportStrategyDto saveUpdate(AccountReportStrategyDto accountReportStrategyDto);

    /**
     * 批量保存记账报表策略表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountReportStrategyDto> saveUpdateBatch(List<AccountReportStrategyDto> list);

    /**
     * 查询记账报表策略表信息
     *
     * @param accountReportStrategyDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<AccountReportStrategyDto> findPage(AccountReportStrategyDto accountReportStrategyDto, PageQuery pageQuery);

    /**
     * 条件查询记账报表策略表信息
     *
     * @param accountReportStrategyDto 查询条件
     * @return 记账报表策略表信息
     */
    AccountReportStrategyDto findOne(AccountReportStrategyDto accountReportStrategyDto);

    /**
     * 条件查询记账报表策略表信息
     *
     * @param accountReportStrategyDto 查询条件
     * @return 记账报表策略表信息
     */
    List<AccountReportStrategyDto> findList(AccountReportStrategyDto accountReportStrategyDto);

    /**
     * 删除记账报表策略表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
