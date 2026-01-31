package com.freesia.notice.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.po.SysNoticePo;
import com.freesia.notice.vo.SysNoticeVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 消息公告表 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysNoticeConverter extends MapStructConverter<SysNoticeVo, SysNoticeDto, SysNoticePo> {
    @Mapping(target = "announcementIdList", ignore = true)
    @Override
    SysNoticeDto convertVo2Dto(SysNoticeVo source);

    @Override
    SysNoticePo convertDto2Po(SysNoticeDto source);

    @Mapping(target = "createTimeTo", ignore = true)
    @Mapping(target = "createTimeFrom", ignore = true)
    @Mapping(target = "announcementIdList", ignore = true)
    @Override
    SysNoticeDto convertPo2Dto(SysNoticePo source);
}
