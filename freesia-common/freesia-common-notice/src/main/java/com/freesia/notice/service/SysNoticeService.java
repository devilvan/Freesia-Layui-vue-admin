package com.freesia.notice.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.notice.dto.SysNoticeDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 消息公告表 业务逻辑接口
 * @date 2025-06-06
 */
public interface SysNoticeService {
    /**
     * 保存消息公告表信息
     *
     * @param sysNoticeDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysNoticeDto saveUpdate(SysNoticeDto sysNoticeDto);

    /**
     * 批量保存消息公告表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysNoticeDto> saveUpdateBatch(List<SysNoticeDto> list);

    /**
     * 查询消息公告表信息
     *
     * @param sysNoticeDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysNoticeDto> findPageSysNotice(SysNoticeDto sysNoticeDto, PageQuery pageQuery);

    /**
     * 条件查询消息公告表信息
     *
     * @param sysNoticeDto 查询条件
     * @return 消息公告表信息
     */
    SysNoticeDto findSysNotice(SysNoticeDto sysNoticeDto);

    /**
     * 删除消息公告表信息
     *
     * @param idList 主键
     */
    void deleteSysNotice(List<Long> idList);

    /**
     * 查询已发布的公告
     *
     * @return 公告集合
     */
    List<SysNoticeDto> findPublishedAnnouncement();
}
