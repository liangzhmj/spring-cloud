package com.liangzhmj.cat.tools.spel;

import lombok.NonNull;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * SpEL表达式工具类
 * @author liangzhmj
 */
public class SpelUtils {

    private static ExpressionParser parser = new SpelExpressionParser();

    /**
     * 解析表达式
     * @param expStr
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseExpression(@NonNull String expStr, @NonNull Class<T> type){
        Expression exp = parser.parseExpression(expStr);
        return exp.getValue(type);
    }

    /**
     * 解析表达式
     * @param itemContext 包含的变量
     * @param expStr
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseExpression(EvaluationContext itemContext, @NonNull String expStr, @NonNull Class<T> type){
        Expression exp = parser.parseExpression(expStr);
        return exp.getValue(itemContext,type);
    }

    /**
     * 模板解析表达式 在#{exp}里面的是表达式内容，里面可以做表达式的各种取值还有运算，例如#param.id2+#param2.id
     * #{}外部的是字符串直接和#{exp}的结果拼接，例如str_#{#param.id+#param.id}_hehe
     * @param itemContext
     * @param expStr
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseTemplateExpression(EvaluationContext itemContext, @NonNull String expStr, @NonNull Class<T> type){
        Expression exp = parser.parseExpression(expStr,new TemplateParserContext());
        return exp.getValue(itemContext,type);
    }

}
