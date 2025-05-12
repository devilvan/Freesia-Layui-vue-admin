package com.freesia.service;


import com.freesia.dto.SysConfigDto;
import com.freesia.po.SysConfigPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 业务逻辑接口
 * @date 2023-08-12
 */
public interface SysConfigService {
    /**
     * 保存
     *
     * @param sysConfigDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysConfigDto saveUpdate(SysConfigDto sysConfigDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysConfigPo> saveUpdateBatch(List<SysConfigDto> list);

    /**
     * 根据系统配置键查询值
     *
     * @param configKey 系统配置键
     * @return 系统配置值
     */
    SysConfigDto findConfigByKey(String configKey);

    /**
     * 1. 判断验证码功能是否开启
     * 2. 若开启则验证用户输入的验证码
     *
     * @param username   用户名
     * @param code       用户输入的验证码
     * @param captchaKey 唯一ID
     */
    void validateCaptcha(String username, String code, String captchaKey);

    /**
     * 应用启动初始化数据字典
     */
    void loadSysConfig();

    /**
     * 获取参数配置分页
     *
     * @param sysConfigDto 查询参数
     * @param pageQuery    分页参数
     * @return 参数配置分页对象
     */
    TableResult<SysConfigDto> findPageSysConfig(SysConfigDto sysConfigDto, PageQuery pageQuery);

    /**
     * 保存系统配置信息
     *
     * @param sysConfigDto 系统配置信息
     */
    void saveConfig(SysConfigDto sysConfigDto);

    /**
     * 根据键查询系统配置参数
     *
     * @param configKey 系统配置键
     * @return 系统配置参数对象
     */
    SysConfigDto findSysConfigByConfigKey(String configKey);

    /**
     * 删除系统配置参数
     *
     * @param configKey 系统配置键
     */
    void deleteConfig(String configKey);
}
