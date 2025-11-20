package com.freesia.repository;


import com.freesia.po.CommonTodoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 待办事项表 持久层
 * @date 2025-11-20
 */
@Repository
public interface CommonTodoRepository extends JpaRepository<CommonTodoPo, Long> {
}
