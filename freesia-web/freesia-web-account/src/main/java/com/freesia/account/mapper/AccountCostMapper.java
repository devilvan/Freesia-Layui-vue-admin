package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.dto.FindRankByCostTypeDto;
import com.freesia.account.entity.*;
import com.freesia.account.po.AccountCostPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销表 持久层
 * @date 2024-12-14
 */
@Mapper
public interface AccountCostMapper extends BaseMapper<AccountCostPo> {
    /**
     * 查询待导出的记账数据
     *
     * @param accountCostDto 查询条件
     * @return 待导出的数据集合
     */
    List<AccountCostExportEntity> findListAccountsExport(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 饼图-查询各类型开销比例
     *
     * @param accountCostDto 查询参数
     * @return 结果集
     */
    List<FindCostTypeRatePieEntity> findCostTypeRatePie(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 折线图-查询近一周支出数据
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    List<FindCostLineChartEntity> findWeekCostLineChart(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 折线图-根据月份查询支出数据
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    List<FindCostLineChartEntity> findMonthCostLineChart(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 折线图-根据年份查询支出数据
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    List<FindCostLineChartEntity> findYearCostLineChart(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 日历-查询近一年支出
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    List<FindCostSumCalendarNearYearEntity> findCostSumCalendarNearYear(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 查询开销表分页集合信息
     *
     * @param accountCostDto 入参
     * @return 分页数据
     */
    List<FindPageAccountCostEntity> findPageAccountCost(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 条件查询开销表
     *
     * @param accountCostDto 入参
     * @return 结果集
     */
    FindAccountCostEntity findAccountCost(@Param("accountCostDto") AccountCostDto accountCostDto);

    /**
     * 排名-按周消费类型排名
     *
     * @param accountCostDto 查询入参
     * @return 排名数据
     */
    List<FindRankByCostTypeEntity> findWeekRankByCostType(@Param("accountCostDto") FindRankByCostTypeDto accountCostDto);

    /**
     * 排名-按月消费类型排名
     *
     * @param accountCostDto 查询入参
     * @return 排名数据
     */
    List<FindRankByCostTypeEntity> findMonthRankByCostType(@Param("accountCostDto") FindRankByCostTypeDto accountCostDto);

    /**
     * 查询开销类型查询选择框
     *
     * @param accountCostDto 查询参数
     * @return 结果集
     */
    List<AccountCostPo> findSelectCostTypeList(@Param(value = "accountCostDto") AccountCostDto accountCostDto);

    /**
     * 查询分页查询所需的ID
     *
     * @param accountCostDto 查询条件
     * @param pageQuery      分页条件
     * @return ID集合
     */
    Page<Long> findPageAccountCostId(@Param(value = "accountCostDto") AccountCostDto accountCostDto, @Param(value = "page") Page<AccountCostPo> pageQuery);
}
