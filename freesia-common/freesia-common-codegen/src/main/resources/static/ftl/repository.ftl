package ${packageName}.repository;


import ${packageName}.po.${dataBaseDto.className}Po;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 持久层
 * @date ${date}
 */
@Repository
public interface ${dataBaseDto.className}Repository extends JpaRepository<${dataBaseDto.className}Po, Long> {
}
