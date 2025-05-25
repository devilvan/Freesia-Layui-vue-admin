package com.freesia.filter;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 合并FastJSON值过滤器 过滤器
 * @date 2025-05-25
 */
public class CombinedValueFilter implements ValueFilter {
    private final List<ValueFilter> filters = new ArrayList<>();

    public CombinedValueFilter addFilter(ValueFilter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public Object process(Object object, String name, Object value) {
        Object result = value;
        for (ValueFilter filter : filters) {
            result = filter.process(object, name, result);
        }
        return result;
    }
}