package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysOssConfigDto;
import com.freesia.json.util.UJSON;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysOssConfigMapper;
import com.freesia.oss.constant.OssModule;
import com.freesia.po.SysOssConfigPo;
import com.freesia.redis.util.URedis;
import com.freesia.repository.SysOssConfigRepository;
import com.freesia.service.SysOssConfigService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 业务逻辑类
 * @date 2024-02-28
 */
@Service
@RequiredArgsConstructor
public class SysOssConfigServiceImpl extends BaseServiceImpl<SysOssConfigMapper, SysOssConfigPo, SysOssConfigDto> implements SysOssConfigService {
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
    protected JpaRepository<SysOssConfigPo, Long> getRepository() {
        return sysOssConfigRepository;
    }

    @Override
    protected Class<SysOssConfigDto> getDtoClass() {
        return SysOssConfigDto.class;
    }

    @Override
    protected Class<SysOssConfigPo> getPoClass() {
        return SysOssConfigPo.class;
    }

    @Override
    protected Wrapper<SysOssConfigPo> buildQueryWrapper(@NonNull SysOssConfigDto dto) {
        return new LambdaQueryWrapper<SysOssConfigPo>()
                .eq(SysOssConfigPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(dto.getId()), SysOssConfigPo::getId, dto.getId());
    }

    @Override
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.DELETE_OSS_CONFIG, message = "oss.config.delete")
    public void deleteBatch(List<Long> idList) {
        super.deleteBatch(idList);
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
            URedis.put(CacheConstant.SYS_OSS_CONFIG, configKey, UJSON.toJSONString(sysOssConfigPo));
        }
    }
}
