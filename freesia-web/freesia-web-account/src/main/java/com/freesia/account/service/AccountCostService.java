package com.freesia.account.service;

import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.AccountCostExportEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销表 业务逻辑接口
 * @date 2024-12-14
 */
public interface AccountCostService {
    /**
     * 保存开销表信息
     *
     * @param accountCostDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountCostDto saveUpdate(AccountCostDto accountCostDto);

    /**
     * 批量保存开销表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountCostDto> saveUpdateBatch(List<AccountCostDto> list);

    /**
     * 查询开销表信息
     *
     * @param accountCostDto 查询条件
     * @param pageQuery      分页条件
     * @return 分页信息
     */
    TableResult<AccountCostDto> findPageAccountCost(AccountCostDto accountCostDto, PageQuery pageQuery);

    /**
     * 条件查询开销表信息
     *
     * @param accountCostDto 查询条件
     * @return 开销表信息
     */
    AccountCostDto findAccountCost(AccountCostDto accountCostDto);

    /**
     * 删除开销表信息
     *
     * @param idList 主键
     */
    void deleteAccountCost(List<Long> idList);

    /**
     * 查询待导出的记账数据，并构建导出格式
     *
     * @param accountCostDto 查询条件
     * @return 待导出的数据集合
     */
    List<AccountCostExportEntity> findBuildListAccountsExport(AccountCostDto accountCostDto);
}
