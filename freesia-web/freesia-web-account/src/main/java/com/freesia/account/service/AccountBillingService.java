package com.freesia.account.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.account.dto.AccountBillingDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单表 业务逻辑接口
 * @date 2026-02-17
 */
public interface AccountBillingService {
    /**
     * 保存记账账单表信息
     *
     * @param accountBillingDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountBillingDto saveUpdate(AccountBillingDto accountBillingDto);

    /**
     * 批量保存记账账单表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountBillingDto> saveUpdateBatch(List<AccountBillingDto> list);

    /**
     * 查询记账账单表信息
     *
     * @param accountBillingDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<AccountBillingDto> findPage(AccountBillingDto accountBillingDto, PageQuery pageQuery);

    /**
     * 条件查询记账账单表信息
     *
     * @param accountBillingDto 查询条件
     * @return 记账账单表信息
     */
    AccountBillingDto findOne(AccountBillingDto accountBillingDto);

    /**
     * 条件查询记账账单表信息
     *
     * @param accountBillingDto 查询条件
     * @return 记账账单表信息
     */
    List<AccountBillingDto> findList(AccountBillingDto accountBillingDto);

    /**
     * 删除记账账单表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
