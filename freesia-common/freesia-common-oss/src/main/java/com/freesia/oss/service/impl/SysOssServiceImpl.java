package com.freesia.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.oss.constant.AccessPolicy;
import com.freesia.oss.dto.SysOssDto;
import com.freesia.oss.exception.OssException;
import com.freesia.oss.factory.OssFactory;
import com.freesia.oss.handler.OssHandler;
import com.freesia.oss.mapper.SysOssMapper;
import com.freesia.oss.po.SysOssPo;
import com.freesia.oss.repository.SysOssRepository;
import com.freesia.oss.service.SysOssService;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 业务逻辑类
 * @date 2024-02-27
 */
@Service
@RequiredArgsConstructor
public class SysOssServiceImpl extends ServiceImpl<SysOssMapper, SysOssPo> implements SysOssService {
    private final SysOssRepository sysOssRepository;

    @Override
    public SysOssDto saveUpdate(SysOssDto sysOssDto) {
        SysOssPo sysOssPo = new SysOssPo();
        UCopy.fullCopy(sysOssDto, sysOssPo);
        SysOssDto resultDto = new SysOssDto();
        UCopy.fullCopy(sysOssRepository.saveAndFlush(sysOssPo), resultDto);
        return resultDto;
    }

    @Override
    public List<SysOssDto> saveUpdateBatch(List<SysOssDto> list) {
        List<SysOssPo> sysOssPoList = UCopy.fullCopyList(list, SysOssPo.class);
        return UCopy.fullCopyList(sysOssRepository.saveAllAndFlush(sysOssPoList), SysOssDto.class);
    }

    @Override
    public TableResult<SysOssDto> findPageSysOss(SysOssDto sysOss, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOssPo> wrapper = new LambdaQueryWrapper<SysOssPo>()
                .eq(SysOssPo::getLogicDel, FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(sysOss.getId()), SysOssPo::getId, sysOss.getId());
        Page<SysOssPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, SysOssDto.class));
    }

    @Override
    public SysOssDto findSysOss(SysOssDto sysOss) {
        LambdaQueryWrapper<SysOssPo> wrapper = new LambdaQueryWrapper<SysOssPo>()
                .eq(SysOssPo::getLogicDel, FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(sysOss.getId()), SysOssPo::getId, sysOss.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), SysOssDto.class);
    }

    @Override
    public void deleteSysOss(List<Long> idList) {
        removeBatchByIds(idList);
    }

    @Override
    public SysOssDto upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.')))
                .orElseThrow(() -> new OssException("oss.file.required"));
        OssHandler ossHandler = OssFactory.getInstance();
        OssHandler.UploadResultEntity uploadResultEntity = new OssHandler.UploadResultEntity();
        try {
            uploadResultEntity = ossHandler.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 保存文件信息
        SysOssDto sysOssDto = new SysOssDto();
        sysOssDto.setFileName(uploadResultEntity.getFilename());
        sysOssDto.setOriginalName(originalFilename);
        sysOssDto.setFileSuffix(suffix);
        sysOssDto.setUrl(uploadResultEntity.getUrl());
        sysOssDto.setService(ossHandler.getConfigKey());
        setPrivateBucketExpirationUrl(sysOssDto);
        SysOssPo sysOssPo = UCopy.copyDto2Po(sysOssDto, SysOssPo.class);
        return UCopy.copyPo2Dto(sysOssRepository.save(sysOssPo), SysOssDto.class);
    }

    /**
     * 为私有桶设置URL过期时间
     *
     * @param sysOssDto OSS对象
     */
    private void setPrivateBucketExpirationUrl(SysOssDto sysOssDto) {
        OssHandler ossHandler = OssFactory.getInstance(sysOssDto.getService());
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicy.PRIVATE == ossHandler.getAccessPolicy()) {
            sysOssDto.setUrl(ossHandler.getPrivateUrl(sysOssDto.getFileName(), 120));
        }
    }
}
