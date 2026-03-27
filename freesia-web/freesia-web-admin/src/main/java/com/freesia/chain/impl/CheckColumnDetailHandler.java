package com.freesia.chain.impl;

import com.freesia.chain.handler.AbstractCheckColumnHandler;

/**
 * @author Bliss.Wu
 * @Description 检查系统列明细 处理类
 * @date 2026-03-27
 */
public class CheckColumnDetailHandler extends AbstractCheckColumnHandler {
    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     */
    public CheckColumnDetailHandler() {
        super(AbstractCheckColumnHandler.PRIORITY_1);
    }

    @Override
    protected boolean response() {
        return false;
    }
}
