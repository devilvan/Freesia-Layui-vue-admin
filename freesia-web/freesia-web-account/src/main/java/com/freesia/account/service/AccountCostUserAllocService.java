package com.freesia.account.service;

import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.account.dto.FindListSysUserByIdDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 业务逻辑接口
 * @date 2025-10-03
 */
public interface AccountCostUserAllocService {
    /**
     * 保存费用分摊表信息
     *
     * @param accountCostUserAllocDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountCostUserAllocDto saveUpdate(AccountCostUserAllocDto accountCostUserAllocDto);

    /**
     * 批量保存费用分摊表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountCostUserAllocDto> saveUpdateBatch(List<AccountCostUserAllocDto> list);

    /**
     * 查询费用分摊表信息
     *
     * @param accountCostUserAllocDto 查询条件
     * @param pageQuery               分页条件
     * @return 分页信息
     */
    TableResult<AccountCostUserAllocDto> findPage(AccountCostUserAllocDto accountCostUserAllocDto, PageQuery pageQuery);

    /**
     * 条件查询费用分摊表信息
     *
     * @param accountCostUserAllocDto 查询条件
     * @return 费用分摊表信息
     */
    AccountCostUserAllocDto findOne(AccountCostUserAllocDto accountCostUserAllocDto);

    /**
     * 删除费用分摊表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);

    /**
     * 新增费用分摊-根据分摊用户ID查询用户信息
     *
     * @param idList 用户ID集合
     * @return 用户信息
     */
    List<FindListSysUserByIdDto> findListSysUserById(List<Long> idList);

    /**
     * 修改费用分摊-根据记账ID查询分摊信息
     *
     * @param accountCostUserAllocDto 查询条件
     * @return 分摊信息
     */
    List<FindListSysUserByIdDto> findListAllocByCostId(AccountCostUserAllocDto accountCostUserAllocDto);

    /**
     * 根据记账ID删除
     *
     * @param costIdList 记账ID
     */
    void deleteAccountCostUserAllocByCostId(List<Long> costIdList);
}
