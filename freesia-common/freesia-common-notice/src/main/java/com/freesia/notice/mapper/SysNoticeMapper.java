package com.freesia.notice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.notice.FindPageSysNoticeEntity;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.po.SysNoticePo;
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
    List<SysNoticePo> findPublishedAnnouncement();

    /**
     * 查询消息公告表信息
     *
     * @param sysNoticeDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    Page<FindPageSysNoticeEntity> findPageSysNotice(@Param(value = "sysNoticeDto") SysNoticeDto sysNoticeDto, @Param(value = "page") Page<SysNoticePo> pageQuery);
}
