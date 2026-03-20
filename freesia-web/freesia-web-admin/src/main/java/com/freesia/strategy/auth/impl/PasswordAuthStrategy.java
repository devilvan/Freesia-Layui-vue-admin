package com.freesia.strategy.auth.impl;

import com.freesia.strategy.auth.AuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Bliss.Wu
 * @Description 密码授权 策略
 * @date 2026-03-12
 */
@Slf4j
@Component(value = "password" + AuthStrategy.NAME)
public class PasswordAuthStrategy implements AuthStrategy {
}
