package com.freesia.repository;


import com.freesia.po.SysThirdpartyAuthPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 持久层
 * @date 2026-03-13
 */
@Repository
public interface SysThirdpartyAuthRepository extends JpaRepository<SysThirdpartyAuthPo, Long> {
}
