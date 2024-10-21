package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * @author Evad.Wu
 * @Description Spring EL 工具类
 * @date 2024-10-21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USpEl {
    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final StandardEvaluationContext context = new StandardEvaluationContext();

    public static <T> T parse(String str) {
        return parse(str, null);
    }

    public static <T> T parse(String str, Class<T> clz) {
        return parse(str, clz, null);
    }

    public static <T> T parse(String str, Class<T> clz, Map<String, Object> variables) {
        Expression expression = parser.parseExpression(str, ParserContext.TEMPLATE_EXPRESSION);
        if (UEmpty.isNotEmpty(variables)) {
            context.setVariables(variables);
        }
        return expression.getValue(context, clz);
    }
}
