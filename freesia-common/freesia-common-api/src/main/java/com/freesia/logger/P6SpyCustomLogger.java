package com.freesia.logger;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import lombok.NoArgsConstructor;

/**
 * @author Bliss.Wu
 * @Description 自定义P6Spy日志格式化器
 * @date 2026-02-27
 */
@NoArgsConstructor
public class P6SpyCustomLogger implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return StringUtils.isNotBlank(sql) ? " Consume Time：" + elapsed + " ms " + now + "\n Execute SQL：" + sql.replaceAll("[\\s]+", " ") + "\n" : "";
    }
}