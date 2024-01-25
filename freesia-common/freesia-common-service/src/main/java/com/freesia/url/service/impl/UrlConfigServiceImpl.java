package com.freesia.url.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.url.dto.UrlConfigDto;
import com.freesia.url.mapper.UrlConfigMapper;
import com.freesia.url.po.UrlConfigPo;
import com.freesia.url.repository.UrlConfigRepository;
import com.freesia.url.service.UrlConfigService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 业务逻辑类
 * @date 2024-01-24
 */
@Service
@RequiredArgsConstructor
public class UrlConfigServiceImpl extends ServiceImpl<UrlConfigMapper, UrlConfigPo> implements UrlConfigService {
    private static final String URL_CONFIG = "url_config";
    private final UrlConfigRepository urlConfigRepository;

    @Override
    @CachePut(cacheNames = URL_CONFIG, key = "#urlConfigDto.code", unless = "#urlConfigDto.code==null")
    public UrlConfigDto saveUpdate(UrlConfigDto urlConfigDto) {
        UrlConfigPo urlConfigPo = new UrlConfigPo();
        UCopy.fullCopy(urlConfigDto, urlConfigPo);
        UrlConfigDto resultDto = new UrlConfigDto();
        UCopy.fullCopy(urlConfigRepository.saveAndFlush(urlConfigPo), resultDto);
        return resultDto;
    }

    @Override
    public List<UrlConfigDto> saveUpdateBatch(List<UrlConfigDto> list) {
        List<UrlConfigPo> urlConfigPoList = UCopy.fullCopyList(list, UrlConfigPo.class);
        return UCopy.fullCopyList(urlConfigRepository.saveAllAndFlush(urlConfigPoList), UrlConfigDto.class);
    }

    @Override
    public TableResult<UrlConfigDto> findPageUrlConfig(UrlConfigDto urlConfigDto, PageQuery pageQuery) {
        LambdaQueryWrapper<UrlConfigPo> wrapper = new LambdaQueryWrapper<UrlConfigPo>()
                .eq(UrlConfigPo::getLogicDel, FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(urlConfigDto.getId()), UrlConfigPo::getId, urlConfigDto.getId());
        Page<UrlConfigPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, UrlConfigDto.class));
    }

    @Override
    public UrlConfigDto findUrlConfig(UrlConfigDto urlConfigDto) {
        LambdaQueryWrapper<UrlConfigPo> wrapper = new LambdaQueryWrapper<UrlConfigPo>()
                .eq(UrlConfigPo::getLogicDel, FlagConstant.ENABLED)
                .eq(UrlConfigPo::getId, urlConfigDto.getId());
        UrlConfigPo urlConfigPo = getOne(wrapper);
        return UCopy.copyPo2Dto(urlConfigPo, UrlConfigDto.class);
    }

    @Override
    @Cacheable(cacheNames = URL_CONFIG, key = "#code")
    public UrlConfigDto findCacheUrlConfigByCode(String code) {
        LambdaQueryWrapper<UrlConfigPo> wrapper = new LambdaQueryWrapper<UrlConfigPo>()
                .eq(UrlConfigPo::getLogicDel, FlagConstant.ENABLED)
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
