package com.freesia.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.po.AccountBillingStrategyPo;
import com.freesia.account.dto.AccountBillingStrategyDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 持久层
 * @date 2026-02-17
 */
@Mapper
public interface AccountBillingStrategyMapper extends BaseMapper<AccountBillingStrategyPo> {
    /**
    * 分页查询记账账单策略表信息
    *
    * @param accountBillingStrategyDto 查询条件
    * @param page    分页条件
    * @return 分页信息
    */
    Page<AccountBillingStrategyPo> findPage(@Param(value = "dto") AccountBillingStrategyDto accountBillingStrategyDto, @Param("page") Page<AccountBillingStrategyPo> page);

    /**
    * 查询记账账单策略表信息
    *
    * @param accountBillingStrategyDto 查询条件
    * @return 分页信息
    */
    List<AccountBillingStrategyDto> findList(@Param(value = "dto") AccountBillingStrategyDto accountBillingStrategyDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<AccountBillingStrategyPo> list);
}
