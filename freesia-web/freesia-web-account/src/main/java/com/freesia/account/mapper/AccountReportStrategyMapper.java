package com.freesia.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.po.AccountReportStrategyPo;
import com.freesia.account.dto.AccountReportStrategyDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表策略表 持久层
 * @date 2026-03-03
 */
@Mapper
public interface AccountReportStrategyMapper extends BaseMapper<AccountReportStrategyPo> {
    /**
     * 分页查询记账报表策略表信息
     *
     * @param accountReportStrategyDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<AccountReportStrategyPo> findPage(@Param(value = "dto") AccountReportStrategyDto accountReportStrategyDto, @Param("page") Page<AccountReportStrategyPo> page);

    /**
     * 查询记账报表策略表信息
     *
     * @param accountReportStrategyDto 查询条件
     * @return 分页信息
     */
    List<AccountReportStrategyDto> findList(@Param(value = "dto") AccountReportStrategyDto accountReportStrategyDto);

    /**
     * 查询记账报表策略表信息
     *
     * @param accountReportStrategyDto 查询条件
     * @return 分页信息
     */
    AccountReportStrategyDto findOne(@Param(value = "dto") AccountReportStrategyDto accountReportStrategyDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<AccountReportStrategyPo> list);
}
