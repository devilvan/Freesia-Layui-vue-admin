package com.freesia.account.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.AccountCostExportEntity;
import com.freesia.account.entity.FindCostTypeRatePieEntity;
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
}
