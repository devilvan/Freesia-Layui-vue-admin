package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.vo.SysColumnDetailVo;
import com.freesia.dto.SysColumnDetailDto;
import com.freesia.po.SysColumnDetailPo;
import com.freesia.service.SysColumnDetailService;
import com.freesia.converter.SysColumnDetailConverter;
import com.freesia.mapper.SysColumnDetailMapper;
import com.freesia.repository.SysColumnDetailRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 业务逻辑类
 * @date 2026-03-16
 */
@Service
@RequiredArgsConstructor
public class SysColumnDetailServiceImpl extends BaseServiceImpl<SysColumnDetailMapper, SysColumnDetailVo, SysColumnDetailDto, SysColumnDetailPo> implements SysColumnDetailService {
    private final SysColumnDetailRepository sysColumnDetailRepository;
    private final SysColumnDetailMapper sysColumnDetailMapper;
    private final SysColumnDetailConverter sysColumnDetailConverter;

    @Override
    protected MapStructConverter<SysColumnDetailVo, SysColumnDetailDto, SysColumnDetailPo> getMapStructConverter() {
        return sysColumnDetailConverter;
    }

    @Override
    protected JpaRepository<SysColumnDetailPo, Long> getRepository() {
        return sysColumnDetailRepository;
    }

    @Override
    protected Class<SysColumnDetailDto> getDtoClass() {
        return SysColumnDetailDto.class;
    }

    @Override
    protected Class<SysColumnDetailPo> getPoClass() {
        return SysColumnDetailPo.class;
    }

    @Override
    protected Wrapper<SysColumnDetailPo> buildQueryWrapper(@NonNull SysColumnDetailDto sysColumnDetailDto) {
        return new LambdaQueryWrapper<SysColumnDetailPo>()
                .eq(SysColumnDetailPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getId()), SysColumnDetailPo::getId, sysColumnDetailDto.getId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getUserId()), SysColumnDetailPo::getUserId, sysColumnDetailDto.getUserId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getHeaderId()), SysColumnDetailPo::getHeaderId, sysColumnDetailDto.getHeaderId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getTitle()), SysColumnDetailPo::getTitle, sysColumnDetailDto.getTitle())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getEnabled()), SysColumnDetailPo::getEnabled, sysColumnDetailDto.getEnabled())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getFixed()), SysColumnDetailPo::getFixed, sysColumnDetailDto.getFixed())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getEllipsisTooltip()), SysColumnDetailPo::getEllipsisTooltip, sysColumnDetailDto.getEllipsisTooltip())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getOrderNum()), SysColumnDetailPo::getOrderNum, sysColumnDetailDto.getOrderNum())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getSorted()), SysColumnDetailPo::getSorted, sysColumnDetailDto.getSorted())
                ;
    }

    @Override
    public TableResult<SysColumnDetailDto> findPage(SysColumnDetailDto dto, PageQuery pageQuery) {
        Page<SysColumnDetailPo> page = sysColumnDetailMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysColumnDetailConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<SysColumnDetailDto> findList(SysColumnDetailDto dto) {
        return sysColumnDetailMapper.findList(dto);
    }


    @Override
    public SysColumnDetailDto findOne(SysColumnDetailDto dto) {
        return sysColumnDetailConverter.convertPo2Dto(sysColumnDetailMapper.findOne(dto));
    }

    @Override
    public void init() {

    }
}
