package com.freesia.icon.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.bean.CommonIconTemplateHeaderBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.icon.converter.CommonIconTemplateHeaderConverter;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;
import com.freesia.icon.dto.FindListSelectCostTypeDto;
import com.freesia.icon.mapper.CommonIconTemplateHeaderMapper;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
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
public class CommonIconTemplateHeaderServiceImpl extends BaseServiceImpl<CommonIconTemplateHeaderMapper, CommonIconTemplateHeaderVo, CommonIconTemplateHeaderDto, CommonIconTemplateHeaderPo> implements CommonIconTemplateHeaderService, CommonIconTemplateHeaderProviderService {
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
            defaultCommonIconHeaderDto = findAndCache(defaultCommonIconHeaderDto);
        } else {
            defaultCommonIconHeaderDto = URedis.get(CacheConstant.DEFAULT_COMMON_ICON_HEADER);
            if (UEmpty.isNotEmpty(defaultCommonIconHeaderDto) && defaultCommonIconHeaderDto.getId() != null) {
                return defaultCommonIconHeaderDto;
            }
            defaultCommonIconHeaderDto = findAndCache(defaultCommonIconHeaderDto);
        }
        return defaultCommonIconHeaderDto;
    }

    private CommonIconTemplateHeaderDto findAndCache(CommonIconTemplateHeaderDto defaultCommonIconHeaderDto) {
        CommonIconTemplateHeaderPo defaultCommonIconHeaderPo = commonIconTemplateHeaderRepository.findFirstByBuildInTrueOrderByCreateTime();
        if (defaultCommonIconHeaderPo != null) {
            defaultCommonIconHeaderDto = commonIconTemplateHeaderConverter.convertPo2Dto(defaultCommonIconHeaderPo);
            URedis.set(CacheConstant.DEFAULT_COMMON_ICON_HEADER, defaultCommonIconHeaderDto);
        } else {
            URedis.set(CacheConstant.DEFAULT_COMMON_ICON_HEADER, defaultCommonIconHeaderDto);
        }
        return defaultCommonIconHeaderDto;
    }

    @Override
    public CommonIconTemplateHeaderBean findDefaultCommonIconHeader() {
        CommonIconTemplateHeaderDto cacheDefaultCommonIconHeader = findCacheDefaultCommonIconHeader();
        return commonIconTemplateHeaderConverter.convertDto2Bean(cacheDefaultCommonIconHeader);
    }

    @Override
    public void initUserIconTemplate(Long userId) {
        // 根据用户ID查询用户是否已初始化图标模板
        Optional<CommonIconTemplateHeaderPo> findOne = commonIconTemplateHeaderRepository.findById(userId);
        if (findOne.isPresent()) {
            CommonIconTemplateHeaderPo commonIconTemplateHeaderPo = findOne.get();
            // 查询图标模板详情
            buildSaveTemplateDetail(commonIconTemplateHeaderPo.getId());
        } else {
            CommonIconTemplateHeaderDto cacheDefaultCommonIconHeader = findCacheDefaultCommonIconHeader();
            if (cacheDefaultCommonIconHeader != null && cacheDefaultCommonIconHeader.getId() != null) {
                cacheDefaultCommonIconHeader.setId(null);
                cacheDefaultCommonIconHeader.setRecVer(0L);
                cacheDefaultCommonIconHeader.setBuildIn(false);
                cacheDefaultCommonIconHeader.setLogicDel(false);
                cacheDefaultCommonIconHeader.setUserId(userId);
                cacheDefaultCommonIconHeader.setDefaultFlag(true);
                CommonIconTemplateHeaderDto headerDto = super.saveUpdate(cacheDefaultCommonIconHeader);
                if (headerDto != null) {
                    buildSaveTemplateDetail(headerDto.getId());
                }
            }
        }
    }

    private void buildSaveTemplateDetail(Long headerId) {
        Boolean flag = commonIconTemplateDetailService.findByHeaderIdExists(headerId);
        if (!flag) {
            // 生成该用户默认的图标模板
            List<CommonIconTemplateDetailDto> cacheDefaultCommonIconDetailList = commonIconTemplateDetailService.findCacheDefaultCommonIconDetail();
            if (UEmpty.isNotEmpty(cacheDefaultCommonIconDetailList)) {
                cacheDefaultCommonIconDetailList = cacheDefaultCommonIconDetailList.stream().peek(item -> {
                    item.setHeaderId(headerId);
                }).collect(Collectors.toList());
                List<CommonIconTemplateDetailDto> treeifyList = UTree.buildTree(cacheDefaultCommonIconDetailList);
                saveTreeifyList(AdminConstant.MENU_TOP_PARENT_ID, treeifyList);
            }
        }
    }

    private void saveTreeifyList(Long parentId, List<CommonIconTemplateDetailDto> treeifyList) {
        if (UEmpty.isNotEmpty(treeifyList) && parentId != null) {
            for (CommonIconTemplateDetailDto commonIconTemplateDetailDto : treeifyList) {
                commonIconTemplateDetailDto.setId(null);
                commonIconTemplateDetailDto.setRecVer(0L);
                commonIconTemplateDetailDto.setBuildIn(false);
                commonIconTemplateDetailDto.setLogicDel(false);
                commonIconTemplateDetailDto.setParentId(parentId);
                CommonIconTemplateDetailDto saveDto = commonIconTemplateDetailService.saveUpdate(commonIconTemplateDetailDto);
                List<CommonIconTemplateDetailDto> children = commonIconTemplateDetailDto.getChildren();
                saveTreeifyList(saveDto.getId(), children);
            }
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
