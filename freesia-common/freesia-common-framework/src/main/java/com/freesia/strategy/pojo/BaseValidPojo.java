package com.freesia.strategy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 校验必要参数-父类
 * @date 2024-04-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseValidPojo {
    /**
     * i18n编码
     */
    String messageCode;
    /**
     * 待校验的数据值的类型
     */
    private Class<?> dataType;
    /**
     * 待校验的字段名或字段描述
     */
    private String field;
    /**
     * 待校验的数据的值
     */
    private Object value;
}
