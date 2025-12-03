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
        List<${dataBaseDto.className}Po> ${dataBaseDto.className?uncap_first}PoList = UCopy.fullCopyList(list, ${dataBaseDto.className}Po.class);
        return UCopy.fullCopyList(${dataBaseDto.className?uncap_first}Repository.saveAllAndFlush(${dataBaseDto.className?uncap_first}PoList), ${dataBaseDto.className}Dto.class);
    }

    @Override
    public TableResult<${dataBaseDto.className}Dto> findPage${dataBaseDto.className}(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto, PageQuery pageQuery) {
        LambdaQueryWrapper<${dataBaseDto.className}Po> wrapper = new LambdaQueryWrapper<${dataBaseDto.className}Po>()
                .eq(${dataBaseDto.className}Po::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(${dataBaseDto.className?uncap_first}Dto.getId()), ${dataBaseDto.className}Po::getId, ${dataBaseDto.className?uncap_first}Dto.getId());
        Page<${dataBaseDto.className}Po> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, ${dataBaseDto.className}Dto.class));
    }

    @Override
    public ${dataBaseDto.className}Dto find${dataBaseDto.className}(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto) {
        LambdaQueryWrapper<${dataBaseDto.className}Po> wrapper = new LambdaQueryWrapper<${dataBaseDto.className}Po>()
            .eq(${dataBaseDto.className}Po::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(${dataBaseDto.className?uncap_first}Dto.getId()), ${dataBaseDto.className}Po::getId, ${dataBaseDto.className?uncap_first}Dto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), ${dataBaseDto.className}Dto.class);
    }

    @Override
    public List<${dataBaseDto.className}Dto> findList${dataBaseDto.className}(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto) {
        LambdaQueryWrapper<${dataBaseDto.className}Po> wrapper = new LambdaQueryWrapper<${dataBaseDto.className}Po>()
            .eq(${dataBaseDto.className}Po::getLogicDel, FlagConstant.DISABLED);
        return UCopy.fullCopyList(list(wrapper), ${dataBaseDto.className}Dto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete${dataBaseDto.className}(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
