package com.freesia.icon.converter;

import com.freesia.bean.CommonIconTemplateDetailBean;
import com.freesia.convert.MapStructConverter;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.po.CommonIconTemplateDetailPo;
import com.freesia.icon.vo.CommonIconTemplateDetailVo;
import com.freesia.icon.vo.FindMaxOrderNumVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 图标模板详情 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommonIconTemplateDetailConverter extends MapStructConverter<CommonIconTemplateDetailVo, CommonIconTemplateDetailDto, CommonIconTemplateDetailPo> {
    @Mapping(target = "idList", ignore = true)
    @Override
    CommonIconTemplateDetailDto convertVo2Dto(CommonIconTemplateDetailVo source);

    @Mapping(target = "commonIconTemplateHeaderPo", ignore = true)
    @Override
    CommonIconTemplateDetailPo convertDto2Po(CommonIconTemplateDetailDto source);

    @Mapping(target = "multipleIconList", ignore = true)
    @Mapping(target = "idList", ignore = true)
    @Override
    CommonIconTemplateDetailDto convertPo2Dto(CommonIconTemplateDetailPo source);

    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "orderNum", ignore = true)
    @Mapping(target = "multipleIconList", ignore = true)
    @Mapping(target = "idList", ignore = true)
    @Mapping(target = "iconId", ignore = true)
    CommonIconTemplateDetailDto convertFindMaxOrderNumVo2CommonIconTemplateDetailDto(FindMaxOrderNumVo findMaxOrderNumVo);

    List<CommonIconTemplateDetailBean> convertBatchDto2Bean(List<CommonIconTemplateDetailDto> commonIconTemplateDetailDtoList);
}
