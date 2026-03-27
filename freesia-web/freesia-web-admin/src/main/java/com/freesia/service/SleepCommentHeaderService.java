package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.vo.SleepCommentHeaderVo;

import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 业务逻辑接口
 * @date 2026-03-23
 */
public interface SleepCommentHeaderService {
    /**
     * 保存睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SleepCommentHeaderDto saveUpdate(SleepCommentHeaderDto sleepCommentHeaderDto);

    /**
     * 批量保存睡眠产品评论信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SleepCommentHeaderDto> saveUpdateBatch(List<SleepCommentHeaderDto> list);

    /**
     * 查询睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SleepCommentHeaderDto> findPage(SleepCommentHeaderDto sleepCommentHeaderDto, PageQuery pageQuery);

    /**
     * 条件查询睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 查询条件
     * @return 睡眠产品评论信息
     */
    SleepCommentHeaderDto findOne(SleepCommentHeaderDto sleepCommentHeaderDto);

    /**
     * 条件查询睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 查询条件
     * @return 睡眠产品评论信息
     */
    List<SleepCommentHeaderDto> findList(SleepCommentHeaderDto sleepCommentHeaderDto);

    /**
     * 删除睡眠产品评论信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);

    void handleTrustPilot(SleepCommentHeaderVo sleepCommentHeaderVo);

    void handleReddit(SleepCommentHeaderVo sleepCommentHeaderVo);

    void handle3B(SleepCommentHeaderVo sleepCommentHeaderVo, Map<String, String> headersAsMap);

    void exportTrustPilot(SleepCommentHeaderVo sleepCommentHeaderVo);

    void export3B(SleepCommentHeaderVo sleepCommentHeaderVo);
}
