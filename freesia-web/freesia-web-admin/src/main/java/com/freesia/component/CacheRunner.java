package com.freesia.component;

import com.freesia.constant.CacheConstant;
import com.freesia.oss.constant.OssConstant;
import com.freesia.oss.service.SysOssConfigService;
import com.freesia.properties.WebCommonProperties;
import com.freesia.service.SysConfigService;
import com.freesia.service.SysDictValueService;
import com.freesia.util.UMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description 管理模块-缓存 组件
 * @date 2023-09-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheRunner implements ApplicationRunner {
    private final WebCommonProperties webCommonProperties;
    private final SysConfigService sysConfigService;
    private final SysDictValueService sysDictValueService;
    private final SysOssConfigService sysOssConfigService;

    @Override
    public void run(ApplicationArguments args) {
        sysOssConfigService.loadSysOssConfig();
        log.info(UMessage.message("oss.load.success", OssConstant.SYS_OSS_DEFAULT_CONFIG));
        log.info(UMessage.message("oss.load.success", OssConstant.SYS_OSS_CONFIG));
        if (webCommonProperties.getInitSysConfig()) {
            sysConfigService.loadSysConfig();
            log.info(UMessage.message("config.load.success", CacheConstant.SYS_CONFIG));
        }
        if (webCommonProperties.getInitSysDict()) {
            sysDictValueService.loadSysDictValue();
            log.info(UMessage.message("dict.load.success", CacheConstant.SYS_DICT));
        }
    }
}
