package com.freesia.repository;


import com.freesia.po.SysConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 持久层
 * @date 2023-08-12
 */
@Repository
public interface SysConfigRepository extends JpaRepository<SysConfigPo, Long> {
    /**
     * 根据参数键名查询
     *
     * @param configKey 参数键名
     * @return 结果
     */
    SysConfigPo findByConfigKey(String configKey);
}
