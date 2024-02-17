package com.freesia.component;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.freesia.constant.AuditConstant;
import com.freesia.model.LoginUserModel;
import com.freesia.util.USecurity;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description Mybatis-Plus 管理模块审计字段初始化组件
 * @date 2023-09-18
 */
@Primary
@Component
public class MybatisAdminAuditComponent implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        defaultInsertFill(metaObject);
        LoginUserModel loginUser = USecurity.getLoginUser();
        if (ObjectUtil.isNotNull(loginUser)) {
            if (metaObject.hasSetter(AuditConstant.CREATOR)) {
                this.setFieldValByName(AuditConstant.CREATOR, loginUser.getUsername(), metaObject);
            }
            if (metaObject.hasSetter(AuditConstant.MODIFIER)) {
                this.setFieldValByName(AuditConstant.MODIFIER, loginUser.getUsername(), metaObject);
            }
        }
    }

    private void defaultInsertFill(MetaObject metaObject) {
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
        defaultUpdateFill(metaObject);
        LoginUserModel loginUser = USecurity.getLoginUser();
        if (ObjectUtil.isNotNull(loginUser)) {
            if (metaObject.hasSetter(AuditConstant.MODIFIER)) {
                this.setFieldValByName(AuditConstant.MODIFIER, loginUser.getUsername(), metaObject);
            }
        }
    }

    private void defaultUpdateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(AuditConstant.MODIFIER)) {
            this.setFieldValByName(AuditConstant.MODIFIER, "Evad", metaObject);
        }
        if (metaObject.hasSetter(AuditConstant.MODIFY_TIME)) {
            this.setFieldValByName(AuditConstant.MODIFY_TIME, new Date(), metaObject);
        }
    }
}
