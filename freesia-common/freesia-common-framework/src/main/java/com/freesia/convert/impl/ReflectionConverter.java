package com.freesia.convert.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.convert.Converter;
import com.freesia.exception.ServiceException;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 泛型转换器 实现
 * @date 2026-01-05
 */
@AllArgsConstructor
public class ReflectionConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET> {
    private final Class<SOURCE> sourceClass;
    private final Class<TARGET> targetClass;

    @Override
    public TARGET convert(SOURCE source) {
        if (source == null) {
            return null;
        }
        try {
            TARGET target = targetClass.getConstructor().newInstance();
            UCopy.fullCopy(source, target);
            return target;
        } catch (Exception e) {
            throw new ServiceException(UMessage.message("convert.object.failed", new Object[]{sourceClass.getName(), targetClass.getName(), e.toString()}));
        }
    }

    @Override
    public List<TARGET> convertBatch(List<SOURCE> sourceList) {
        if (UEmpty.isEmpty(sourceList)) {
            return null;
        }
        try {
            return UCopy.fullCopyList(sourceList, targetClass);
        } catch (Exception e) {
            throw new ServiceException(UMessage.message("convert.object.failed", new Object[]{sourceClass.getName(), targetClass.getName(), e.toString()}));
        }
    }

    @Override
    public Page<TARGET> convertPage(Page<SOURCE> sourcePage) {
        if (sourcePage == null || UEmpty.isEmpty(sourcePage.getRecords())) {
            return null;
        }
        Page<TARGET> pageDto = null;
        try {
            pageDto = new Page<>();
            List<TARGET> targetList = convertBatch(sourcePage.getRecords());
            pageDto.setRecords(targetList);
            pageDto.setSize(sourcePage.getSize());
            pageDto.setCurrent(sourcePage.getCurrent());
            pageDto.setTotal(sourcePage.getTotal());
            pageDto.setPages(sourcePage.getPages());
        } catch (Exception e) {
            throw new ServiceException(UMessage.message("convert.object.failed", new Object[]{sourceClass.getName(), targetClass.getName(), e.toString()}));
        }
        return pageDto;
    }
}
