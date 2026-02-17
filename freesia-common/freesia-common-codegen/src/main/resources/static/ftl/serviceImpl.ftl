package ${packageName}.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import ${packageName}.vo.${dataBaseDto.className}Vo;
import ${packageName}.dto.${dataBaseDto.className}Dto;
import ${packageName}.po.${dataBaseDto.className}Po;
import ${packageName}.service.${dataBaseDto.className}Service;
import ${packageName}.converter.${dataBaseDto.className}Converter;
import ${packageName}.mapper.${dataBaseDto.className}Mapper;
import ${packageName}.repository.${dataBaseDto.className}Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 业务逻辑类
 * @date ${date}
 */
@Service
@RequiredArgsConstructor
public class ${dataBaseDto.className}ServiceImpl extends BaseServiceImpl<${dataBaseDto.className}Mapper, ${dataBaseDto.className}Vo, ${dataBaseDto.className}Dto, ${dataBaseDto.className}Po> implements ${dataBaseDto.className}Service {
    private final ${dataBaseDto.className}Repository ${dataBaseDto.className?uncap_first}Repository;
    private final ${dataBaseDto.className}Converter ${dataBaseDto.className?uncap_first}Converter;

    @Override
    protected JpaRepository<${dataBaseDto.className}Po, Long> getRepository() {
    return ${dataBaseDto.className?uncap_first}Repository;
    }

    @Override
    protected Class<${dataBaseDto.className}Dto> getDtoClass() {
        return ${dataBaseDto.className}Dto.class;
    }

    @Override
    protected Class<${dataBaseDto.className}Po> getPoClass() {
        return ${dataBaseDto.className}Po.class;
    }

    @Override
    protected Wrapper<${dataBaseDto.className}Po> buildQueryWrapper(@NonNull ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto) {
        return new LambdaQueryWrapper<${dataBaseDto.className}Po>()
            .eq(${dataBaseDto.className}Po::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(${dataBaseDto.className?uncap_first}Dto.getId()), ${dataBaseDto.className}Po::getId, ${dataBaseDto.className?uncap_first}Dto.getId());
    }

    @Override
    protected MapStructConverter<${dataBaseDto.className}Vo, ${dataBaseDto.className}Dto, ${dataBaseDto.className}Po> getMapStructConverter() {
        return ${dataBaseDto.className?uncap_first}Converter;
    }
}
