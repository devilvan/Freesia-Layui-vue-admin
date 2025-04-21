package com.freesia.icon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.entity.FindCommonIconTemplateDetailEntity;
import com.freesia.icon.entity.FindTreeIconTreeTypeEntity;
import com.freesia.icon.mapper.CommonIconTemplateDetailMapper;
import com.freesia.icon.po.CommonIconTemplateDetailPo;
import com.freesia.icon.repository.CommonIconTemplateDetailRepository;
import com.freesia.icon.service.CommonIconTemplateDetailService;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UTree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 业务逻辑类
 * @date 2025-04-07
 */
@Service
@RequiredArgsConstructor
public class CommonIconTemplateDetailServiceImpl extends ServiceImpl<CommonIconTemplateDetailMapper, CommonIconTemplateDetailPo> implements CommonIconTemplateDetailService {
    private final CommonIconTemplateDetailRepository commonIconTemplateDetailRepository;
    private final CommonIconTemplateDetailMapper commonIconTemplateDetailMapper;

    @Override
    public CommonIconTemplateDetailDto saveUpdate(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        CommonIconTemplateDetailPo commonIconTemplateDetailPo = new CommonIconTemplateDetailPo();
        UCopy.fullCopy(commonIconTemplateDetailDto, commonIconTemplateDetailPo);
        CommonIconTemplateDetailDto resultDto = new CommonIconTemplateDetailDto();
        UCopy.fullCopy(commonIconTemplateDetailRepository.saveAndFlush(commonIconTemplateDetailPo), resultDto);
        return resultDto;
    }

    @Override
    public List<CommonIconTemplateDetailDto> saveUpdateBatch(List<CommonIconTemplateDetailDto> list) {
        List<CommonIconTemplateDetailPo> commonIconTemplateDetailPoList = UCopy.fullCopyList(list, CommonIconTemplateDetailPo.class);
        return UCopy.fullCopyList(commonIconTemplateDetailRepository.saveAllAndFlush(commonIconTemplateDetailPoList), CommonIconTemplateDetailDto.class);
    }

    @Override
    public TableResult<CommonIconTemplateDetailDto> findPageCommonIconTemplateDetail(CommonIconTemplateDetailDto commonIconTemplateDetailDto, PageQuery pageQuery) {
        LambdaQueryWrapper<CommonIconTemplateDetailPo> wrapper = new LambdaQueryWrapper<CommonIconTemplateDetailPo>()
                .eq(CommonIconTemplateDetailPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(commonIconTemplateDetailDto.getId()), CommonIconTemplateDetailPo::getId, commonIconTemplateDetailDto.getId());
        Page<CommonIconTemplateDetailPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, CommonIconTemplateDetailDto.class));
    }

    @Override
    public FindCommonIconTemplateDetailEntity findCommonIconTemplateDetail(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        return commonIconTemplateDetailMapper.findCommonIconTemplateDetail(commonIconTemplateDetailDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonIconTemplateDetail(List<Long> idList) {
        removeBatchByIds(idList);
    }

    @Override
    public List<FindTreeIconTreeTypeEntity> findTreeIconTreeType(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        List<FindTreeIconTreeTypeEntity> findTreeIconTreeTypeEntityList = commonIconTemplateDetailMapper.findTreeIconTreeType(commonIconTemplateDetailDto);
        return UTree.buildTree(findTreeIconTreeTypeEntityList);
    }
}
