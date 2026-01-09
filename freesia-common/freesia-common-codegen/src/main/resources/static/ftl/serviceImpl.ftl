package ${packageName}.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import ${packageName}.dto.${dataBaseDto.className}Dto;
import ${packageName}.po.${dataBaseDto.className}Po;
import ${packageName}.service.${dataBaseDto.className}Service;
import ${packageName}.mapper.${dataBaseDto.className}Mapper;
import ${packageName}.repository.${dataBaseDto.className}Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
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
public class ${dataBaseDto.className}ServiceImpl extends BaseServiceImpl<${dataBaseDto.className}Mapper, ${dataBaseDto.className}Po, ${dataBaseDto.className}Dto> implements ${dataBaseDto.className}Service {
    private final ${dataBaseDto.className}Repository ${dataBaseDto.className?uncap_first}Repository;

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
    protected Wrapper<WorldClockSunriseSunsetPo> buildQueryWrapper(@NonNull ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto) {
        return new LambdaQueryWrapper<WorldClockSunriseSunsetPo>()
            .eq(${dataBaseDto.className}Po::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(${dataBaseDto.className?uncap_first}Dto.getId()), ${dataBaseDto.className}Po::getId, ${dataBaseDto.className?uncap_first}Dto.getId());
    }
}
