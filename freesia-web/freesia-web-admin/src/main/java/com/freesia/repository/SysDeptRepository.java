package com.freesia.repository;


import com.freesia.po.SysDeptPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
* @author Evad.Wu
* @Description 部门信息表 持久层
* @date 2023-08-12
*/
@Repository
public interface SysDeptRepository extends JpaRepository<SysDeptPo, Long> {
}
