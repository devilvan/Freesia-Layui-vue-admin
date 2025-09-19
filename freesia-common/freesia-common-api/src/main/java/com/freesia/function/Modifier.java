package com.freesia.function;

/**
 * @author Evad.Wu
 * @Description 修改器 函数式接口
 * <T> 入参泛型
 * @date 2025-03-18
 */
@FunctionalInterface
public interface Modifier<T> {
    /**
     * 修改器实现
     *
     * @param obj 待修改的对象
     * @return 执行修改方法后的对象
     */
    T modify(T obj);
}