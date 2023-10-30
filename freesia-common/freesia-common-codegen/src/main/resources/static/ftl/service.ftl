package ${packageName}.service;

import ${packageName}.dto.${dataBaseDto.className}Dto;
import ${packageName}.po.${dataBaseDto.className}Po;

import java.util.List;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 业务逻辑接口
 * @date ${date}
 */
public interface ${dataBaseDto.className}Service {
    /**
    * 保存
    *
    * @param ${dataBaseDto.className?uncap_first}Dto 控制层处理后的数据传输对象
    * @return 保存回调对象
    */
    ${dataBaseDto.className}Dto saveUpdate(${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto);
    /**
    * 批量保存
    *
    * @param list 控制层处理后的数据传输对象集合
    * @return 保存回调对象
    */
    List<${dataBaseDto.className}Dto> saveUpdateBatch(List<${dataBaseDto.className}Dto> list);
}
