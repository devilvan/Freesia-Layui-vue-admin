package com.freesia.icon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import com.freesia.icon.service.CommonIconTemplateHeaderService;
import com.freesia.icon.mapper.CommonIconTemplateHeaderMapper;
import com.freesia.icon.repository.CommonIconTemplateHeaderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
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

    @Override
    public CommonIconTemplateHeaderDto saveUpdate(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto) {
        CommonIconTemplateHeaderPo commonIconTemplateHeaderPo = new CommonIconTemplateHeaderPo();
        UCopy.fullCopy(commonIconTemplateHeaderDto, commonIconTemplateHeaderPo);
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
                .eq(UEmpty.isNotEmpty(commonIconTemplateHeaderDto.getId()), CommonIconTemplateHeaderPo::getId, commonIconTemplateHeaderDto.getId());
        Page<CommonIconTemplateHeaderPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, CommonIconTemplateHeaderDto.class));
    }

    @Override
    public CommonIconTemplateHeaderDto findCommonIconTemplateHeader(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto) {
        LambdaQueryWrapper<CommonIconTemplateHeaderPo> wrapper = new LambdaQueryWrapper<CommonIconTemplateHeaderPo>()
            .eq(CommonIconTemplateHeaderPo::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(commonIconTemplateHeaderDto.getId()), CommonIconTemplateHeaderPo::getId, commonIconTemplateHeaderDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), CommonIconTemplateHeaderDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonIconTemplateHeader(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
