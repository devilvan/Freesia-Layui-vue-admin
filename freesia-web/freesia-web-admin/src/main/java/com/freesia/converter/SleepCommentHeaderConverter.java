package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.po.SleepCommentHeaderPo;
import com.freesia.vo.SleepCommentHeaderVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 MapStruct转换器
 * @date 2026-03-23
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SleepCommentHeaderConverter extends MapStructConverter<SleepCommentHeaderVo, SleepCommentHeaderDto, SleepCommentHeaderPo> {
}
