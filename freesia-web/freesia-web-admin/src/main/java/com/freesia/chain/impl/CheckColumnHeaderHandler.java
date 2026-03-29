package com.freesia.chain.impl;

import com.freesia.chain.handler.AbstractCheckColumnHandler;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.service.SysColumnHeaderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Bliss.Wu
 * @Description 检查系统列头 处理类
 * @date 2026-03-27
 */
@Component
public class CheckColumnHeaderHandler extends AbstractCheckColumnHandler {
    @Resource
    private SysColumnHeaderService sysColumnHeaderService;

    /**
     * 每个责任节点都要说明下自己能处理哪些需求
     */
    public CheckColumnHeaderHandler() {
        super(AbstractCheckColumnHandler.PRIORITY_1);
    }

    @Override
    protected void response(SysColumnHeaderDto sysColumnHeaderDto) {
        // 根据组件名查询列头是否存在，无则新增
        SysColumnHeaderDto one = sysColumnHeaderService.findOne(sysColumnHeaderDto);
        if (one == null) {
            sysColumnHeaderService.saveUpdate(sysColumnHeaderDto);
        }
    }
}
