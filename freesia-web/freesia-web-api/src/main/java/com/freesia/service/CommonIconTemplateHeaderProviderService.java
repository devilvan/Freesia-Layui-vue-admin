package com.freesia.service;

import com.freesia.bean.CommonIconTemplateHeaderBean;

/**
 * @author Bliss.Wu
 * @Description 通用图标提供服务接口
 * @date 2026-06-11
 */
public interface CommonIconTemplateHeaderProviderService {
    /**
     * 查找默认通用图标模板头
     *
     * @return 通用图标模板头
     */
    CommonIconTemplateHeaderBean findDefaultCommonIconHeader();

    /**
     * 初始化用户默认通用图标模板头
     */
    void initUserTemplateHeader(Long userId);
}
