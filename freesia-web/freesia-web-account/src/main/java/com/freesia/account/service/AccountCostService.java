package com.freesia.account.service;

import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.AccountCostExportEntity;
import com.freesia.account.entity.FindAccountCostEntity;
import com.freesia.account.entity.FindPageAccountCostEntity;
import com.freesia.account.entity.FindRankByCostTypeEntity;
import com.freesia.entity.EchartCalendarOptionEntity;
import com.freesia.entity.EchartLineOptionEntity;
import com.freesia.entity.EchartPieOptionEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销表 业务逻辑接口
 * @date 2024-12-14
 */
public interface AccountCostService {
    /**
     * 保存开销表信息
     *
     * @param accountCostDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountCostDto saveUpdate(AccountCostDto accountCostDto);

    /**
     * 批量保存开销表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountCostDto> saveUpdateBatch(List<AccountCostDto> list);

    /**
     * 查询开销表信息
     *
     * @param accountCostDto 查询条件
     * @param pageQuery      分页条件
     * @return 分页信息
     */
    TableResult<FindPageAccountCostEntity> findPageAccountCost(AccountCostDto accountCostDto, PageQuery pageQuery);

    /**
     * 条件查询开销表信息
     *
     * @param accountCostDto 查询条件
     * @return 开销表信息
     */
    FindAccountCostEntity findAccountCost(AccountCostDto accountCostDto);

    /**
     * 删除开销表信息
     *
     * @param idList 主键
     */
    void deleteAccountCost(List<Long> idList);

    /**
     * 查询待导出的记账数据，并构建导出格式
     *
     * @param accountCostDto 查询条件
     * @return 待导出的数据集合
     */
    List<AccountCostExportEntity> findBuildListAccountsExport(AccountCostDto accountCostDto);

    /**
     * 饼图-查询各类型开销比例
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    EchartPieOptionEntity findCostTypeRatePie(AccountCostDto accountCostDto);

    /**
     * 折线图-根据时间查询
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    EchartLineOptionEntity findCostLineChart(AccountCostDto accountCostDto);

    /**
     * 日历-查询近一年支出
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    EchartCalendarOptionEntity findCostSumCalendarNearYear(AccountCostDto accountCostDto);

    /**
     * 排名-按消费类型排名
     *
     * @param accountCostDto 查询入参
     * @return 排名数据
     */
    FindRankByCostTypeEntity findRankByCostType(AccountCostDto accountCostDto);
}
