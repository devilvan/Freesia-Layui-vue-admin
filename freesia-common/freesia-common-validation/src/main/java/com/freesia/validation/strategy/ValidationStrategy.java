package com.freesia.validation.strategy;


import com.freesia.validation.pojo.BaseValidPojo;

/**
 * @author Evad.Wu
 * @Description 数据校验-策略
 * @date 2024-04-26
 */
public interface ValidationStrategy<T extends BaseValidPojo> {
    /**
     * 数据校验注解的校验逻辑
     *
     * @param pojo 校验过程必要参数
     * @return 校验后的提示
     */
    String valid(T pojo);
}
