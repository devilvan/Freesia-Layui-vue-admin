package com.freesia.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.po.AccountReportPo;
import com.freesia.account.dto.AccountReportDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表表 持久层
 * @date 2026-02-25
 */
@Mapper
public interface AccountReportMapper extends BaseMapper<AccountReportPo> {
    /**
    * 分页查询记账报表表信息
    *
    * @param accountReportDto 查询条件
    * @param page    分页条件
    * @return 分页信息
    */
    Page<AccountReportPo> findPage(@Param(value = "dto") AccountReportDto accountReportDto, @Param("page") Page<AccountReportPo> page);

    /**
    * 查询记账报表表信息
    *
    * @param accountReportDto 查询条件
    * @return 分页信息
    */
    List<AccountReportDto> findList(@Param(value = "dto") AccountReportDto accountReportDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<AccountReportPo> list);
}
