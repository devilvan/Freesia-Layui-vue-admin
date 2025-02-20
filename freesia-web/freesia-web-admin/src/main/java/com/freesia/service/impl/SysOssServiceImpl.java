package com.freesia.service.impl;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysOssDto;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysOssMapper;
import com.freesia.oss.constant.AccessPolicy;
import com.freesia.oss.constant.OssModule;
import com.freesia.oss.exception.OssException;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.oss.util.UOssFile;
import com.freesia.po.BasePo;
import com.freesia.po.SysOssPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysOssRepository;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysOssService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.util.USpring;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.SAVE_OSS, message = "oss.save")
    public SysOssDto saveUpdate(SysOssDto sysOssDto) {
        SysOssPo sysOssPo = new SysOssPo();
        UCopy.fullCopy(sysOssDto, sysOssPo);
        SysOssDto resultDto = new SysOssDto();
        UCopy.fullCopy(sysOssRepository.saveAndFlush(sysOssPo), resultDto);
        return resultDto;
    }

    @Override
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.SAVE_OSS, message = "oss.save")
    public List<SysOssDto> saveUpdateBatch(List<SysOssDto> list) {
        List<SysOssPo> sysOssPoList = UCopy.fullCopyList(list, SysOssPo.class);
        return UCopy.fullCopyList(sysOssRepository.saveAllAndFlush(sysOssPoList), SysOssDto.class);
    }

    @Override
    public TableResult<SysOssDto> findPageSysOss(SysOssDto sysOss, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOssPo> wrapper = new LambdaQueryWrapper<SysOssPo>()
                .eq(SysOssPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysOss.getId()), SysOssPo::getId, sysOss.getId())
                .eq(SysOssPo::getTempFlag, false)
                .orderByDesc(SysOssPo::getCreateTime);
        Page<SysOssPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, SysOssDto.class));
    }

    @Override
    public SysOssDto findSysOss(SysOssDto sysOss) {
        LambdaQueryWrapper<SysOssPo> wrapper = new LambdaQueryWrapper<SysOssPo>()
                .eq(SysOssPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysOss.getId()), SysOssPo::getId, sysOss.getId())
                .eq(SysOssPo::getTempFlag, false);
        return UCopy.copyPo2Dto(getOne(wrapper), SysOssDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(module = OssModule.OSS_MANAGEMENT, subModule = OssModule.SubModule.DELETE_OSS, message = "oss.delete")
    public void deleteSysOss(List<Long> idList) {
        if (UEmpty.isNotEmpty(idList)) {
            Wrapper<SysOssPo> queryWrapper = new LambdaQueryWrapper<SysOssPo>()
                    .eq(SysOssPo::getLogicDel, FlagConstant.DISABLED)
                    .in(SysOssPo::getId, idList);
            List<SysOssPo> sysOssPoList = this.list(queryWrapper);
            for (SysOssPo sysOssPo : sysOssPoList) {
                OssHandler ossHandler = OssFactory.getInstance(sysOssPo.getService());
                ossHandler.delete(sysOssPo.getUrl());
            }
            removeBatchByIds(idList);
        }
    }

    @Override
    public List<SysOssDto> upload(List<MultipartFile> files) {
        List<SysOssDto> resultSysOssDtoList = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String suffix = Optional.of(file)
                    .map(MultipartFile::getOriginalFilename)
                    .map(m -> m.substring(m.lastIndexOf('.') + 1))
                    .orElseThrow(() -> new OssException("oss.file.required"));
            OssHandler ossHandler = OssFactory.getInstance();
            OssHandler.UploadResultEntity uploadResultEntity = new OssHandler.UploadResultEntity();
            try {
                uploadResultEntity = ossHandler.uploadSuffix(file.getBytes(), "." + suffix, file.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 保存文件信息
            SysOssDto sysOssDto = new SysOssDto();
            String filename = uploadResultEntity.getFilename();
            String url = ossHandler.convertEndpoint2Domain(uploadResultEntity.getUrl());
            filename = filename.substring(filename.lastIndexOf("/") + 1);
            sysOssDto.setFileName(filename);
            sysOssDto.setOriginalName(originalFilename);
            sysOssDto.setFileSuffix(suffix);
            sysOssDto.setUrl(url);
            sysOssDto.setService(ossHandler.getConfigKey());
            SysOssPo sysOssPo = UCopy.copyDto2Po(sysOssDto, SysOssPo.class);
            SysOssDto resultSysOssDto = UCopy.copyPo2Dto(sysOssRepository.save(sysOssPo), SysOssDto.class);
            resultSysOssDtoList.add(resultSysOssDto);
            // 保存操作日志
            SysSensitiveLogBean sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean assignButtonLogBean = new SysSensitiveLogBean();
                assignButtonLogBean.setModule(OssModule.OSS_MANAGEMENT);
                assignButtonLogBean.setSubModule(OssModule.SubModule.OSS_UPLOAD);
                assignButtonLogBean.setType(OssModule.SubModule.OSS_UPLOAD);
                assignButtonLogBean.setResult(FlagConstant.SUCCESS);
                assignButtonLogBean.setContextOld(null);
                assignButtonLogBean.setContext(null);
                assignButtonLogBean.setRemark(UMessage.message("oss.upload.success", originalFilename));
                return assignButtonLogBean;
            });
            USpring.context().publishEvent(sysSensitiveLogBean);
        }
        return resultSysOssDtoList;
    }

    @Override
    public SysOssDto uploadTemp(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = Optional.of(file)
                .map(MultipartFile::getOriginalFilename)
                .map(m -> m.substring(m.lastIndexOf('.') + 1))
                .orElseThrow(() -> new OssException("oss.file.required"));
        OssHandler ossHandler = OssFactory.getInstance();
        OssHandler.UploadResultEntity uploadResultEntity = new OssHandler.UploadResultEntity();
        try {
            uploadResultEntity = ossHandler.uploadTemp(file.getBytes(), "." + suffix, file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 保存文件信息
        SysOssDto sysOssDto = new SysOssDto();
        String filename = uploadResultEntity.getFilename();
        String url = ossHandler.convertEndpoint2Domain(uploadResultEntity.getUrl());
        filename = filename.substring(filename.lastIndexOf("/") + 1);
        sysOssDto.setFileName(filename);
        sysOssDto.setOriginalName(originalFilename);
        sysOssDto.setFileSuffix(suffix);
        sysOssDto.setUrl(url);
        sysOssDto.setService(ossHandler.getConfigKey());
        sysOssDto.setTempFlag(true);
        setPrivateBucketExpirationUrl(sysOssDto);
        SysOssPo sysOssPo = UCopy.copyDto2Po(sysOssDto, SysOssPo.class);
        SysOssDto resultSysOssDto = UCopy.copyPo2Dto(sysOssRepository.save(sysOssPo), SysOssDto.class);
        // 保存操作日志
        SysSensitiveLogBean sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
            SysSensitiveLogBean assignButtonLogBean = new SysSensitiveLogBean();
            assignButtonLogBean.setModule(OssModule.OSS_MANAGEMENT);
            assignButtonLogBean.setSubModule(OssModule.SubModule.OSS_UPLOAD);
            assignButtonLogBean.setType(OssModule.SubModule.OSS_UPLOAD);
            assignButtonLogBean.setResult(FlagConstant.SUCCESS);
            assignButtonLogBean.setContextOld(null);
            assignButtonLogBean.setContext(null);
            assignButtonLogBean.setRemark(UMessage.message("oss.upload.success", originalFilename));
            return assignButtonLogBean;
        });
        USpring.context().publishEvent(sysSensitiveLogBean);
        return resultSysOssDto;
    }

    @Override
    public void download(Long id, HttpServletResponse response) {
        SysOssDto sysOssDto = USpring.getAopProxy(this).findCacheById(id);
        sysOssDto = Optional.of(sysOssDto).orElseThrow(() -> new OssException("oss.file.not.found", new Object[]{id}));
        // 设置响应体选项
        String originalName = sysOssDto.getOriginalName();
        UOssFile.setAttachmentResponseHeader(response, originalName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
        OssHandler ossHandler = OssFactory.getInstance(sysOssDto.getService());
        InputStream inputStream = ossHandler.getObjectContent(sysOssDto.getUrl());
        try {
            int available = inputStream.available();
            IoUtil.copy(inputStream, response.getOutputStream(), available);
            // 保存操作日志
            SysSensitiveLogBean sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean assignButtonLogBean = new SysSensitiveLogBean();
                assignButtonLogBean.setModule(OssModule.OSS_MANAGEMENT);
                assignButtonLogBean.setSubModule(OssModule.SubModule.OSS_DOWNLOAD);
                assignButtonLogBean.setType(OssModule.SubModule.OSS_DOWNLOAD);
                assignButtonLogBean.setResult(FlagConstant.SUCCESS);
                assignButtonLogBean.setContextOld(null);
                assignButtonLogBean.setContext(null);
                assignButtonLogBean.setRemark(UMessage.message("oss.download.success", originalName));
                return assignButtonLogBean;
            });
            USpring.context().publishEvent(sysSensitiveLogBean);
        } catch (IOException e) {
            SysSensitiveLogBean sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean assignButtonLogBean = new SysSensitiveLogBean();
                assignButtonLogBean.setModule(OssModule.OSS_MANAGEMENT);
                assignButtonLogBean.setSubModule(OssModule.SubModule.OSS_DOWNLOAD);
                assignButtonLogBean.setType(OssModule.SubModule.OSS_DOWNLOAD);
                assignButtonLogBean.setResult(FlagConstant.FAILED);
                assignButtonLogBean.setContextOld(null);
                assignButtonLogBean.setContext(null);
                assignButtonLogBean.setRemark(e.getMessage());
                return assignButtonLogBean;
            });
            USpring.context().publishEvent(sysSensitiveLogBean);
            throw new OssException(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = CacheConstant.SYS_OSS, key = "#id")
    public SysOssDto findCacheById(Long id) {
        return UCopy.copyPo2Dto(getById(id), SysOssDto.class);
    }

    @Override
    public void initDeleteTempFile() {
        LambdaQueryWrapper<SysOssPo> wrapper = new LambdaQueryWrapper<SysOssPo>()
                .select(SysOssPo::getId)
                .eq(SysOssPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysOssPo::getTempFlag, true);
        List<SysOssPo> sysOssPoList = this.list(wrapper);
        List<Long> idList = sysOssPoList.stream().map(BasePo::getId).collect(Collectors.toList());
        deleteSysOss(idList);
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
            sysOssDto.setUrl(ossHandler.getPrivateUrl(sysOssDto.getFileName(), 60));
        }
    }
}
