package com.freesia.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.annotation.LogRecord;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysOssConfigDto;
import com.freesia.mapper.SysOssConfigMapper;
import com.freesia.oss.constant.OssModule;
import com.freesia.po.SysOssConfigPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysOssConfigRepository;
import com.freesia.service.SysOssConfigService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.URedis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 业务逻辑类
 * @date 2024-02-28
 */
@Service
@RequiredArgsConstructor
public class SysOssConfigServiceImpl extends ServiceImpl<SysOssConfigMapper, SysOssConfigPo> implements SysOssConfigService {
    private final SysOssConfigRepository sysOssConfigRepository;
    private final SysOssConfigMapper sysOssConfigMapper;

    @Override
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.SAVE_OSS_CONFIG, message = "oss.config.save")
    public SysOssConfigDto saveUpdate(SysOssConfigDto sysOssConfigDto) {
        SysOssConfigPo sysOssConfigPo = new SysOssConfigPo();
        UCopy.fullCopy(sysOssConfigDto, sysOssConfigPo);
        SysOssConfigDto resultDto = new SysOssConfigDto();
        UCopy.fullCopy(sysOssConfigRepository.saveAndFlush(sysOssConfigPo), resultDto);
        return resultDto;
    }

    @Override
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.SAVE_OSS_CONFIG, message = "oss.config.save")
    public List<SysOssConfigDto> saveUpdateBatch(List<SysOssConfigDto> list) {
        List<SysOssConfigPo> sysOssConfigPoList = UCopy.fullCopyList(list, SysOssConfigPo.class);
        return UCopy.fullCopyList(sysOssConfigRepository.saveAllAndFlush(sysOssConfigPoList), SysOssConfigDto.class);
    }

    @Override
    public TableResult<SysOssConfigDto> findPageSysOssConfig(SysOssConfigDto sysOssConfig, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOssConfigPo> wrapper = new LambdaQueryWrapper<SysOssConfigPo>()
                .eq(SysOssConfigPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysOssConfig.getId()), SysOssConfigPo::getId, sysOssConfig.getId());
        Page<SysOssConfigPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, SysOssConfigDto.class));
    }

    @Override
    public SysOssConfigDto findSysOssConfig(SysOssConfigDto sysOssConfig) {
        LambdaQueryWrapper<SysOssConfigPo> wrapper = new LambdaQueryWrapper<SysOssConfigPo>()
                .eq(SysOssConfigPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysOssConfig.getId()), SysOssConfigPo::getId, sysOssConfig.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), SysOssConfigDto.class);
    }

    @Override
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.DELETE_OSS_CONFIG, message = "oss.config.delete")
    public void deleteSysOssConfig(List<Long> idList) {
        removeBatchByIds(idList);
    }

    /**
     * 应用启动初始化OSS对象存储配置
     */
    @Override
    public void loadSysOssConfig() {
        Wrapper<SysOssConfigPo> queryWrapper = new LambdaQueryWrapper<SysOssConfigPo>()
                .eq(SysOssConfigPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysOssConfigPo::getStatus, FlagConstant.ENABLED);
        List<SysOssConfigPo> sysOssConfigPoList = sysOssConfigMapper.selectList(queryWrapper);
        for (SysOssConfigPo sysOssConfigPo : sysOssConfigPoList) {
            String configKey = sysOssConfigPo.getConfigKey();
            URedis.set(CacheConstant.SYS_OSS_DEFAULT_CONFIG, configKey);
            URedis.put(CacheConstant.SYS_OSS_CONFIG, configKey, JSONObject.toJSONString(sysOssConfigPo));
        }
    }
}
