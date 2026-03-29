package com.freesia.chain.impl;

import com.freesia.chain.handler.AbstractCheckColumnHandler;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.service.SysColumnDetailService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Bliss.Wu
 * @Description 检查系统列明细 处理类
 * @date 2026-03-27
 */
@Component
public class CheckColumnDetailHandler extends AbstractCheckColumnHandler {
    @Resource
    private SysColumnDetailService sysColumnDetailService;

    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     */
    public CheckColumnDetailHandler() {
        super(AbstractCheckColumnHandler.PRIORITY_3);
    }

    @Override
    protected void response(SysColumnHeaderDto sysColumnHeaderDto) {
    }
}
