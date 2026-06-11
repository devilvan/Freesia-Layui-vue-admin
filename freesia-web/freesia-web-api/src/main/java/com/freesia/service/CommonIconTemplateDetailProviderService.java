package com.freesia.service;

import com.freesia.bean.CommonIconTemplateDetailBean;

import java.util.List;

/**
 * @author Bliss.Wu
 * @Description 通用图标模板详情表提供服务接口
 * @date 2026-06-11
 */
public interface CommonIconTemplateDetailProviderService {
    /**
     * 查找默认通用图标模板头
     *
     * @return 通用图标模板头
     */
    List<CommonIconTemplateDetailBean> findListDefaultCommonIconTemplate();
}
