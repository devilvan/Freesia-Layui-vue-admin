package com.freesia.chain.handler;

import com.freesia.dto.SysColumnHeaderDto;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Bliss.Wu
 * @Description 检查列 处理类
 * @date 2026-03-27
 */
@Slf4j
public abstract class AbstractCheckColumnHandler {
    /**
     * 检查系统列明细
     */
    public final static int PRIORITY_1 = 1;
    /**
     * 检查系统列中间表
     */
    public final static int PRIORITY_2 = 2;
    /**
     * 检查系统列头
     */
    public final static int PRIORITY_3 = 3;

    /**
     * 能处理的级别
     */
    private final int priority;

    /**
     * 责任传递
     */
    private AbstractCheckColumnHandler nextHandler;

    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     *
     * @param priority 优先级
     */
    public AbstractCheckColumnHandler(int priority) {
        this.priority = priority;
    }

    /**
     * 处理需求
     *
     * @param priority 需求优先级
     */
    public final void handle(int priority, SysColumnHeaderDto sysColumnHeaderDto) {
        if (priority == this.priority) {
            response(sysColumnHeaderDto);
        } else {
            // 后续有环节则继续推进请求
            if (this.nextHandler != null) {
                this.nextHandler.handle(priority, sysColumnHeaderDto);
            } else {
                log.info("没有对应的处理器");
            }
        }
    }

    /**
     * 传递给下个处理人
     *
     * @param handler 下个处理人
     */
    public void setNext(AbstractCheckColumnHandler handler) {
        this.nextHandler = handler;
    }

    /**
     * 处理节点的响应
     */
    protected abstract void response(SysColumnHeaderDto sysColumnHeaderDto);
}
