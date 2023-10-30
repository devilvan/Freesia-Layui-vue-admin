package ${packageName}.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${packageName}.po.${dataBaseDto.className}Po;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 持久层
 * @date ${date}
 */
@Mapper
public interface ${dataBaseDto.className}Mapper extends BaseMapper<${dataBaseDto.className}Po> {

}
