package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.account.po.AccountCostPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description 开销表 持久层
 * @date 2024-12-14
 */
@Mapper
public interface AccountCostMapper extends BaseMapper<AccountCostPo> {

}
