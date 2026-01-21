package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.CommonTodoDto;
import com.freesia.po.CommonTodoPo;
import com.freesia.vo.CommonTodoVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 待办事项 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommonTodoConverter extends MapStructConverter<CommonTodoVo, CommonTodoDto, CommonTodoPo> {
}
