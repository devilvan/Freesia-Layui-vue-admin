package com.freesia.repository;


import com.freesia.po.SysUserPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 用户信息表 持久层
 * @date 2023-08-12
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUserPo, Long> {
    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @param logicDel 是否被删除
     * @return 用户信息
     */
    SysUserPo findByUserNameAndLogicDel(String userName, boolean logicDel);

    /**
     * 根据邮箱查询用户信息
     *
     * @param email    邮箱
     * @param logicDel 是否被删除
     * @return 用户信息
     */
    SysUserPo findByEmailAndLogicDel(String email, boolean logicDel);
}
