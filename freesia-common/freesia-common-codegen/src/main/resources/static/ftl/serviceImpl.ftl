package ${packageName}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${packageName}.dto.${dataBaseDto.className}Dto;
import ${packageName}.po.${dataBaseDto.className}Po;
import ${packageName}.service.${dataBaseDto.className}Service;
import ${packageName}.mapper.${dataBaseDto.className}Mapper;
import ${packageName}.repository.${dataBaseDto.className}Repository;
import org.springframework.stereotype.Service;
import com.freesia.util.UCopy;
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
public class ${dataBaseDto.className}ServiceImpl extends ServiceImpl<${dataBaseDto.className}Mapper, ${dataBaseDto.className}Po> implements ${dataBaseDto.className}Service {
    private final ${dataBaseDto.className}Repository ${dataBaseDto.className?uncap_first}Repository;

    @Override
    public ${dataBaseDto.className}Dto saveUpdate(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto) {
        ${dataBaseDto.className}Po ${dataBaseDto.className?uncap_first}Po = new ${dataBaseDto.className}Po();
        UCopy.fullCopy(${dataBaseDto.className?uncap_first}Dto, ${dataBaseDto.className?uncap_first}Po);
        ${dataBaseDto.className}Dto resultDto = new ${dataBaseDto.className}Dto();
        UCopy.fullCopy(${dataBaseDto.className?uncap_first}Repository.saveAndFlush(${dataBaseDto.className?uncap_first}Po), resultDto);
        return resultDto;
    }

    @Override
    public List<${dataBaseDto.className}Dto> saveUpdateBatch(List<${dataBaseDto.className}Dto> list) {
        List<${dataBaseDto.className}Po> ${dataBaseDto.className?uncap_first}PoList = UCopy.fullCopyCollections(list, ${dataBaseDto.className}Po.class);
        return UCopy.fullCopyCollections(${dataBaseDto.className?uncap_first}Repository.saveAllAndFlush(${dataBaseDto.className?uncap_first}PoList), ${dataBaseDto.className}Dto.class);
    }
}
