package com.freesia.repository;


import com.freesia.po.SysColumnDetailPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 持久层
 * @date 2026-03-17
 */
@Repository
public interface SysColumnDetailRepository extends JpaRepository<SysColumnDetailPo, Long> {
}
