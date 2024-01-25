package ${packageName}.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import ${packageName}.dto.${dataBaseDto.className}Dto;

import java.util.List;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 业务逻辑接口
 * @date ${date}
 */
public interface ${dataBaseDto.className}Service {
    /**
     * 保存${dataBaseDto.comment}信息
     *
     * @param ${dataBaseDto.className?uncap_first}Dto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    ${dataBaseDto.className}Dto saveUpdate(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto);

    /**
     * 批量保存${dataBaseDto.comment}信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<${dataBaseDto.className}Dto> saveUpdateBatch(List<${dataBaseDto.className}Dto> list);

    /**
     * 查询${dataBaseDto.comment}信息
     *
     * @param ${dataBaseDto.className?uncap_first}Dto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<${dataBaseDto.className}Dto> findPage${dataBaseDto.className}(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto, PageQuery pageQuery);

    /**
     * 条件查询${dataBaseDto.comment}信息
     *
     * @param ${dataBaseDto.className?uncap_first}Dto 查询条件
     * @return ${dataBaseDto.comment}信息
     */
    ${dataBaseDto.className}Dto find${dataBaseDto.className}(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto);

    /**
     * 删除${dataBaseDto.comment}信息
     *
     * @param id 主键
     */
    void delete${dataBaseDto.className}(Long id);
}
