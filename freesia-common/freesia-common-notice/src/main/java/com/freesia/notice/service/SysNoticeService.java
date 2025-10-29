package com.freesia.notice.service;

import com.freesia.notice.dto.MarkReadDto;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.entity.FindPageSysNoticeEntity;
import com.freesia.notice.entity.FindPublishedAnnouncementEntity;
import com.freesia.notice.vo.SysNoticeVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

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
    TableResult<FindPageSysNoticeEntity> findPageSysNotice(SysNoticeDto sysNoticeDto, PageQuery pageQuery);

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
    List<FindPublishedAnnouncementEntity> findPublishedAnnouncement();

    /**
     * 查询未读消息/公告数量
     *
     * @param sysNoticeVo 查询条件
     * @return 未读消息/公告数量
     */
    Integer findUnreadCount(SysNoticeVo sysNoticeVo);

    /**
     * 标记已读
     *
     * @param markReadDto 入参
     */
    Integer markRead(MarkReadDto markReadDto);

    /**
     * 查询消息公告表集合
     *
     * @param sysNoticeDto 查询条件
     * @return 消息公告表集合
     */
    List<FindPageSysNoticeEntity> findListSysNotice(SysNoticeDto sysNoticeDto);

    /**
     * 用户登录时检查是否生成用户未读的公告数据，无则生成
     *
     * @param userId 用户ID
     */
    void checkSaveAnnouncement(Long userId);
}
