package com.freesia.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.db.sql.SqlUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.freesia.po.BasePo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * @author Evad.Wu
 * @Description SQL相关 工具类
 * @date 2023-08-30
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USql extends SqlUtil {

    /**
     * 定义常用的 sql关键字
     */
    public static final String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 匿名函数
     * 构建SQL，通过前端查询参数自定义查询规则
     *
     * @param supplier 自定义查询规则
     * @param <PO>     映射类型
     * @return 构造SQL的Wrapper对象
     */
    public static <PO extends BasePo> Wrapper<PO> buildQueryWrapper(Supplier<Wrapper<PO>> supplier) {
        return supplier.get();
    }

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {
        if (UEmpty.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new UtilException("参数不符合规范，不能进行查询");
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字检查
     */
    public static void filterKeyword(String value) {
        if (UEmpty.isEmpty(value)) {
            return;
        }
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords) {
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) {
                throw new UtilException("参数存在SQL注入风险");
            }
        }
    }
}
