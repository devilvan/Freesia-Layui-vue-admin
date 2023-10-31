package com.freesia.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.freesia.constant.AuditConstant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description Mybatis-Plus 审计字段初始化组件
 * @date 2022-07-10
 */
@Component
public class MybatisAuditComponent implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(AuditConstant.CREATOR)) {
            this.setFieldValByName(AuditConstant.CREATOR, "Evad", metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.CREATE_TIME)) {
            this.setFieldValByName(AuditConstant.CREATE_TIME, new Date(), metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.MODIFIER)) {
            this.setFieldValByName(AuditConstant.MODIFIER, "Evad", metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.MODIFY_TIME)) {
            this.setFieldValByName(AuditConstant.MODIFY_TIME, new Date(), metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.LOGIC_DEL)) {
            this.setFieldValByName(AuditConstant.LOGIC_DEL, false, metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.REC_VER)) {
            this.setFieldValByName(AuditConstant.REC_VER, 1L, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(AuditConstant.MODIFIER)) {
            this.setFieldValByName(AuditConstant.MODIFIER, "Evad", metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.MODIFY_TIME)) {
            this.setFieldValByName(AuditConstant.MODIFY_TIME, new Date(), metaObject);
        }
    }
}
