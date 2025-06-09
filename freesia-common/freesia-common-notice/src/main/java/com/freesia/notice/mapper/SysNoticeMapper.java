package com.freesia.notice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.notice.po.SysNoticePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description 消息公告表 持久层
 * @date 2025-06-06
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNoticePo> {

}
