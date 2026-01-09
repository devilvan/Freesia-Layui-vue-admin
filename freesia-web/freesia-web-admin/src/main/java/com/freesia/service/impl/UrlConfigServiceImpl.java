package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.UrlConfigDto;
import com.freesia.mapper.UrlConfigMapper;
import com.freesia.po.UrlConfigPo;
import com.freesia.repository.UrlConfigRepository;
import com.freesia.service.UrlConfigService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 业务逻辑类
 * @date 2024-01-24
 */
@Service
@RequiredArgsConstructor
public class UrlConfigServiceImpl extends BaseServiceImpl<UrlConfigMapper, UrlConfigPo, UrlConfigDto> implements UrlConfigService {
    private static final String URL_CONFIG = "url_config";
    private final UrlConfigRepository urlConfigRepository;


    @Override
    protected JpaRepository<UrlConfigPo, Long> getRepository() {
        return urlConfigRepository;
    }

    @Override
    protected Class<UrlConfigDto> getDtoClass() {
        return UrlConfigDto.class;
    }

    @Override
    protected Class<UrlConfigPo> getPoClass() {
        return UrlConfigPo.class;
    }

    @Override
    protected Wrapper<UrlConfigPo> buildQueryWrapper(@NonNull UrlConfigDto urlConfigDto) {
        return new LambdaQueryWrapper<UrlConfigPo>()
                .eq(UrlConfigPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(urlConfigDto.getId()), UrlConfigPo::getId, urlConfigDto.getId())
                .likeRight(UEmpty.isNotEmpty(urlConfigDto.getCode()), UrlConfigPo::getCode, urlConfigDto.getCode());
    }

    @Override
    @CachePut(cacheNames = URL_CONFIG, key = "#urlConfigDto.code")
    public UrlConfigDto saveUpdate(UrlConfigDto urlConfigDto) {
        return super.saveUpdate(urlConfigDto);
    }

    @Override
    @Cacheable(cacheNames = URL_CONFIG, key = "#code")
    public UrlConfigDto findCacheUrlConfigByCode(String code) {
        LambdaQueryWrapper<UrlConfigPo> wrapper = new LambdaQueryWrapper<UrlConfigPo>()
                .eq(UrlConfigPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UrlConfigPo::getCode, code);
        UrlConfigPo urlConfigPo = getOne(wrapper);
        return UCopy.copyPo2Dto(urlConfigPo, UrlConfigDto.class);
    }

    @Override
    @CacheEvict(cacheNames = URL_CONFIG, key = "#code")
    public void deleteUrlConfig(Long id, String code) {
        urlConfigRepository.deleteById(id);
    }
}
