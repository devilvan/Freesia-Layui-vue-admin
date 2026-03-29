package com.freesia.chain.impl;

import com.freesia.chain.handler.AbstractCheckColumnHandler;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.service.SysColumnMiddleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Bliss.Wu
 * @Description 检查系统列中间表 处理类
 * @date 2026-03-27
 */
@Component
public class CheckColumnMiddleHandler extends AbstractCheckColumnHandler {
    @Resource
    private SysColumnMiddleService sysColumnMiddleService;

    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     */
    public CheckColumnMiddleHandler() {
        super(AbstractCheckColumnHandler.PRIORITY_2);
    }

    @Override
    protected void response(SysColumnHeaderDto sysColumnHeaderDto) {

    }
}
