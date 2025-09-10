package com.freesia.icon.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;
import com.freesia.icon.dto.FindListSelectCostTypeDto;
import com.freesia.icon.mapper.CommonIconTemplateHeaderMapper;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import com.freesia.icon.repository.CommonIconTemplateHeaderRepository;
import com.freesia.icon.service.CommonIconTemplateHeaderService;
import com.freesia.po.BasePo;
import com.freesia.pojo.LaySelect;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 业务逻辑类
 * @date 2025-04-07
 */
@Service
@RequiredArgsConstructor
public class CommonIconTemplateHeaderServiceImpl extends ServiceImpl<CommonIconTemplateHeaderMapper, CommonIconTemplateHeaderPo> implements CommonIconTemplateHeaderService {
    private final CommonIconTemplateHeaderRepository commonIconTemplateHeaderRepository;
    private final CommonIconTemplateHeaderMapper commonIconTemplateHeaderMapper;

    @Override
    public CommonIconTemplateHeaderDto saveUpdate(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto) {
        Long userId = USecurity.getUserId();
        if (Convert.toBool(commonIconTemplateHeaderDto.getDefaultFlag(), false)) {
            boolean flag = commonIconTemplateHeaderMapper.findExistsDefaultFlag(userId);
            if (flag) {
                Wrapper<CommonIconTemplateHeaderPo> wrapper = new LambdaQueryWrapper<CommonIconTemplateHeaderPo>()
                        .eq(BasePo::getLogicDel, FlagConstant.DISABLED)
                        .eq(CommonIconTemplateHeaderPo::getUserId, userId);
                List<CommonIconTemplateHeaderPo> commonIconTemplateHeaderPoList = commonIconTemplateHeaderMapper.selectList(wrapper);
                for (CommonIconTemplateHeaderPo commonIconTemplateHeaderPo : commonIconTemplateHeaderPoList) {
                    commonIconTemplateHeaderPo.setDefaultFlag(false);
                }
                commonIconTemplateHeaderRepository.saveAll(commonIconTemplateHeaderPoList);
            }
        }
        CommonIconTemplateHeaderPo commonIconTemplateHeaderPo = UCopy.copyDto2Po(commonIconTemplateHeaderDto, CommonIconTemplateHeaderPo.class);
        commonIconTemplateHeaderPo.setUserId(userId);
        CommonIconTemplateHeaderDto resultDto = new CommonIconTemplateHeaderDto();
        UCopy.fullCopy(commonIconTemplateHeaderRepository.saveAndFlush(commonIconTemplateHeaderPo), resultDto);
        return resultDto;
    }

    @Override
    public List<CommonIconTemplateHeaderDto> saveUpdateBatch(List<CommonIconTemplateHeaderDto> list) {
        List<CommonIconTemplateHeaderPo> commonIconTemplateHeaderPoList = UCopy.fullCopyList(list, CommonIconTemplateHeaderPo.class);
        return UCopy.fullCopyList(commonIconTemplateHeaderRepository.saveAllAndFlush(commonIconTemplateHeaderPoList), CommonIconTemplateHeaderDto.class);
    }

    @Override
    public TableResult<CommonIconTemplateHeaderDto> findPageCommonIconTemplateHeader(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto, PageQuery pageQuery) {
        LambdaQueryWrapper<CommonIconTemplateHeaderPo> wrapper = new LambdaQueryWrapper<CommonIconTemplateHeaderPo>()
                .eq(CommonIconTemplateHeaderPo::getLogicDel, FlagConstant.DISABLED)
                .eq(CommonIconTemplateHeaderPo::getUserId, USecurity.getUserId())
                .eq(UEmpty.isNotEmpty(commonIconTemplateHeaderDto.getId()), CommonIconTemplateHeaderPo::getId, commonIconTemplateHeaderDto.getId());
        Page<CommonIconTemplateHeaderPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, CommonIconTemplateHeaderDto.class));
    }

    @Override
    public CommonIconTemplateHeaderDto findCommonIconTemplateHeader(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto) {
        LambdaQueryWrapper<CommonIconTemplateHeaderPo> wrapper = new LambdaQueryWrapper<CommonIconTemplateHeaderPo>()
                .eq(CommonIconTemplateHeaderPo::getLogicDel, FlagConstant.DISABLED)
                .eq(CommonIconTemplateHeaderPo::getUserId, USecurity.getUserId())
                .eq(UEmpty.isNotEmpty(commonIconTemplateHeaderDto.getId()), CommonIconTemplateHeaderPo::getId, commonIconTemplateHeaderDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), CommonIconTemplateHeaderDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonIconTemplateHeader(List<Long> idList) {
        removeBatchByIds(idList);
    }

    @Override
    public Integer findMaxOrderNum() {
        return commonIconTemplateHeaderMapper.findMaxOrderNum();
    }

    @Override
    public List<LaySelect> findSelectCommonIconHeader(Long userId) {
        LambdaQueryWrapper<CommonIconTemplateHeaderPo> wrapper = new LambdaQueryWrapper<CommonIconTemplateHeaderPo>()
                .eq(CommonIconTemplateHeaderPo::getLogicDel, FlagConstant.DISABLED)
                .eq(CommonIconTemplateHeaderPo::getUserId, USecurity.getUserId());
        List<CommonIconTemplateHeaderPo> commonIconTemplateHeaderPoList = commonIconTemplateHeaderMapper.selectList(wrapper);
        return buildLaySelects(commonIconTemplateHeaderPoList);
    }

    @Override
    public List<LaySelect> findListSelectCostType(FindListSelectCostTypeDto dto) {
        return commonIconTemplateHeaderMapper.findListSelectCostType(dto);
    }

    @Override
    public List<LaySelect> findCacheCostType(FindListSelectCostTypeDto dto) {
        return commonIconTemplateHeaderMapper.findCacheCostType(dto);
    }

    private static List<LaySelect> buildLaySelects(List<CommonIconTemplateHeaderPo> commonIconTemplateHeaderPoList) {
        List<LaySelect> laySelectList = new ArrayList<>();
        if (UEmpty.isNotEmpty(commonIconTemplateHeaderPoList)) {
            for (CommonIconTemplateHeaderPo commonIconTemplateHeaderPo : commonIconTemplateHeaderPoList) {
                LaySelect laySelect = new LaySelect();
                laySelect.setLabel(commonIconTemplateHeaderPo.getName());
                laySelect.setValue(commonIconTemplateHeaderPo.getId().toString());
                laySelect.setDefaultFlag(commonIconTemplateHeaderPo.getDefaultFlag());
                laySelectList.add(laySelect);
            }
        }
        return laySelectList;
    }
}
