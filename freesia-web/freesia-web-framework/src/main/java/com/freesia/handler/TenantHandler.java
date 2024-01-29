package com.freesia.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.freesia.properties.TenantProperties;
import com.freesia.util.UEmpty;
import com.freesia.util.USecurity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户相关 处理类
 * @date 2024-01-29
 */
@NoArgsConstructor
@AllArgsConstructor
public class TenantHandler implements TenantLineHandler {
    private TenantProperties tenantProperties;

    @Override
    public Expression getTenantId() {
        Long tenantId = USecurity.getTenantId();
        return new LongValue(tenantId);
    }

    /**
     * 默认返回 false 表示所有表都需要拼多租户条件
     *
     * @param tableName 表名
     * @return false 所有表都参与租户隔离
     */
    @Override
    public boolean ignoreTable(String tableName) {
        Long tenantId = USecurity.getTenantId();
        if (UEmpty.isEmpty(tenantId)) {
            return true;
        }
        String ignoreTable = tenantProperties.getIgnoreTable();
        boolean flag;
        String[] sp = ignoreTable.split(",");
        for (String s : sp) {
            flag = s.equals(tableName);
            if (flag) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTenantIdColumn() {
        return tenantProperties.getTenantColumn();
    }

    @Override
    public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        return TenantLineHandler.super.ignoreInsert(columns, tenantIdColumn);
    }
}
