package com.freesia.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.po.AccountBillingPo;
import com.freesia.account.dto.AccountBillingDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单表 持久层
 * @date 2026-02-17
 */
@Mapper
public interface AccountBillingMapper extends BaseMapper<AccountBillingPo> {
    /**
    * 分页查询记账账单表信息
    *
    * @param accountBillingDto 查询条件
    * @param page    分页条件
    * @return 分页信息
    */
    Page<AccountBillingPo> findPage(@Param(value = "dto") AccountBillingDto accountBillingDto, @Param("page") Page<AccountBillingPo> page);

    /**
    * 查询记账账单表信息
    *
    * @param accountBillingDto 查询条件
    * @return 分页信息
    */
    List<AccountBillingDto> findList(@Param(value = "dto") AccountBillingDto accountBillingDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<AccountBillingPo> list);
}
