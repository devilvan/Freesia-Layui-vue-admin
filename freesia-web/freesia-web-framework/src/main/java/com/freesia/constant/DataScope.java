package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 数据范围 枚举类
 * @date 2023-08-11
 */
@Getter
@AllArgsConstructor
public enum DataScope {
    /**
     * 全部数据权限
     */
    ALL("1", "", ""),
    /**
     * 自定义数据权限
     */
    CUSTOM("2", " #{#deptName} IN ( #{@sdss.getRoleCustom( #user.roleId )} ) ", ""),
    /**
     * 本部门数据权限
     */
    DEPT("3", " #{#deptName} = #{#user.deptId} ", ""),
    /**
     * 本部门及以下数据权限
     */
    DEPT_UNDERLING("4", " #{#deptName} IN ( #{@sdss.getDeptAndChild( #user.deptId )} )", ""),
    /**
     * 仅本人
     */
    OWN("5", " #{#userName} = #{#user.userId} ", " 1 = 0 ");
    /**
     * 编号
     */
    private final String code;
    /**
     * 语法，采用SPEL模板表达式
     */
    private final String sqlTemplate;
    /**
     * 不满足sqlTemplate则填充
     */
    private final String elseSql;

    /**
     * 根据编号获取枚举对象
     *
     * @param code 编号
     * @return 枚举对象
     */
    public static DataScope getInstanceByCode(String code) {
        DataScope[] values = DataScope.values();
        for (DataScope dataScope : values) {
            if (dataScope.code.equals(code)) {
                return dataScope;
            }
        }
        return null;
    }
}
