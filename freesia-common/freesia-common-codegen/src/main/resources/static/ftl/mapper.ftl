package ${packageName}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${packageName}.po.${dataBaseDto.className}Po;
import ${packageName}.dto.${dataBaseDto.className}Dto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 持久层
 * @date ${date}
 */
@Mapper
public interface ${dataBaseDto.className}Mapper extends BaseMapper<${dataBaseDto.className}Po> {
    /**
     * 分页查询${dataBaseDto.comment}信息
     *
     * @param ${dataBaseDto.className?uncap_first}Dto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<${dataBaseDto.className}Po> findPage(@Param(value = "dto") ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto, @Param("page") Page<${dataBaseDto.className}Po> page);

    /**
     * 查询${dataBaseDto.comment}信息
     *
     * @param ${dataBaseDto.className?uncap_first}Dto 查询条件
     * @return 分页信息
     */
    List<${dataBaseDto.className}Dto> findList(@Param(value = "dto") ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto);

    /**
     * 查询${dataBaseDto.comment}信息
     *
     * @param ${dataBaseDto.className?uncap_first}Dto 查询条件
     * @return 分页信息
     */
    ${dataBaseDto.className}Po findOne(@Param(value = "dto") ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<${dataBaseDto.className}Po> list);
}
