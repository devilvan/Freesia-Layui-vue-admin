package com.freesia.repository;


import com.freesia.po.SysClientPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 持久层
 * @date 2026-03-13
 */
@Repository
public interface SysClientRepository extends JpaRepository<SysClientPo, Long> {
}
