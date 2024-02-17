package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysDictDto;
import com.freesia.dto.SysDictKeyDto;
import com.freesia.entity.FindPageSysDictKeyEntity;
import com.freesia.mapper.SysDictKeyMapper;
import com.freesia.po.SysDictKeyPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysDictKeyRepository;
import com.freesia.service.SysDictKeyService;
import com.freesia.util.UCache;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 业务逻辑类
 * @date 2023-09-08
 */
@Service
@RequiredArgsConstructor
public class SysDictKeyServiceImpl extends ServiceImpl<SysDictKeyMapper, SysDictKeyPo> implements SysDictKeyService {
    private final SysDictKeyRepository sysDictKeyRepository;
    private final SysDictKeyMapper sysDictKeyMapper;

    @Override
    public SysDictKeyDto saveUpdate(SysDictKeyDto sysDictKeyDto) {
        SysDictKeyPo sysDictKeyPo = new SysDictKeyPo();
        UCopy.fullCopy(sysDictKeyDto, sysDictKeyPo);
        SysDictKeyDto resultDto = new SysDictKeyDto();
        UCopy.fullCopy(sysDictKeyRepository.saveAndFlush(sysDictKeyPo), resultDto);
        return resultDto;
    }

    @Override
    public List<SysDictKeyDto> saveUpdateBatch(List<SysDictKeyDto> list) {
        List<SysDictKeyPo> sysDictKeyPoList = UCopy.fullCopyList(list, SysDictKeyPo.class);
        return UCopy.fullCopyList(sysDictKeyRepository.saveAllAndFlush(sysDictKeyPoList), SysDictKeyDto.class);
    }

    @Override
    public TableResult<FindPageSysDictKeyEntity> findPageSysDictList(SysDictDto sysDictDto, PageQuery pageQuery) {
        Page<FindPageSysDictKeyEntity> page = sysDictKeyMapper.findPageSysDictList(pageQuery.build(), Wrappers.<SysDictKeyPo>query()
                .eq("DK.LOGIC_DEL", FlagConstant.ENABLED)
                .eq("DV.LOGIC_DEL", FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(sysDictDto.getStatus()), "DK.STATUS", sysDictDto.getStatus())
                .eq(UEmpty.isNotEmpty(sysDictDto.getStatus()), "DV.STATUS", sysDictDto.getStatus())
                .like(UEmpty.isNotEmpty(sysDictDto.getKeyName()), "DK.KEY_NAME", sysDictDto.getKeyName())
                .like(UEmpty.isNotEmpty(sysDictDto.getDictKey()), "DK.DICT_KEY", sysDictDto.getDictKey())
                .like(UEmpty.isNotEmpty(sysDictDto.getValue()), "DK.VALUE", sysDictDto.getValue())
                .like(UEmpty.isNotEmpty(sysDictDto.getValueName()), "DK.NAME", sysDictDto.getValueName())
                .orderByDesc("DK.ID")
                .orderByAsc("DV.ORDER_NUM"));
        return TableResult.build(page);
    }

    @Override
    public List<SysDictKeyDto> findSysDictKeyList(SysDictKeyDto sysDictKeyDto) {
        List<SysDictKeyPo> sysDictList = sysDictKeyMapper.findSysDictKeyList(Wrappers.<SysDictKeyPo>query()
                .eq("DK.LOGIC_DEL", FlagConstant.ENABLED)
                .like(UEmpty.isNotEmpty(sysDictKeyDto.getDictKey()), "DK.DICT_KEY", sysDictKeyDto.getDictKey())
                .like(UEmpty.isNotEmpty(sysDictKeyDto.getKeyName()), "DK.KEY_NAME", sysDictKeyDto.getKeyName())
                .eq(UEmpty.isNotEmpty(sysDictKeyDto.getStatus()), "DK.STATUS", sysDictKeyDto.getStatus())
                .orderByDesc("DK.ID"));
        return UCopy.fullCopyList(sysDictList, SysDictKeyDto.class);
    }

    @Override
    public SysDictKeyDto saveSysDictKey(SysDictKeyDto sysDictKeyDto) {
        Long id = sysDictKeyDto.getId();
        SysDictKeyDto dto = saveUpdate(sysDictKeyDto);
        if (ObjectUtil.isNull(id)) {
            // 新增字典键则，加入到缓存中
            UCache.put(CacheConstant.SYS_DICT, sysDictKeyDto.getDictKey(), Collections.emptyList());
        }
        return dto;
    }
}
