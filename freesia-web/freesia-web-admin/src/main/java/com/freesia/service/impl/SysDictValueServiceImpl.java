package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.DictModule;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysDictValueDto;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysDictValueMapper;
import com.freesia.po.SysDictValuePo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysDictValueRepository;
import com.freesia.service.SysDictValueService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 字典值信息表 业务逻辑类
 * @date 2023-09-08
 */
@Service
@RequiredArgsConstructor
public class SysDictValueServiceImpl extends ServiceImpl<SysDictValueMapper, SysDictValuePo> implements SysDictValueService {
    private final SysDictValueRepository sysDictValueRepository;
    private final SysDictValueMapper sysDictValueMapper;

    @Override
    public SysDictValueDto saveUpdate(SysDictValueDto sysDictValueDto) {
        SysDictValuePo sysDictValuePo = new SysDictValuePo();
        UCopy.fullCopy(sysDictValueDto, sysDictValuePo);
        SysDictValueDto resultDto = new SysDictValueDto();
        UCopy.fullCopy(sysDictValueRepository.saveAndFlush(sysDictValuePo), resultDto);
        return resultDto;
    }

    @Override
    public List<SysDictValueDto> saveUpdateBatch(List<SysDictValueDto> list) {
        List<SysDictValuePo> sysDictValuePoList = UCopy.fullCopyList(list, SysDictValuePo.class);
        return UCopy.fullCopyList(sysDictValueRepository.saveAllAndFlush(sysDictValuePoList), SysDictValueDto.class);
    }

    @Override
    public TableResult<SysDictValueDto> findPageSysDictValue(SysDictValueDto sysDictValueDto, PageQuery pageQuery) {
        Page<SysDictValuePo> sysDictValuePoList = sysDictValueMapper.findPageSysDictValue(pageQuery.build(), Wrappers.<SysDictValuePo>query()
                .eq("DV.LOGIC_DEL", FlagConstant.DISABLED)
                .eq(ObjectUtil.isNotNull(sysDictValueDto.getKeyId()), "DV.KEY_ID", sysDictValueDto.getKeyId())
                .like(UEmpty.isNotEmpty(sysDictValueDto.getValueName()), "DV.VALUE_NAME", sysDictValueDto.getValueName())
                .like(UEmpty.isNotEmpty(sysDictValueDto.getValue()), "DV.VALUE", sysDictValueDto.getValue())
                .eq(UEmpty.isNotEmpty(sysDictValueDto.getStatus()), "DV.STATUS", sysDictValueDto.getStatus())
                .orderByDesc("DV.IS_DEFAULT")
                .orderByAsc("DV.ORDER_NUM"));
        Page<SysDictValueDto> sysDictValueDtoPage = UCopy.convertPagePo2Dto(sysDictValuePoList, SysDictValueDto.class);
        return TableResult.build(sysDictValueDtoPage);
    }

    @Override
    public List<SysDictValueDto> findSysDictValueList(SysDictValueDto sysDictValueDto) {
        List<SysDictValuePo> sysDictValuePoList = sysDictValueMapper.findSysDictValueList(Wrappers.<SysDictValuePo>query()
                .eq("DV.LOGIC_DEL", FlagConstant.DISABLED)
                .eq(ObjectUtil.isNotNull(sysDictValueDto.getKeyId()), "DV.KEY_ID", sysDictValueDto.getKeyId())
                .eq(UEmpty.isNotEmpty(sysDictValueDto.getDictKey()), "DV.DICT_KEY", sysDictValueDto.getDictKey())
                .like(UEmpty.isNotEmpty(sysDictValueDto.getValueName()), "DV.VALUE_NAME", sysDictValueDto.getValueName())
                .like(UEmpty.isNotEmpty(sysDictValueDto.getValue()), "DV.VALUE", sysDictValueDto.getValue())
                .eq(UEmpty.isNotEmpty(sysDictValueDto.getStatus()), "DV.STATUS", sysDictValueDto.getStatus())
                .orderByDesc("DV.IS_DEFAULT")
                .orderByAsc("DV.ORDER_NUM"));
        return UCopy.fullCopyList(sysDictValuePoList, SysDictValueDto.class);
    }

    @Override
    public List<SysDictValueDto> findCacheSysDictValueList(String dictKey) {
        List<SysDictValuePo> sysDictValuePoList = USpring.getAopProxy(this).findSysDictValuePoList(dictKey);
        return UCopy.fullCopyList(sysDictValuePoList, SysDictValueDto.class);
    }

    @Cacheable(cacheNames = CacheConstant.SYS_DICT, key = "#dictKey", unless = "#dictKey==null")
    public List<SysDictValuePo> findSysDictValuePoList(String dictKey) {
        Wrapper<SysDictValuePo> queryWrapper = Wrappers.<SysDictValuePo>query()
                .eq("DV.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("DK.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("DK.DICT_KEY", dictKey)
                .orderByAsc("DV.ID")
                .orderByAsc("DV.ORDER_NUM");
        return sysDictValueMapper.findSysDictValueList(queryWrapper);
    }

    @Override
    public void loadSysDictValue() {
        Wrapper<SysDictValuePo> queryWrapper = new LambdaQueryWrapper<SysDictValuePo>()
                .eq(SysDictValuePo::getLogicDel, FlagConstant.DISABLED)
                .orderByAsc(SysDictValuePo::getId)
                .orderByAsc(SysDictValuePo::getOrderNum);
        List<SysDictValuePo> sysDictValuePoList = sysDictValueMapper.selectList(queryWrapper);
        Map<String, List<SysDictValuePo>> sysDictValueMap = UStream.groupingByKey(sysDictValuePoList, SysDictValuePo::getDictKey);
        sysDictValueMap.forEach((k, v) -> UCache.put(CacheConstant.SYS_DICT, k, v));
    }

    @Override
    @LogRecord(module = DictModule.DICT_MANAGEMENT, subModule = DictModule.SubModule.SAVE_DICT_VALUE, message = "dict.value.save")
    public SysDictValueDto saveSysDictValue(SysDictValueDto sysDictValueDto) {
        SysDictValuePo sysDictValuePo = new SysDictValuePo();
        if (UEmpty.isNotEmpty(sysDictValueDto.getId())) {
            sysDictValuePo = sysDictValueRepository.findById(sysDictValueDto.getId()).orElseGet(SysDictValuePo::new);
            UCopy.halfCopy(sysDictValueDto, sysDictValuePo);
        } else {
            UCopy.fullCopy(sysDictValueDto, sysDictValuePo);
        }
        sysDictValueRepository.save(sysDictValuePo);
        return UCopy.copyPo2Dto(sysDictValuePo, SysDictValueDto.class);
    }

    @Override
    public void flushCacheSysDictValue(String dictKey) {
        UCache.evict(CacheConstant.SYS_DICT, dictKey);
        USpring.getAopProxy(this).findSysDictValuePoList(dictKey);
    }

    @Override
    @LogRecord(module = DictModule.DICT_MANAGEMENT, subModule = DictModule.SubModule.SAVE_DICT_VALUE, message = "dict.value.save")
    public void deleteSysDictValueList(List<Long> idList) {
        List<SysDictValuePo> sysDictValuePoList = sysDictValueRepository.findAllById(idList);
        for (SysDictValuePo sysDictValuePo : sysDictValuePoList) {
            sysDictValuePo.setLogicDel(true);
        }
        sysDictValueRepository.saveAll(sysDictValuePoList);
    }

    @Override
    public void enableSysDictValueList(List<Long> idList) {
        List<SysDictValuePo> sysDictValuePoList = sysDictValueRepository.findAllById(idList);
        for (SysDictValuePo sysDictValuePo : sysDictValuePoList) {
            String status = sysDictValuePo.getStatus();
            if (FlagConstant.ENABLED.equals(status)) {
                sysDictValuePo.setStatus(FlagConstant.DISABLED);
            } else if (FlagConstant.DISABLED.equals(status)) {
                sysDictValuePo.setStatus(FlagConstant.ENABLED);
            }
        }
        sysDictValueRepository.saveAll(sysDictValuePoList);
    }
}
