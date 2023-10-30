package com.freesia.component.datasource;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description Jpa审计功能配置类
 * @date 2022-08-06
 */
@Component
public class JpaAuditorAware implements AuditorAware<String> {
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Evad");
    }
}
