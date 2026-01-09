package com.freesia.icon.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.icon.entity.FindPageCommonIconEntity;
import com.freesia.icon.mapper.CommonIconMapper;
import com.freesia.icon.po.CommonIconPo;
import com.freesia.icon.repository.CommonIconRepository;
import com.freesia.icon.service.CommonIconService;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssService;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 通用图标表 业务逻辑类
 * @date 2025-03-26
 */
@Service
@RequiredArgsConstructor
public class CommonIconServiceImpl extends BaseServiceImpl<CommonIconMapper, CommonIconPo, CommonIconDto> implements CommonIconService {
    private final CommonIconRepository commonIconRepository;
    private final CommonIconMapper commonIconMapper;
    private final SysOssService sysOssService;


    @Override
    protected JpaRepository<CommonIconPo, Long> getRepository() {
        return commonIconRepository;
    }

    @Override
    protected Class<CommonIconDto> getDtoClass() {
        return CommonIconDto.class;
    }

    @Override
    protected Class<CommonIconPo> getPoClass() {
        return CommonIconPo.class;
    }

    @Override
    protected Wrapper<CommonIconPo> buildQueryWrapper(@NonNull CommonIconDto dto) {
        return null;
    }

    @Override
    public TableResult<FindPageCommonIconEntity> findPageCommonIcon(CommonIconDto commonIconDto, PageQuery pageQuery) {
        Page<FindPageCommonIconEntity> findPageCommonIconEntityPage = commonIconMapper.findPageCommonIcon(commonIconDto, pageQuery.build());
        return TableResult.build(findPageCommonIconEntityPage);
    }

    @Override
    public FindCommonIconEntity findCommonIcon(CommonIconDto commonIconDto) {
        return commonIconMapper.findCommonIcon(commonIconDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonIcon(List<Long> idList) {
        List<CommonIconPo> commonIconPoList = commonIconRepository.findAllById(idList);
        List<Long> fileIdList = commonIconPoList.stream().map(CommonIconPo::getFileId).collect(Collectors.toList());
        if (UEmpty.isNotEmpty(fileIdList)) {
            sysOssService.deleteBatch(fileIdList);
        }
        removeBatchByIds(idList);
    }

    @Override
    public Map<String, List<FindCommonIconEntity>> findCommonIconPicker(CommonIconDto commonIconDto) {
        List<FindCommonIconEntity> findCommonIconEntityList = commonIconMapper.findCommonIconPicker(commonIconDto);
        return findCommonIconEntityList.stream().collect(Collectors.groupingBy(FindCommonIconEntity::getIconPartitionName));
    }

    @Override
    public List<FindCommonIconEntity> findListCommonIcon(CommonIconDto commonIconDto) {
        return commonIconMapper.findListCommonIcon(commonIconDto);
    }
}
