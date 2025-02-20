package com.freesia.jdbc.component;

import com.freesia.constant.AdminConstant;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.satoken.util.USecurity;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 管理系统JPA审计字段 功能配置类
 * @date 2023-09-16
 */
@Primary
@Component
public class AdminJpaAuditAware implements AuditorAware<String> {
    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            LoginUserModel loginUser = USecurity.getLoginUser();
            return Optional.ofNullable(loginUser).map(LoginUserModel::getUsername);
        } catch (Exception ignored) {
            return Optional.of(AdminConstant.SYSTEM);
        }
    }
}
