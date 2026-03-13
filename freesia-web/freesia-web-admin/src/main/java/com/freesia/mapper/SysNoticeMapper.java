package com.freesia.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.entity.FindPageSysNoticeEntity;
import com.freesia.dto.SysNoticeDto;
import com.freesia.entity.FindPublishedAnnouncementEntity;
import com.freesia.po.SysNoticePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 消息公告表 持久层
 * @date 2025-06-06
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNoticePo> {
    /**
     * 查询已发布的公告
     *
     * @return 公告集合
     */
    List<FindPublishedAnnouncementEntity> findPublishedAnnouncement();

    /**
     * 查询消息公告表信息
     *
     * @param sysNoticeDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    Page<FindPageSysNoticeEntity> findPageSysNotice(@Param(value = "sysNoticeDto") SysNoticeDto sysNoticeDto, @Param(value = "page") Page<SysNoticePo> pageQuery);

    /**
     * 查询未读消息/公告数量
     *
     * @param sysNoticeDto 查询条件
     * @return 未读消息/公告数量
     */
    Integer findUnreadCount(@Param(value = "sysNoticeDto") SysNoticeDto sysNoticeDto);

    /**
     * 查询消息公告表集合
     *
     * @param sysNoticeDto 查询条件
     * @return 消息公告表集合
     */
    List<FindPageSysNoticeEntity> findListSysNotice(@Param(value = "sysNoticeDto") SysNoticeDto sysNoticeDto);

    /**
     * 根据用户ID查询是否有未生成的公告
     *
     * @param sysNoticeDto 查询参数
     * @return 是否生成
     */
    List<Long> findExistsAnnouncement(@Param(value = "sysNoticeDto") SysNoticeDto sysNoticeDto);
}
