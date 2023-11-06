package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.SysModule;
import com.freesia.dto.SysConfigDto;
import com.freesia.exception.CaptchaException;
import com.freesia.exception.CaptchaExpireException;
import com.freesia.mapper.SysConfigMapper;
import com.freesia.po.SysConfigPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysConfigRepository;
import com.freesia.service.SysConfigService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigPo> implements SysConfigService {
    private final SysConfigMapper sysConfigMapper;
    private final SysConfigRepository sysConfigRepository;

    @Override
    @CachePut(cacheNames = CacheConstant.SYS_CONFIG, key = "#sysConfigDto.configKey")
    public SysConfigPo saveUpdate(SysConfigDto sysConfigDto) {
        SysConfigPo sysConfigPo = new SysConfigPo();
        UCopy.fullCopy(sysConfigDto, sysConfigPo);
        return sysConfigRepository.saveAndFlush(sysConfigPo);
    }

    @Override
    public List<SysConfigPo> saveUpdateBatch(List<SysConfigDto> list) {
        List<SysConfigPo> sysConfigPoList = UCopy.fullCopyCollections(list, SysConfigPo.class);
        return sysConfigRepository.saveAllAndFlush(sysConfigPoList);
    }

    @Override
    public boolean findCaptchaEnabled() {
        String captchaEnabled = USpring.getAopProxy(this).findConfigByKey(AdminConstant.SYS_ACCOUNT_CAPTCHA_ENABLED);
        if (UEmpty.isEmpty(captchaEnabled)) {
            return true;
        }
        return Convert.toBool(captchaEnabled, false);
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_CONFIG, key = "#configKey")
    public String findConfigByKey(String configKey) {
        Wrapper<SysConfigPo> queryWrapper = new LambdaQueryWrapper<SysConfigPo>()
                .select(SysConfigPo::getConfigKey)
                .eq(SysConfigPo::getLogicDel, FlagConstant.ENABLED)
                .eq(SysConfigPo::getConfigKey, configKey);
        SysConfigPo sysConfigPo = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(sysConfigPo)) {
            return sysConfigPo.getConfigValue();
        }
        return UString.EMPTY;
    }

    @Override
    public void validateCaptcha(String username, String code, String captchaKey) {
        if (findCaptchaEnabled()) {
            checkCaptcha(username, code, captchaKey);
        }
    }

    @Override
    public void loadSysConfig() {
        Wrapper<SysConfigPo> queryWrapper = new LambdaQueryWrapper<SysConfigPo>()
                .eq(SysConfigPo::getLogicDel, FlagConstant.ENABLED);
        List<SysConfigPo> sysConfigPoList = this.list(queryWrapper);
        sysConfigPoList.forEach(sysConfigPo -> UCache.put(CacheConstant.SYS_CONFIG, sysConfigPo.getConfigKey(), sysConfigPo.getConfigValue()));
    }

    @Override
    public TableResult<SysConfigDto> findPageSysConfig(SysConfigDto sysConfigDto, PageQuery pageQuery) {
        Page<SysConfigPo> page = sysConfigMapper.findPageSysConfig(pageQuery.build(), new LambdaQueryWrapper<SysConfigPo>()
                .eq(SysConfigPo::getLogicDel, FlagConstant.ENABLED)
                .like(UEmpty.isNotEmpty(sysConfigDto.getConfigKey()), SysConfigPo::getConfigKey, sysConfigDto.getConfigKey())
                .like(UEmpty.isNotEmpty(sysConfigDto.getConfigName()), SysConfigPo::getConfigName, sysConfigDto.getConfigName())
                .orderByAsc(SysConfigPo::getConfigKey)
        );
        return TableResult.build(UCopy.convertPagePo2Dto(page, SysConfigDto.class));
    }

    @Override
    public void saveConfig(SysConfigDto sysConfigDto) {
        USpring.getAopProxy(this).saveUpdate(sysConfigDto);
    }

    /**
     * 校验验证码
     *
     * @param username   用户名
     * @param code       用户输入的验证码（被校验）
     * @param captchaKey 校验验证码
     */
    public void checkCaptcha(String username, String code, String captchaKey) {
        String verifyKey = CacheConstant.CAPTCHA_CODE_KEY + StrUtil.emptyToDefault(captchaKey, "");
        String captcha = URedis.get(verifyKey);
        URedis.delete(verifyKey);
        String ip = UServlet.getInitiatedRequestIp();
        if (UEmpty.isEmpty(captcha)) {
            SysSensitiveLogBean registerOperLogEvent = USecurity.recordSensitiveLog(() -> {
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
                sysSensitiveLogBean.setModule(SysModule.USER_MANAGEMENT);
                sysSensitiveLogBean.setSubModule(SysModule.CHECK_CAPTCHA);
                sysSensitiveLogBean.setType(SysModule.LOGIN);
                sysSensitiveLogBean.setResult(FlagConstant.FAILED);
                sysSensitiveLogBean.setContextOld(null);
                sysSensitiveLogBean.setContext(null);
                sysSensitiveLogBean.setSign(username);
                sysSensitiveLogBean.setRemark(UMessage.message("user.jcaptcha.expire"));
                return sysSensitiveLogBean;
            });
            USpring.context().publishEvent(registerOperLogEvent);
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            SysSensitiveLogBean registerOperLogEvent = USecurity.recordSensitiveLog(() -> {
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
                sysSensitiveLogBean.setModule(SysModule.USER_MANAGEMENT);
                sysSensitiveLogBean.setSubModule(SysModule.CHECK_CAPTCHA);
                sysSensitiveLogBean.setType(SysModule.LOGIN);
                sysSensitiveLogBean.setResult(FlagConstant.FAILED);
                sysSensitiveLogBean.setContextOld(null);
                sysSensitiveLogBean.setContext(null);
                sysSensitiveLogBean.setSign(username);
                sysSensitiveLogBean.setRemark(UMessage.message("user.jcaptcha.error"));
                return sysSensitiveLogBean;
            });
            USpring.context().publishEvent(registerOperLogEvent);
            throw new CaptchaException();
        }
    }


}
