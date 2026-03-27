package com.freesia.chain.impl;

import com.freesia.chain.handler.AbstractCheckColumnHandler;

/**
 * @author Bliss.Wu
 * @Description 检查系统列中间表 处理类
 * @date 2026-03-27
 */
public class CheckColumnMiddleHandler extends AbstractCheckColumnHandler {
    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     */
    public CheckColumnMiddleHandler() {
        super(AbstractCheckColumnHandler.PRIORITY_2);
    }

    @Override
    protected boolean response() {
        return false;
    }
}
