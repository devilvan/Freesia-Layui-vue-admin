package com.freesia.spring.admin.config;

import cn.hutool.core.lang.UUID;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    private final AdminServerProperties adminServer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.path("/"));
        // 构建过滤链并返回
        return http.authorizeRequests((authorizeRequests) -> {
                            authorizeRequests
                                    .antMatchers(this.adminServer.path("/applications")).authenticated()
                                    .antMatchers(this.adminServer.path("/instances/**")).authenticated()
                                    .antMatchers(this.adminServer.path("/instances/**")).authenticated()
                                    .antMatchers(this.adminServer.path("/swagger-ui/**")).authenticated()
                                    .anyRequest().permitAll();
                        }
                )
                .formLogin((formLogin) -> {
                    formLogin.loginPage(this.adminServer.path("/login")).successHandler(successHandler);
                })
                .logout((logout) -> {
                    logout.logoutUrl(this.adminServer.path("/logout"));
                    logout.logoutSuccessUrl(this.adminServer.path("/login"));
                })
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe((rememberMe) -> {
                    rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(3600);
                })
                .build();
    }
}

