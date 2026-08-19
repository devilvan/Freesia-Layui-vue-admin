package com.freesia.mail.config;

import cn.hutool.extra.mail.MailAccount;
import com.freesia.mail.properties.MailProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.Resource;

/**
 * @author Evad.Wu
 * @Description 邮件模块 配置类
 * @date 2024-10-24
 */
@AutoConfiguration
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
public class MailConfig {
    @Resource
    private MailProperties mailProperties;

    @Bean(value = "mailAccount")
    public MailAccount buildMailAccount() {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(mailProperties.getHost());
        mailAccount.setPort(mailProperties.getPort());
        mailAccount.setAuth(mailProperties.getAuth());
        mailAccount.setUser(mailProperties.getUser());
        mailAccount.setPass(mailProperties.getPass());
        mailAccount.setFrom(mailProperties.getFrom());
        mailAccount.setStarttlsEnable(mailProperties.getStarttlsEnable());
        mailAccount.setSslEnable(mailProperties.getSslEnable());
        mailAccount.setSocketFactoryPort(mailProperties.getPort());
        mailAccount.setTimeout(mailProperties.getTimeout());
        mailAccount.setConnectionTimeout(mailProperties.getConnectionTimeout());
        return mailAccount;
    }
}
