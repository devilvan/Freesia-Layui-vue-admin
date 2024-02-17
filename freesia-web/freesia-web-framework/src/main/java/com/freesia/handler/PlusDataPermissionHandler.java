package com.freesia.handler;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.annotation.DataColumn;
import com.freesia.annotation.DataPermission;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.DataScope;
import com.freesia.exception.ServiceException;
import com.freesia.helper.DataPermissionHelper;
import com.freesia.model.LoginUserModel;
import com.freesia.model.SysRoleModel;
import com.freesia.util.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description Mybatis plus数据权限过滤 处理类
 * @date 2023-09-04
 */
@Slf4j
public class PlusDataPermissionHandler {
    /**
     * 方法或类(名称) 与 注解的映射关系缓存
     */
    private final Map<String, DataPermission> dataPermissionCacheMap = new ConcurrentHashMap<>();
    /**
     * 无效注解方法缓存用于快速返回
     */
    private final Set<String> invalidCacheSet = new ConcurrentHashSet<>();
    /**
     * spel 解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParserContext parserContext = new TemplateParserContext();
    /**
     * bean解析器 用于处理 spel 表达式中对 bean 的调用
     */
    private final BeanResolver beanResolver = new BeanFactoryResolver(USpring.getBeanFactory());

    public Expression getSqlSegment(Expression where, String mappedStatementId, boolean isSelect) {
        DataColumn[] dataColumns = findAnnotation(mappedStatementId);
        // 如果无需数据权限处理则直接放行
        if (ArrayUtil.isEmpty(dataColumns)) {
            invalidCacheSet.add(mappedStatementId);
            return where;
        }
        // 从Security上下文中获取当前操作的用户
        LoginUserModel loginUserModel = DataPermissionHelper.getVariable(AdminConstant.USER);
        if (ObjectUtil.isNull(loginUserModel)) {
            loginUserModel = USecurity.getLoginUser();
            DataPermissionHelper.setVariable(AdminConstant.USER, loginUserModel);
        }
        // 如果是超级管理员，则不过滤数据
        if (ObjectUtil.isNotNull(loginUserModel) && USecurity.isAdmin(loginUserModel.getUserId())) {
            return where;
        }
        String dataFilterSql = buildDataFilter(dataColumns, isSelect);
        if (UString.isBlank(dataFilterSql)) {
            return where;
        }
        try {
            Expression expression = CCJSqlParserUtil.parseExpression(dataFilterSql);
            // 数据权限使用单独的括号，防止与其他条件冲突
            Parenthesis parenthesis = new Parenthesis(expression);
            if (ObjectUtil.isNotNull(where)) {
                return new AndExpression(where, parenthesis);
            } else {
                return parenthesis;
            }
        } catch (JSQLParserException e) {
            throw new ServiceException("数据权限解析异常 => " + e.getMessage());
        }
    }

    /**
     * 构造数据过滤SQL
     *
     * @param dataColumns 数据权限列
     * @param isSelect    是否查询语句
     * @return 构造的SQL
     */
    private String buildDataFilter(DataColumn[] dataColumns, boolean isSelect) {
        // 更新或删除需满足所有条件
        String joinStr = isSelect ? " OR " : " AND ";
        LoginUserModel user = DataPermissionHelper.getVariable(AdminConstant.USER);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(beanResolver);
        DataPermissionHelper.getContext().forEach(context::setVariable);
        Set<String> conditions = new HashSet<>();
        List<SysRoleModel> roles = user.getRoles();
        for (SysRoleModel role : roles) {
            user.setRoleId(role.getId());
            // 获取角色权限泛型
            DataScope dataScope = DataScope.getInstanceByCode(role.getDataScope());
            if (ObjectUtil.isNull(dataScope)) {
                throw new ServiceException("角色数据范围异常 => " + role.getDataScope());
            }
            // 全部数据权限则无需过滤
            if (dataScope.equals(DataScope.ALL)) {
                return "";
            }
            boolean isSuccess = false;
            for (DataColumn dataColumn : dataColumns) {
                if (dataColumn.key().length != dataColumn.value().length) {
                    throw new ServiceException("角色数据范围异常 => key与value长度不匹配");
                }
                // 不包含key则不处理
                String[] arr = Arrays.stream(dataColumn.key()).map(key -> "#" + key).toArray(String[]::new);
                if (!UString.containsAny(dataScope.getSqlTemplate(), arr)) {
                    continue;
                }
                // 设置注解变量key为表达式变量，value为变量值
                for (int i = 0, len = dataColumn.key().length; i < len; i++) {
                    context.setVariable(dataColumn.key()[i], dataColumn.value()[i]);
                }
                // 解析SQL模板并填充
                String sql = parser.parseExpression(dataScope.getSqlTemplate(), parserContext).getValue(context, String.class);
                conditions.add(joinStr + sql);
                isSuccess = true;
            }
            // 未处理成功则填充默认方案
            if (!isSuccess && UString.isNotBlank(dataScope.getElseSql())) {
                conditions.add(joinStr + dataScope.getElseSql());
            }
        }
        if (UEmpty.isNotEmpty(conditions)) {
            String sql = UStream.join(conditions, Function.identity(), "");
            return sql.substring(joinStr.length());
        }
        return "";
    }


    /**
     * 判断mapper方法是否为{@link DataPermission} 标记
     *
     * @param mappedStatementId mapper方法的名称
     * @return 数据权限列
     */
    private DataColumn[] findAnnotation(String mappedStatementId) {
        StringBuilder sb = new StringBuilder(mappedStatementId);
        int index = sb.lastIndexOf(".");
        // 类路径
        String clzName = sb.substring(0, index);
        // 方法名
        String methodName = sb.substring(index + 1, sb.length());
        Class<?> clz = ClassUtil.loadClass(clzName);
        // 获取类中名为method方法
        List<Method> methodList = Arrays.stream(ClassUtil.getDeclaredMethods(clz))
                .filter(method -> method.getName().equals(methodName))
                .collect(Collectors.toList());
        DataPermission dataPermission;
        // 获取方法注解
        for (Method method : methodList) {
            // 缓存中获取
            dataPermission = dataPermissionCacheMap.get(mappedStatementId);
            if (ObjectUtil.isNotNull(dataPermission)) {
                return dataPermission.value();
            }
            if (AnnotationUtil.hasAnnotation(method, DataPermission.class)) {
                dataPermission = AnnotationUtil.getAnnotation(method, DataPermission.class);
                dataPermissionCacheMap.put(mappedStatementId, dataPermission);
                return dataPermission.value();
            }
        }
        dataPermission = dataPermissionCacheMap.get(clz.getName());
        if (ObjectUtil.isNotNull(dataPermission)) {
            return dataPermission.value();
        }
        // 获取类注解
        if (AnnotationUtil.hasAnnotation(clz, DataPermission.class)) {
            dataPermission = AnnotationUtil.getAnnotation(clz, DataPermission.class);
            dataPermissionCacheMap.put(clz.getName(), dataPermission);
            return dataPermission.value();
        }
        return null;
    }

    /**
     * 是否为无效方法 无数据权限
     *
     * @param mappedStatementId mapper方法名称
     * @return flag
     */
    public boolean isInvalid(String mappedStatementId) {
        return invalidCacheSet.contains(mappedStatementId);
    }

}
