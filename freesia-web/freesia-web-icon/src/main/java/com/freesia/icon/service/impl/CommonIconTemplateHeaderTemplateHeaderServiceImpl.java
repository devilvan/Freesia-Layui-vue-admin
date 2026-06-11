package com.freesia.icon.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.bean.CommonIconTemplateHeaderBean;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.exception.ServiceException;
import com.freesia.icon.converter.CommonIconTemplateHeaderConverter;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;
import com.freesia.icon.dto.FindListSelectCostTypeDto;
import com.freesia.icon.mapper.CommonIconTemplateHeaderMapper;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import com.freesia.icon.repository.CommonIconTemplateDetailRepository;
import com.freesia.icon.repository.CommonIconTemplateHeaderRepository;
import com.freesia.icon.service.CommonIconTemplateDetailService;
import com.freesia.icon.service.CommonIconTemplateHeaderService;
import com.freesia.icon.vo.CommonIconTemplateHeaderVo;
import com.freesia.po.BasePo;
import com.freesia.pojo.LaySelect;
import com.freesia.properties.WebCommonProperties;
import com.freesia.redis.util.URedis;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.CommonIconTemplateHeaderProviderService;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.util.UTree;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 业务逻辑类
 * @date 2025-04-07
 */
@Service
@RequiredArgsConstructor
public class CommonIconTemplateHeaderTemplateHeaderServiceImpl extends BaseServiceImpl<CommonIconTemplateHeaderMapper, CommonIconTemplateHeaderVo, CommonIconTemplateHeaderDto, CommonIconTemplateHeaderPo> implements CommonIconTemplateHeaderService, CommonIconTemplateHeaderProviderService {
    private final CommonIconTemplateHeaderRepository commonIconTemplateHeaderRepository;
    private final CommonIconTemplateHeaderMapper commonIconTemplateHeaderMapper;
    private final CommonIconTemplateHeaderConverter commonIconTemplateHeaderConverter;
    private final WebCommonProperties webCommonProperties;
    private final CommonIconTemplateDetailService commonIconTemplateDetailService;

    @Override
    protected MapStructConverter<CommonIconTemplateHeaderVo, CommonIconTemplateHeaderDto, CommonIconTemplateHeaderPo> getMapStructConverter() {
        return commonIconTemplateHeaderConverter;
    }

    @Override
    protected JpaRepository<CommonIconTemplateHeaderPo, Long> getRepository() {
        return commonIconTemplateHeaderRepository;
    }

    @Override
    protected Class<CommonIconTemplateHeaderDto> getDtoClass() {
        return CommonIconTemplateHeaderDto.class;
    }

    @Override
    protected Class<CommonIconTemplateHeaderPo> getPoClass() {
        return CommonIconTemplateHeaderPo.class;
    }

    @Override
    protected Wrapper<CommonIconTemplateHeaderPo> buildQueryWrapper(@NonNull CommonIconTemplateHeaderDto commonIconTemplateHeaderDto) {
        return new LambdaQueryWrapper<CommonIconTemplateHeaderPo>()
                .eq(CommonIconTemplateHeaderPo::getLogicDel, FlagConstant.DISABLED)
                .eq(CommonIconTemplateHeaderPo::getUserId, USecurity.getUserId())
                .eq(UEmpty.isNotEmpty(commonIconTemplateHeaderDto.getId()), CommonIconTemplateHeaderPo::getId, commonIconTemplateHeaderDto.getId());
    }

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
        CommonIconTemplateHeaderPo commonIconTemplateHeaderPo = commonIconTemplateHeaderConverter.convertDto2Po(commonIconTemplateHeaderDto);
        commonIconTemplateHeaderPo.setUserId(userId);
        CommonIconTemplateHeaderDto resultDto = new CommonIconTemplateHeaderDto();
        return super.saveUpdate(resultDto);
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

    @Override
    public CommonIconTemplateHeaderDto findCacheDefaultCommonIconHeader() {
        CommonIconTemplateHeaderDto defaultCommonIconHeaderDto = new CommonIconTemplateHeaderDto();
        Boolean initDefaultCommonIconTemplateFlag = webCommonProperties.getInitDefaultCommonIconTemplateFlag();
        if (initDefaultCommonIconTemplateFlag != null && initDefaultCommonIconTemplateFlag) {
            findAndCache(defaultCommonIconHeaderDto);
        } else {
            defaultCommonIconHeaderDto = URedis.get(CacheConstant.DEFAULT_COMMON_ICON_HEADER);
            if (UEmpty.isNotEmpty(defaultCommonIconHeaderDto) && defaultCommonIconHeaderDto.getId() != null) {
                return defaultCommonIconHeaderDto;
            }
            findAndCache(defaultCommonIconHeaderDto);
        }
        return defaultCommonIconHeaderDto;
    }

    @Override
    public CommonIconTemplateHeaderBean findDefaultCommonIconHeader() {
        CommonIconTemplateHeaderDto cacheDefaultCommonIconHeader = findCacheDefaultCommonIconHeader();
        return commonIconTemplateHeaderConverter.convertDto2Bean(cacheDefaultCommonIconHeader);
    }

    @Override
    public void initUserTemplateHeader(Long userId) {
        // 根据用户ID查询用户是否已初始化图标模板
        Optional<CommonIconTemplateHeaderPo> findOne = commonIconTemplateHeaderRepository.findById(userId);
        if (findOne.isPresent()) {
            CommonIconTemplateHeaderPo commonIconTemplateHeaderPo = findOne.get();
            // 查询图标模板详情
            Long headerId = commonIconTemplateHeaderPo.getId();
            Boolean templateExistsFlag = commonIconTemplateDetailService.findByHeaderIdExists(headerId);
            if (!templateExistsFlag) {
                // 生成该用户默认的图标模板
                List<CommonIconTemplateDetailDto> cacheDefaultCommonIconDetail = commonIconTemplateDetailService.findCacheDefaultCommonIconDetail();
                if (UEmpty.isNotEmpty(cacheDefaultCommonIconDetail)) {
                    cacheDefaultCommonIconDetail = cacheDefaultCommonIconDetail.stream().peek(item -> {
                        item.setId(null);
                        item.setRecVer(0L);
                        item.setBuildIn(false);
                        item.setLogicDel(false);
                        item.setHeaderId(headerId);
                    }).collect(Collectors.toList());
                    List<CommonIconTemplateDetailDto> treeifyList = UTree.buildTree(cacheDefaultCommonIconDetail);
                    saveTreeifyList(treeifyList);
                }
            }
        }


    }

    private void saveTreeifyList(List<CommonIconTemplateDetailDto> treeifyList) {
        if (UEmpty.isNotEmpty(treeifyList)) {
            for (CommonIconTemplateDetailDto commonIconTemplateDetailDto : treeifyList) {
                List<CommonIconTemplateDetailDto> children = commonIconTemplateDetailDto.getChildren();
                CommonIconTemplateDetailDto saveDto = commonIconTemplateDetailService.saveUpdate(commonIconTemplateDetailDto);
                if (saveDto != null && saveDto.getId() != null) {
                    children = children.stream().peek(item -> {
                        item.setId(null);
                        item.setRecVer(0L);
                        item.setBuildIn(false);
                        item.setLogicDel(false);
                        item.setHeaderId(saveDto.getId());
                    }).collect(Collectors.toList());
                    saveTreeifyList(children);
                }
            }
        }
    }

    private void findAndCache(CommonIconTemplateHeaderDto defaultCommonIconHeaderDto) {
        CommonIconTemplateHeaderPo defaultCommonIconHeaderPo = commonIconTemplateHeaderRepository.findFirstByBuildInTrueOrderByCreateTime();
        if (defaultCommonIconHeaderPo != null) {
            URedis.set(CacheConstant.DEFAULT_COMMON_ICON_HEADER, commonIconTemplateHeaderConverter.convertPo2Dto(defaultCommonIconHeaderPo));
        } else {
            URedis.set(CacheConstant.DEFAULT_COMMON_ICON_HEADER, defaultCommonIconHeaderDto);
        }
    }

    private List<LaySelect> buildLaySelects(List<CommonIconTemplateHeaderPo> commonIconTemplateHeaderPoList) {
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
