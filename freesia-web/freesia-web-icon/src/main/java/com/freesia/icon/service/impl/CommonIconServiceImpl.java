package com.freesia.icon.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.icon.entity.FindPageCommonIconEntity;
import com.freesia.icon.mapper.CommonIconMapper;
import com.freesia.icon.po.CommonIconPo;
import com.freesia.icon.repository.CommonIconRepository;
import com.freesia.icon.service.CommonIconService;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysOssService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 通用图标表 业务逻辑类
 * @date 2025-03-26
 */
@Service
@RequiredArgsConstructor
public class CommonIconServiceImpl extends ServiceImpl<CommonIconMapper, CommonIconPo> implements CommonIconService {
    private final CommonIconRepository commonIconRepository;
    private final CommonIconMapper commonIconMapper;
    private final SysOssService sysOssService;

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
    public TableResult<FindPageCommonIconEntity> findPageCommonIcon(CommonIconDto commonIconDto, PageQuery pageQuery) {
        Page<FindPageCommonIconEntity> findPageCommonIconEntityPage = commonIconMapper.findPageCommonIcon(commonIconDto, pageQuery.build());
        List<FindPageCommonIconEntity> newRecordsList = Optional.of(findPageCommonIconEntityPage).map(Page::getRecords).map(list -> {
            OssHandler instance = OssFactory.getInstance();
            for (FindPageCommonIconEntity findPageCommonIconEntity : list) {
                findPageCommonIconEntity.setUrl(instance.convertEndpoint2Domain(findPageCommonIconEntity.getUrl()));
            }
            return list;
        }).orElseGet(ArrayList::new);
        findPageCommonIconEntityPage.setRecords(newRecordsList);
        return TableResult.build(findPageCommonIconEntityPage);
    }

    @Override
    public FindCommonIconEntity findCommonIcon(CommonIconDto commonIconDto) {
        FindCommonIconEntity findCommonIconEntity = commonIconMapper.findCommonIcon(commonIconDto);
        return Optional.of(findCommonIconEntity).map(item -> {
            OssHandler instance = OssFactory.getInstance();
            item.setUrl(instance.convertEndpoint2Domain(item.getUrl()));
            return item;
        }).orElseGet(FindCommonIconEntity::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonIcon(List<Long> idList) {
        List<CommonIconPo> commonIconPoList = commonIconRepository.findAllById(idList);
        List<Long> fileIdList = commonIconPoList.stream().map(CommonIconPo::getFileId).collect(Collectors.toList());
        if (UEmpty.isNotEmpty(fileIdList)) {
            sysOssService.deleteSysOss(fileIdList);
        }
        removeBatchByIds(idList);
    }

    @Override
    public int findMaxOrderNumByIconPartition(String iconPartition) {
        return commonIconMapper.findMaxOrderNumByIconPartition(iconPartition);
    }
}
