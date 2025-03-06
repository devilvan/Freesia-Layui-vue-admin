package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.account.dto.AccountBudgetDto;
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
    List<AccountBudgetPo> findListBudget(@Param("accountBudgetDto") AccountBudgetDto accountBudgetDto);
}
