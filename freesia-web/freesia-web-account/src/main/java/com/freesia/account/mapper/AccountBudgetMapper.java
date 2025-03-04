package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.account.po.AccountBudgetPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 持久层
 * @date 2025-03-04
 */
@Mapper
public interface AccountBudgetMapper extends BaseMapper<AccountBudgetPo> {

}
