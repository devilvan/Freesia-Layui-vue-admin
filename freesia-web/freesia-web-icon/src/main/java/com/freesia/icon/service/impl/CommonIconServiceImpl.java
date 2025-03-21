package com.freesia.icon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.po.CommonIconPo;
import com.freesia.icon.service.CommonIconService;
import com.freesia.icon.mapper.CommonIconMapper;
import com.freesia.icon.repository.CommonIconRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标表 业务逻辑类
 * @date 2025-03-21
 */
@Service
@RequiredArgsConstructor
public class CommonIconServiceImpl extends ServiceImpl<CommonIconMapper, CommonIconPo> implements CommonIconService {
    private final CommonIconRepository commonIconRepository;

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
    public TableResult<CommonIconDto> findPageCommonIcon(CommonIconDto commonIcon, PageQuery pageQuery) {
        LambdaQueryWrapper<CommonIconPo> wrapper = new LambdaQueryWrapper<CommonIconPo>()
                .eq(CommonIconPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(commonIcon.getId()), CommonIconPo::getId, commonIcon.getId());
        Page<CommonIconPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, CommonIconDto.class));
    }

    @Override
    public CommonIconDto findCommonIcon(CommonIconDto commonIcon) {
        LambdaQueryWrapper<CommonIconPo> wrapper = new LambdaQueryWrapper<CommonIconPo>()
            .eq(CommonIconPo::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(commonIcon.getId()), CommonIconPo::getId, commonIcon.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), CommonIconDto.class);
    }

    @Override
    public void deleteCommonIcon(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
