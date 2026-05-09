package com.freesia.account.service;

import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.entity.FindPageAccountReportEntity;
import com.freesia.account.vo.AccountReportVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 记账报表表 业务逻辑接口
 * @date 2026-02-25
 */
public interface AccountReportService {
    /**
     * 保存记账报表表信息
     *
     * @param accountReportDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    AccountReportDto saveUpdate(AccountReportDto accountReportDto);

    /**
     * 批量保存记账报表表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<AccountReportDto> saveUpdateBatch(List<AccountReportDto> list);

    /**
     * 查询记账报表表信息
     *
     * @param accountReportDto 查询条件
     * @param pageQuery        分页条件
     * @return 分页信息
     */
    TableResult<AccountReportDto> findPage(AccountReportDto accountReportDto, PageQuery pageQuery);

    /**
     * 自定义分页查询记账报表表信息
     *
     * @param accountReportDto 查询条件
     * @param pageQuery        分页条件
     * @return 分页信息
     */
    TableResult<FindPageAccountReportEntity> findPageAccountReport(AccountReportDto accountReportDto, PageQuery pageQuery);

    /**
     * 条件查询记账报表表信息
     *
     * @param accountReportDto 查询条件
     * @return 记账报表表信息
     */
    AccountReportDto findOne(AccountReportDto accountReportDto);

    /**
     * 条件查询记账报表表信息
     *
     * @param accountReportDto 查询条件
     * @return 记账报表表信息
     */
    List<AccountReportDto> findList(AccountReportDto accountReportDto);

    /**
     * 删除记账报表表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);

    /**
     * 查询是否存在相同的报表记录
     *
     * @param accountReportDto 查询入参
     * @return 是否存在
     */
    Boolean findExist(AccountReportDto accountReportDto);

    /**
     * 根据ID集合修改是否需要重新计算标志
     *
     * @param idSet ID集合
     */
    void changeRecalculateFlag(Set<Long> idSet);

    /**
     * 查询在时间范围内的报表数据
     *
     * @param accountReportDto 查询入参
     * @return 结果集
     */
    List<AccountReportDto> findBetweenBillingTime(AccountReportDto accountReportDto);

    /**
     * 更新预算金额
     *
     * @param accountReportVo 更新入参
     */
    void updateBudgetAmount(AccountReportVo accountReportVo);
}
