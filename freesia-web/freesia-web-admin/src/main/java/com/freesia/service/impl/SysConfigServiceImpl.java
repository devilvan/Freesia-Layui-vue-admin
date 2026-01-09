package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.*;
import com.freesia.dto.SysConfigDto;
import com.freesia.exception.CaptchaException;
import com.freesia.exception.CaptchaExpireException;
import com.freesia.exception.ConfigException;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysConfigMapper;
import com.freesia.net.util.UServlet;
import com.freesia.po.SysConfigPo;
import com.freesia.redis.util.URedis;
import com.freesia.repository.SysConfigRepository;
import com.freesia.service.SysConfigService;
import com.freesia.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 业务逻辑类
 * @date 2023-08-12
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfigPo, SysConfigDto> implements SysConfigService {
    private final SysConfigMapper sysConfigMapper;
    private final SysConfigRepository sysConfigRepository;

    @Override
    protected JpaRepository<SysConfigPo, Long> getRepository() {
        return sysConfigRepository;
    }

    @Override
    protected Class<SysConfigDto> getDtoClass() {
        return SysConfigDto.class;
    }

    @Override
    protected Class<SysConfigPo> getPoClass() {
        return SysConfigPo.class;
    }

    @Override
    protected Wrapper<SysConfigPo> buildQueryWrapper(@NonNull SysConfigDto dto) {
        return new LambdaQueryWrapper<SysConfigPo>()
                .eq(SysConfigPo::getLogicDel, FlagConstant.DISABLED)
                .like(UEmpty.isNotEmpty(dto.getConfigKey()), SysConfigPo::getConfigKey, dto.getConfigKey())
                .like(UEmpty.isNotEmpty(dto.getConfigName()), SysConfigPo::getConfigName, dto.getConfigName());
    }

    @Override
    @CachePut(cacheNames = CacheConstant.SYS_CONFIG, key = "#sysConfigDto.configKey")
    @LogRecord(module = ConfigModule.CONFIG_MANAGEMENT, subModule = ConfigModule.SubModule.SAVE_CONFIG, message = "config.save")
    public SysConfigDto saveUpdate(SysConfigDto sysConfigDto) {
        return super.saveUpdate(sysConfigDto);
    }

    @Override
    @LogRecord(module = ConfigModule.CONFIG_MANAGEMENT, subModule = ConfigModule.SubModule.SAVE_CONFIG, message = "config.save")
    public List<SysConfigDto> saveUpdateBatch(List<SysConfigDto> list) {
        return super.saveUpdateBatch(list);
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_CONFIG, key = "#configKey")
    public SysConfigDto findConfigByKey(String configKey) {
        SysConfigDto sysConfigDto = new SysConfigDto();
        sysConfigDto.setConfigKey(configKey);
        sysConfigDto = super.findOne(sysConfigDto);
        if (UEmpty.isNull(sysConfigDto)) {
            throw new ConfigException("config.not.exists", new Object[]{configKey});
        }
        return sysConfigDto;
    }

    @Override
    public void validateCaptcha(String username, String code, String captchaKey) {
        SysConfigDto sysConfigDto = USpring.getAopProxy(this).findConfigByKey(SysConfigConstant.SYS_ACCOUNT_CAPTCHA_ENABLED);
        String captchaEnabled = sysConfigDto.getConfigValue();
        boolean flag = Convert.toBool(captchaEnabled, false);
        if (flag) {
            checkCaptcha(username, code, captchaKey);
        }
    }

    @Override
    public void loadSysConfig() {
        List<SysConfigDto> sysConfigDtoList = super.findList(new SysConfigDto());
        sysConfigDtoList.forEach(sysConfigDto -> UCache.put(CacheConstant.SYS_CONFIG, sysConfigDto.getConfigKey(), sysConfigDto));
    }

    @Override
    @CacheEvict(cacheNames = CacheConstant.SYS_CONFIG, key = "#configKey")
    @LogRecord(module = ConfigModule.CONFIG_MANAGEMENT, subModule = ConfigModule.SubModule.DELETE_CONFIG, message = "config.delete")
    public void deleteConfig(String configKey) {
        Wrapper<SysConfigPo> updateWrapper = new LambdaUpdateWrapper<SysConfigPo>()
                .eq(SysConfigPo::getConfigKey, configKey);
        sysConfigMapper.delete(updateWrapper);
    }

    /**
     * 校验验证码
     *
     * @param username   用户名
     * @param code       用户输入的验证码（被校验）
     * @param captchaKey 校验验证码
     */
    private void checkCaptcha(String username, String code, String captchaKey) {
        String verifyKey = CacheConstant.CAPTCHA_CODE_KEY + StrUtil.emptyToDefault(captchaKey, "");
        String captcha = URedis.get(verifyKey);
        URedis.delete(verifyKey);
        String ip = UServlet.getInitiatedRequestIp();
        if (UEmpty.isEmpty(captcha)) {
            SysSensitiveLogBean sysSensitiveLogBean = new SysSensitiveLogBean();
            sysSensitiveLogBean.setOperatorId(0L);
            sysSensitiveLogBean.setDeptId(-1L);
            sysSensitiveLogBean.setDeptName(AdminConstant.UNKNOWN);
            sysSensitiveLogBean.setOperatorName(username);
            sysSensitiveLogBean.setMethodType(UServlet.getMethod());
            sysSensitiveLogBean.setUrl(UServlet.getRequestUri());
            sysSensitiveLogBean.setBeOperatedId(0L);
            sysSensitiveLogBean.setBeOperatedName(username);
            sysSensitiveLogBean.setIpAddress(ip);
            sysSensitiveLogBean.setLocation(URegion.getRealAddressByIp(ip));
            sysSensitiveLogBean.setOperateTime(new Date());
            sysSensitiveLogBean.setBrowser(UServlet.getBrowser());
            sysSensitiveLogBean.setOs(UServlet.getOs());
            sysSensitiveLogBean.setModule(UserModule.USER_MANAGEMENT);
            sysSensitiveLogBean.setSubModule(UserModule.SubModule.CHECK_CAPTCHA);
            sysSensitiveLogBean.setType(UserModule.SubModule.LOGIN);
            sysSensitiveLogBean.setResult(FlagConstant.FAILED);
            sysSensitiveLogBean.setContextOld(null);
            sysSensitiveLogBean.setContext(null);
            sysSensitiveLogBean.setSign(username);
            sysSensitiveLogBean.setRemark(UMessage.message("user.jcaptcha.expire"));
            USpring.context().publishEvent(sysSensitiveLogBean);
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            SysSensitiveLogBean sysSensitiveLogBean = new SysSensitiveLogBean();
            sysSensitiveLogBean.setOperatorId(0L);
            sysSensitiveLogBean.setDeptId(-1L);
            sysSensitiveLogBean.setDeptName(AdminConstant.UNKNOWN);
            sysSensitiveLogBean.setOperatorName(username);
            sysSensitiveLogBean.setMethodType(UServlet.getMethod());
            sysSensitiveLogBean.setUrl(UServlet.getRequestUri());
            sysSensitiveLogBean.setBeOperatedId(0L);
            sysSensitiveLogBean.setBeOperatedName(username);
            sysSensitiveLogBean.setIpAddress(ip);
            sysSensitiveLogBean.setLocation(URegion.getRealAddressByIp(ip));
            sysSensitiveLogBean.setOperateTime(new Date());
            sysSensitiveLogBean.setBrowser(UServlet.getBrowser());
            sysSensitiveLogBean.setOs(UServlet.getOs());
            sysSensitiveLogBean.setModule(UserModule.USER_MANAGEMENT);
            sysSensitiveLogBean.setSubModule(UserModule.SubModule.CHECK_CAPTCHA);
            sysSensitiveLogBean.setType(UserModule.SubModule.LOGIN);
            sysSensitiveLogBean.setResult(FlagConstant.FAILED);
            sysSensitiveLogBean.setContextOld(null);
            sysSensitiveLogBean.setContext(null);
            sysSensitiveLogBean.setSign(username);
            sysSensitiveLogBean.setRemark(UMessage.message("user.jcaptcha.error"));
            USpring.context().publishEvent(sysSensitiveLogBean);
            throw new CaptchaException();
        }
    }

}
