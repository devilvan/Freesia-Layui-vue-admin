package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.FindBudgetCapacityDto;
import com.freesia.account.entity.FindBudgetCapacityEntity;
import com.freesia.account.po.AccountBudgetPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 持久层
 * @date 2025-03-04
 */
@Mapper
public interface AccountBudgetMapper extends BaseMapper<AccountBudgetPo> {
    /**
     * 条件查询
     *
     * @param accountBudgetDto 查询条件
     * @return 结果集
     */
    List<AccountBudgetDto> findListBudget(@Param("accountBudgetDto") AccountBudgetDto accountBudgetDto);

    /**
     * 容量图-查询日预算数据
     *
     * @param findBudgetCapacityDto 查询条件
     * @return 容量图数据
     */
    List<FindBudgetCapacityEntity> findDayBudgetCapacity(@Param("findBudgetCapacityDto") FindBudgetCapacityDto findBudgetCapacityDto);

    /**
     * 容量图-查询周预算数据
     *
     * @param findBudgetCapacityDto 查询条件
     * @return 容量图数据
     */
    List<FindBudgetCapacityEntity> findWeekBudgetCapacity(@Param("findBudgetCapacityDto") FindBudgetCapacityDto findBudgetCapacityDto);

    /**
     * 容量图-查询月预算数据
     *
     * @param findBudgetCapacityDto 查询条件
     * @return 容量图数据
     */
    List<FindBudgetCapacityEntity> findMonthBudgetCapacity(@Param("findBudgetCapacityDto") FindBudgetCapacityDto findBudgetCapacityDto);

    /**
     * 容量图-查询年预算数据
     *
     * @param findBudgetCapacityDto 查询条件
     * @return 容量图数据
     */
    List<FindBudgetCapacityEntity> findYearBudgetCapacity(@Param("findBudgetCapacityDto") FindBudgetCapacityDto findBudgetCapacityDto);

    /**
     * 容量图-查询自定义预算数据
     *
     * @param findBudgetCapacityDto 查询条件
     * @return 容量图数据
     */
    List<FindBudgetCapacityEntity> findCustomBudgetCapacity(@Param("findBudgetCapacityDto") FindBudgetCapacityDto findBudgetCapacityDto);

}
