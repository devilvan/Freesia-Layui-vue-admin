package com.freesia.runner;

import com.freesia.constant.CacheConstant;
import com.freesia.crypt.service.CryptService;
import com.freesia.properties.WebCommonProperties;
import com.freesia.service.*;
import com.freesia.util.UMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description 应用启动后初始化 初始化器
 * @date 2023-09-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FreesiaApplicationRunner implements ApplicationRunner, Ordered {
    private final WebCommonProperties webCommonProperties;
    private final SysConfigService sysConfigService;
    private final SysDictValueService sysDictValueService;
    private final SysOssConfigService sysOssConfigService;
    private final SysOssService sysOssService;
    private final CryptService cryptService;
    private final SysDeptService sysDeptService;

    @Override
    public void run(ApplicationArguments args) {
        sysOssConfigService.loadSysOssConfig();
        sysOssService.initDeleteTempFile();
        log.info(UMessage.message("oss.load.success", CacheConstant.SYS_OSS_DEFAULT_CONFIG));
        log.info(UMessage.message("oss.load.success", CacheConstant.SYS_OSS_CONFIG));
        if (webCommonProperties.getInitSysConfig()) {
            sysConfigService.loadSysConfig();
            log.info(UMessage.message("config.load.success", CacheConstant.SYS_CONFIG));
        }
        if (webCommonProperties.getInitSysDict()) {
            sysDictValueService.loadSysDictValue();
            log.info(UMessage.message("dict.load.success", CacheConstant.SYS_DICT));
        }
        if (webCommonProperties.getInitSecretKey()) {
            cryptService.initRsa();
            log.info(UMessage.message("crypt.init.success"));
        }
        sysDeptService.initDefaultDept();
        log.info(UMessage.message("dept.init.success"));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
