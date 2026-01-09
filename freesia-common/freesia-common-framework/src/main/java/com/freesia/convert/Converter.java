package com.freesia.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description （转换器模式）转换器 接口
 * @date 2026-01-04
 */
public interface Converter<SOURCE, TARGET> {
    /**
     * 单个转换
     *
     * @param source 原实例
     * @return 转换后实例
     */
    TARGET convert(SOURCE source);

    /**
     * 批量转换
     *
     * @param sourceList 原实例集合
     * @return 转换后实例集合
     */
    List<TARGET> convertBatch(List<SOURCE> sourceList);

    /**
     * 分页对象转换
     *
     * @param sourcePage 源分页对象
     * @return 目标分页对象
     */
    Page<TARGET> convertPage(Page<SOURCE> sourcePage);
}
