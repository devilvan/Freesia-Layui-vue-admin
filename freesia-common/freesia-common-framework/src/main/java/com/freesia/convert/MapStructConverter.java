package com.freesia.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.exception.ServiceException;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description MapStruct转换通用父类
 * @date 2026-01-17
 */
@SuppressWarnings(value = "unused")
public interface MapStructConverter<VO, DTO, PO> {
    /**
     * VO转换为DTO
     *
     * @param source VO实例
     * @return DTO实例
     */
    DTO convertVo2Dto(VO source);

    /**
     * DTO转换为PO
     *
     * @param source DTO实例
     * @return PO实例
     */
    PO convertDto2Po(DTO source);

    /**
     * PO转换为DTO
     *
     * @param source PO实例
     * @return DTO实例
     */
    DTO convertPo2Dto(PO source);

    /**
     * 批量VO转换为DTO
     *
     * @param sourceList VO实例集合
     * @return DTO实例集合
     */
    default List<DTO> convertBatchVo2Dto(List<VO> sourceList) {
        if (UEmpty.isEmpty(sourceList)) {
            return null;
        }
        return sourceList.stream().map(this::convertVo2Dto).collect(Collectors.toList());
    }

    /**
     * 批量DTO转换为PO
     *
     * @param sourceList DTO实例集合
     * @return PO实例集合
     */
    default List<PO> convertBatchDto2Po(List<DTO> sourceList) {
        if (UEmpty.isEmpty(sourceList)) {
            return null;
        }
        return sourceList.stream().map(this::convertDto2Po).collect(Collectors.toList());
    }

    /**
     * 批量PO转换为DTO
     *
     * @param sourceList PO实例集合
     * @return DTO实例集合
     */
    default List<DTO> convertBatchPo2Dto(List<PO> sourceList) {
        if (UEmpty.isEmpty(sourceList)) {
            return null;
        }
        return sourceList.stream().map(this::convertPo2Dto).collect(Collectors.toList());
    }

    /**
     * 分页PO转换为DTO
     *
     * @param sourcePage PO分页实例
     * @return DTO分页实例
     */
    default Page<DTO> convertPagePo2Dto(Page<PO> sourcePage) {
        Page<DTO> pageDto = new Page<>();
        if (sourcePage == null) {
            return new Page<>();
        }
        if (UEmpty.isEmpty(sourcePage.getRecords())) {
            pageDto.setRecords(new ArrayList<>());
            pageDto.setSize(sourcePage.getSize());
            pageDto.setCurrent(sourcePage.getCurrent());
            pageDto.setTotal(sourcePage.getTotal());
            pageDto.setPages(sourcePage.getPages());
            return new Page<>();
        }
        try {
            List<DTO> targetList = this.convertBatchPo2Dto(sourcePage.getRecords());
            pageDto.setRecords(targetList);
            pageDto.setSize(sourcePage.getSize());
            pageDto.setCurrent(sourcePage.getCurrent());
            pageDto.setTotal(sourcePage.getTotal());
            pageDto.setPages(sourcePage.getPages());
        } catch (Exception e) {
            throw new ServiceException(UMessage.message("convert.failed", e.toString()));
        }
        return pageDto;
    }
}
