package com.freesia.icon.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.icon.entity.FindCommonIconTemplateDetailEntity;
import com.freesia.icon.entity.FindTreeIconTreeTypeEntity;
import com.freesia.icon.mapper.CommonIconTemplateDetailMapper;
import com.freesia.icon.po.CommonIconTemplateDetailPo;
import com.freesia.icon.repository.CommonIconTemplateDetailRepository;
import com.freesia.icon.service.CommonIconTemplateDetailService;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UTree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
        CommonIconTemplateDetailPo commonIconTemplateDetailPo = UCopy.copyDto2Po(commonIconTemplateDetailDto, CommonIconTemplateDetailPo.class);
        commonIconTemplateDetailDto = UCopy.copyPo2Dto(commonIconTemplateDetailRepository.saveAndFlush(commonIconTemplateDetailPo), CommonIconTemplateDetailDto.class);
        return commonIconTemplateDetailDto;
    }

    @Override
    public List<CommonIconTemplateDetailDto> saveUpdateBatch(CommonIconTemplateDetailDto dto) {
        List<CommonIconTemplateDetailPo> commonIconTemplateDetailPoList = new ArrayList<>();
        List<FindCommonIconEntity> multipleIconList = dto.getMultipleIconList();
        int orderNum = Convert.toInt(dto.getOrderNum(), 0) + 10;
        for (FindCommonIconEntity entity : multipleIconList) {
            CommonIconTemplateDetailPo po = new CommonIconTemplateDetailPo();
            UCopy.fullCopy(dto, po);
            po.setIconId(entity.getId());
            po.setName(entity.getName());
            po.setOrderNum(orderNum);
            commonIconTemplateDetailPoList.add(po);
            orderNum += 10;
        }
        List<CommonIconTemplateDetailPo> poList = commonIconTemplateDetailRepository.saveAll(commonIconTemplateDetailPoList);
        return UCopy.fullCopyList(poList, CommonIconTemplateDetailDto.class);
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
        OssHandler ossHandler = OssFactory.getInstance();
        for (FindTreeIconTreeTypeEntity findTreeIconTreeTypeEntity : findTreeIconTreeTypeEntityList) {
            findTreeIconTreeTypeEntity.setUrl(ossHandler.convertEndpoint2Domain(findTreeIconTreeTypeEntity.getUrl()));
        }
        return UTree.buildTree(findTreeIconTreeTypeEntityList);
    }

    @Override
    public Integer findMaxOrderNum(CommonIconTemplateDetailDto commonIconTemplateDetailDto) {
        return commonIconTemplateDetailMapper.findMaxOrderNum(commonIconTemplateDetailDto);
    }

    @Override
    public List<Map<String, String>> findGrouping(CommonIconTemplateDetailDto dto) {
        return commonIconTemplateDetailMapper.findGrouping(dto);
    }

    @Override
    public Map<String, List<FindTreeIconTreeTypeEntity>> findCustomIconTemplateDetail(CommonIconTemplateDetailDto dto) {
        List<FindTreeIconTreeTypeEntity> list = commonIconTemplateDetailMapper.findCustomIconTemplateDetail(dto);
        // 首先创建一个parentId到父项name的映射
        Map<Long, String> parentIdToNameMap = list.stream()
                .filter(item -> AdminConstant.MENU_TOP_PARENT_ID.equals(item.getParentId()))
                .collect(Collectors.toMap(FindTreeIconTreeTypeEntity::getId, FindTreeIconTreeTypeEntity::getName));
        // 获取所有父项name
        Set<String> allParentNames = new HashSet<>(parentIdToNameMap.values());
        // 创建结果Map，先初始化所有父项
        Map<String, List<FindTreeIconTreeTypeEntity>> result = allParentNames.stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> new ArrayList<>()
                ));
        // 填充子项
        list.stream()
                .filter(item -> !AdminConstant.MENU_TOP_PARENT_ID.equals(item.getParentId()))
                .filter(item -> parentIdToNameMap.containsKey(item.getParentId()))
                .forEach(item -> {
                    String parentName = parentIdToNameMap.get(item.getParentId());
                    result.get(parentName).add(item);
                });
        return result;
    }
}
