package ${packageName}.converter;

import com.freesia.convert.MapStructConverter;
import ${packageName}.dto.${dataBaseDto.className}Dto;
import ${packageName}.po.${dataBaseDto.className}Po;
import ${packageName}.vo.${dataBaseDto.className}Vo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} MapStruct转换器
 * @date ${date}
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ${dataBaseDto.className}Converter extends MapStructConverter<${dataBaseDto.className}Vo, ${dataBaseDto.className}Dto, ${dataBaseDto.className}Po> {
}
