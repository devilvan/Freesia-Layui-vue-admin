package com.freesia.icon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.entity.FindPageCommonIconEntity;
import com.freesia.icon.mapper.CommonIconMapper;
import com.freesia.icon.po.CommonIconPo;
import com.freesia.icon.repository.CommonIconRepository;
import com.freesia.icon.service.CommonIconService;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标表 业务逻辑类
 * @date 2025-03-26
 */
@Service
@RequiredArgsConstructor
public class CommonIconServiceImpl extends ServiceImpl<CommonIconMapper, CommonIconPo> implements CommonIconService {
    private final CommonIconRepository commonIconRepository;
    private final CommonIconMapper commonIconMapper;

    @Override
    public CommonIconDto saveUpdate(CommonIconDto commonIconDto) {
        CommonIconPo commonIconPo = new CommonIconPo();
        UCopy.fullCopy(commonIconDto, commonIconPo);
        CommonIconDto resultDto = new CommonIconDto();
        UCopy.fullCopy(commonIconRepository.saveAndFlush(commonIconPo), resultDto);
        return resultDto;
    }

    @Override
    public List<CommonIconDto> saveUpdateBatch(List<CommonIconDto> list) {
        List<CommonIconPo> commonIconPoList = UCopy.fullCopyList(list, CommonIconPo.class);
        return UCopy.fullCopyList(commonIconRepository.saveAllAndFlush(commonIconPoList), CommonIconDto.class);
    }

    @Override
    public TableResult<FindPageCommonIconEntity> findPageCommonIcon(CommonIconDto commonIconDto, PageQuery pageQuery) {
        Page<FindPageCommonIconEntity> findPageCommonIconEntityPage = commonIconMapper.findPageCommonIcon(commonIconDto, pageQuery.build());
        return TableResult.build(findPageCommonIconEntityPage);
    }

    @Override
    public CommonIconDto findCommonIcon(CommonIconDto commonIconDto) {
        LambdaQueryWrapper<CommonIconPo> wrapper = new LambdaQueryWrapper<CommonIconPo>()
                .eq(CommonIconPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(commonIconDto.getId()), CommonIconPo::getId, commonIconDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), CommonIconDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonIcon(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
