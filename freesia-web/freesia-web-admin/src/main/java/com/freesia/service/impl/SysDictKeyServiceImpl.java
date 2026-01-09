package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.DictModule;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysDictDto;
import com.freesia.dto.SysDictKeyDto;
import com.freesia.entity.FindPageSysDictKeyEntity;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysDictKeyMapper;
import com.freesia.po.SysDictKeyPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysDictKeyRepository;
import com.freesia.service.SysDictKeyService;
import com.freesia.util.UCache;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
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
public class SysDictKeyServiceImpl extends BaseServiceImpl<SysDictKeyMapper, SysDictKeyPo, SysDictKeyDto> implements SysDictKeyService {
    private final SysDictKeyRepository sysDictKeyRepository;
    private final SysDictKeyMapper sysDictKeyMapper;


    @Override
    protected JpaRepository<SysDictKeyPo, Long> getRepository() {
        return sysDictKeyRepository;
    }

    @Override
    protected Class<SysDictKeyDto> getDtoClass() {
        return SysDictKeyDto.class;
    }

    @Override
    protected Class<SysDictKeyPo> getPoClass() {
        return SysDictKeyPo.class;
    }

    @Override
    protected Wrapper<SysDictKeyPo> buildQueryWrapper(@NonNull SysDictKeyDto dto) {
        return null;
    }

    @Override
    public SysDictKeyDto saveUpdate(SysDictKeyDto sysDictKeyDto) {
        return super.saveUpdate(sysDictKeyDto);
    }

    @Override
    public List<SysDictKeyDto> saveUpdateBatch(List<SysDictKeyDto> list) {
        return super.saveUpdateBatch(list);
    }

    @Override
    public TableResult<FindPageSysDictKeyEntity> findPageSysDictList(SysDictDto sysDictDto, PageQuery pageQuery) {
        Page<FindPageSysDictKeyEntity> page = sysDictKeyMapper.findPageSysDictList(pageQuery.build(), Wrappers.<SysDictKeyPo>query()
                .eq("DK.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("DV.LOGIC_DEL", FlagConstant.DISABLED)
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
    @LogRecord(module = DictModule.DICT_MANAGEMENT, subModule = DictModule.SubModule.SAVE_DICT_KEY, message = "dict.key.save")
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
