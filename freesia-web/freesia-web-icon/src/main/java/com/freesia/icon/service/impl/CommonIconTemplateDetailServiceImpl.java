package com.freesia.icon.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.icon.converter.CommonIconTemplateDetailConverter;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.icon.entity.FindCommonIconTemplateDetailEntity;
import com.freesia.icon.entity.FindTreeIconTreeTypeEntity;
import com.freesia.icon.mapper.CommonIconTemplateDetailMapper;
import com.freesia.icon.po.CommonIconTemplateDetailPo;
import com.freesia.icon.repository.CommonIconTemplateDetailRepository;
import com.freesia.icon.service.CommonIconTemplateDetailService;
import com.freesia.icon.vo.CommonIconTemplateDetailVo;
import com.freesia.pojo.LaySelect;
import com.freesia.redis.util.URedis;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.util.UTree;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 业务逻辑类
 * @date 2025-04-07
 */
@Service
@RequiredArgsConstructor
public class CommonIconTemplateDetailServiceImpl extends BaseServiceImpl<CommonIconTemplateDetailMapper, CommonIconTemplateDetailVo, CommonIconTemplateDetailDto, CommonIconTemplateDetailPo> implements CommonIconTemplateDetailService {
    private final CommonIconTemplateDetailRepository commonIconTemplateDetailRepository;
    private final CommonIconTemplateDetailMapper commonIconTemplateDetailMapper;
    private final CommonIconTemplateDetailConverter commonIconTemplateDetailConverter;


    @Override
    protected MapStructConverter<CommonIconTemplateDetailVo, CommonIconTemplateDetailDto, CommonIconTemplateDetailPo> getMapStructConverter() {
        return commonIconTemplateDetailConverter;
    }

    @Override
    protected JpaRepository<CommonIconTemplateDetailPo, Long> getRepository() {
        return commonIconTemplateDetailRepository;
    }

    @Override
    protected Class<CommonIconTemplateDetailDto> getDtoClass() {
        return CommonIconTemplateDetailDto.class;
    }

    @Override
    protected Class<CommonIconTemplateDetailPo> getPoClass() {
        return CommonIconTemplateDetailPo.class;
    }

    @Override
    protected Wrapper<CommonIconTemplateDetailPo> buildQueryWrapper(@NonNull CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        return new LambdaQueryWrapper<CommonIconTemplateDetailPo>()
                .eq(CommonIconTemplateDetailPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(commonIconTemplateDetailDto.getId()), CommonIconTemplateDetailPo::getId, commonIconTemplateDetailDto.getId());
    }

    @Override
    public List<CommonIconTemplateDetailDto> saveUpdateBatch(CommonIconTemplateDetailDto dto) {
        List<CommonIconTemplateDetailPo> commonIconTemplateDetailPoList = new ArrayList<>();
        List<FindCommonIconEntity> multipleIconList = dto.getMultipleIconList();
        int orderNum = Convert.toInt(dto.getOrderNum(), 0) + 10;
        for (FindCommonIconEntity entity : multipleIconList) {
            CommonIconTemplateDetailPo po = commonIconTemplateDetailConverter.convertDto2Po(dto);
            po.setIconId(entity.getId());
            po.setName(entity.getName());
            po.setOrderNum(orderNum);
            commonIconTemplateDetailPoList.add(po);
            orderNum += 10;
        }
        List<CommonIconTemplateDetailPo> poList = commonIconTemplateDetailRepository.saveAll(commonIconTemplateDetailPoList);
        return commonIconTemplateDetailConverter.convertBatchPo2Dto(poList);
    }


    @Override
    public FindCommonIconTemplateDetailEntity findCommonIconTemplateDetail(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        return commonIconTemplateDetailMapper.findCommonIconTemplateDetail(commonIconTemplateDetailDto);
    }

    @Override
    public List<FindTreeIconTreeTypeEntity> findTreeIconTreeType(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        List<FindTreeIconTreeTypeEntity> findTreeIconTreeTypeEntityList = commonIconTemplateDetailMapper.findTreeIconTreeType(commonIconTemplateDetailDto);
        return UTree.buildTree(findTreeIconTreeTypeEntityList);
    }

    @Override
    public Integer findMaxOrderNum(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        return commonIconTemplateDetailMapper.findMaxOrderNum(commonIconTemplateDetailDto);
    }

    @Override
    public List<LaySelect> findGrouping(CommonIconTemplateDetailDto dto) {
        return commonIconTemplateDetailMapper.findGrouping(dto);
    }

    @Override
    public List<FindTreeIconTreeTypeEntity> findCustomIconTemplateDetail(CommonIconTemplateDetailDto dto) {
        List<FindTreeIconTreeTypeEntity> list = commonIconTemplateDetailMapper.findCustomIconTemplateDetail(dto);
        return UTree.buildTree(list);
    }

    @Override
    public void deleteGrouping(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        Long parentId = commonIconTemplateDetailDto.getParentId();
        commonIconTemplateDetailRepository.deleteGrouping(parentId);
    }

    @Override
    public List<CommonIconTemplateDetailDto> findCacheDefaultCommonIconDetail() {
        List<CommonIconTemplateDetailDto> list = URedis.get(CacheConstant.DEFAULT_COMMON_ICON_DETAIL);
        if (UEmpty.isNotEmpty(list)) {
            return list;
        }
        List<CommonIconTemplateDetailPo> commonIconTemplateDetailPoList = commonIconTemplateDetailRepository.findAllByBuildInTrue();
        if (UEmpty.isNotEmpty(commonIconTemplateDetailPoList)) {
            list = commonIconTemplateDetailConverter.convertBatchPo2Dto(commonIconTemplateDetailPoList);
            URedis.set(CacheConstant.DEFAULT_COMMON_ICON_DETAIL, list);
        }
        return list;
    }
}
