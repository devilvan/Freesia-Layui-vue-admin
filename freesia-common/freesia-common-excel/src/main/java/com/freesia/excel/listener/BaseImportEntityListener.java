package com.freesia.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.freesia.excel.pojo.BaseImportEntity;
import com.freesia.json.util.UJSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Excel导入 监听器
 * 重要：监听器不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 * @date 2024-02-22
 */
@Slf4j
public class BaseImportEntityListener<T extends BaseImportEntity> implements ReadListener<T> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    public static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    public List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 错误信息
     */
    public List<String> errorMsg = new ArrayList<>();

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context 解析上下文对象
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        log.info("解析到一条数据:{}", UJSON.toJSONString(data));
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context 解析上下文对象
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }
}
