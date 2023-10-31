package com.freesia.repository;


import com.freesia.po.SysDictKeyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 持久层
 * @date 2023-09-08
 */
@Repository
public interface SysDictKeyRepository extends JpaRepository<SysDictKeyPo, Long> {
}
