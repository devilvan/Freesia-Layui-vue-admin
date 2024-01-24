package ${packageName}.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import ${packageName}.vo.${dataBaseDto.className}Vo;
import ${packageName}.service.${dataBaseDto.className}Service;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 控制器
 * @date ${date}
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${dataBaseDto.className?uncap_first}Controller")
@Tag(name = "${dataBaseDto.className}Controller", description = "${dataBaseDto.comment} 控制器")
public class ${dataBaseDto.className}Controller {
    private final ${dataBaseDto.className}Service ${dataBaseDto.className?uncap_first}Service;

    /**
    * 查询${dataBaseDto.comment}分页信息
    *
    * @param ${dataBaseDto.className?uncap_first}Vo 查询条件
    * @param pageQuery   分页条件
    * @return 形式返回
    */
    @Operation(summary = "查询${dataBaseDto.comment}分页信息")
    @GetMapping(value = "findPage${dataBaseDto.className}")
    public R<TableResult<${dataBaseDto.className}Dto>> findPage${dataBaseDto.className}(${dataBaseDto.className}Vo ${dataBaseDto.className?uncap_first}Vo, PageQuery pageQuery) {
        ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto = UCopy.copyVo2Dto(${dataBaseDto.className?uncap_first}Vo, ${dataBaseDto.className}Dto.class);
        TableResult<${dataBaseDto.className}Dto> tableResult = ${dataBaseDto.className?uncap_first}Service.findPage${dataBaseDto.className}(${dataBaseDto.className?uncap_first}Dto, pageQuery);
        return R.ok(tableResult);
    }

    /**
    * 条件查询${dataBaseDto.comment}
    *
    * @param ${dataBaseDto.className?uncap_first}Vo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询${dataBaseDto.comment}")
    @GetMapping(value = "find${dataBaseDto.className}")
    public R<${dataBaseDto.className}Dto> find${dataBaseDto.className}(${dataBaseDto.className}Vo ${dataBaseDto.className?uncap_first}Vo) {
        ${dataBaseDto.className}Dto ${dataBaseDto.className?uncap_first}Dto = UCopy.copyVo2Dto(${dataBaseDto.className?uncap_first}Vo, ${dataBaseDto.className}Dto.class);
        ${dataBaseDto.className}Dto tableResult = ${dataBaseDto.className?uncap_first}Service.find${dataBaseDto.className}(${dataBaseDto.className?uncap_first}Dto);
        return R.ok(tableResult);
    }

    /**
    * 删除${dataBaseDto.comment}
    *
    * @param id 主键
    * @return 形式返回
    */
    @Operation(summary = "删除${dataBaseDto.comment}")
    @PostMapping(value = "delete${dataBaseDto.className}")
    public R<Void> delete${dataBaseDto.className}(Long id) {
        ${dataBaseDto.className?uncap_first}Service.delete${dataBaseDto.className}(id);
        return R.ok();
    }
}
