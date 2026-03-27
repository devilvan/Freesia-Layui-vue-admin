package com.freesia.chain.impl;

import com.freesia.chain.handler.AbstractCheckColumnHandler;

/**
 * @author Bliss.Wu
 * @Description 检查系统列头 处理类
 * @date 2026-03-27
 */
public class CheckColumnHeaderHandler extends AbstractCheckColumnHandler {
    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     */
    public CheckColumnHeaderHandler() {
        super(AbstractCheckColumnHandler.PRIORITY_3);
    }

    @Override
    protected boolean response() {
        return false;
    }
}
