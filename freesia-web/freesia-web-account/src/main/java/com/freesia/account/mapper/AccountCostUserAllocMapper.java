package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.dto.FindListSysUserByIdDto;
import com.freesia.account.dto.RpFindAllocAmountDto;
import com.freesia.account.po.AccountCostUserAllocPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 持久层
 * @date 2025-10-03
 */
@Mapper
public interface AccountCostUserAllocMapper extends BaseMapper<AccountCostUserAllocPo> {

    /**
     * 修改费用分摊-根据记账ID查询分摊信息
     *
     * @param accountCostUserAllocDto 查询条件
     * @return 结果集
     */
    List<FindListSysUserByIdDto> findListAllocByCostId(@Param(value = "dto") AccountCostUserAllocDto accountCostUserAllocDto);

    /**
     * 查询他人未分摊信息
     *
     * @return 结果集
     */
    List<RpFindAllocAmountDto.Alloc> findCollected(AccountCostUserAllocPo accountCostUserAllocPo);

    /**
     * 查询本人未分摊信息
     *
     * @return 结果集
     */
    List<RpFindAllocAmountDto.Alloc> findAllocated(AccountCostUserAllocPo accountCostUserAllocPo);
}
