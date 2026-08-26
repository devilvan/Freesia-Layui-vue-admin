package com.freesia.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.dto.BaseDto;
import com.freesia.converter.SysNoticeConverter;
import com.freesia.dto.MarkReadDto;
import com.freesia.dto.SysNoticeDto;
import com.freesia.entity.FindPageSysNoticeEntity;
import com.freesia.entity.FindPublishedAnnouncementEntity;
import com.freesia.mapper.SysNoticeMapper;
import com.freesia.po.SysNoticePo;
import com.freesia.repository.SysNoticeRepository;
import com.freesia.service.SysNoticeService;
import com.freesia.vo.SysNoticeVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息公告业务实现
 *
 * @author Evad.Wu
 * @date 2025-06-06
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends BaseServiceImpl<SysNoticeMapper, SysNoticeVo, SysNoticeDto, SysNoticePo> implements SysNoticeService {
    private final SysNoticeRepository sysNoticeRepository;
    private final SysNoticeMapper sysNoticeMapper;
    private final SysNoticeConverter sysNoticeConverter;

    @Override
    protected MapStructConverter<SysNoticeVo, SysNoticeDto, SysNoticePo> getMapStructConverter() {
        return sysNoticeConverter;
    }

    @Override
    protected JpaRepository<SysNoticePo, Long> getRepository() {
        return sysNoticeRepository;
    }

    @Override
    protected Class<SysNoticeDto> getDtoClass() {
        return SysNoticeDto.class;
    }

    @Override
    protected Class<SysNoticePo> getPoClass() {
        return SysNoticePo.class;
    }

    @Override
    protected Wrapper<SysNoticePo> buildQueryWrapper(@NonNull SysNoticeDto sysNoticeDto) {
        return new LambdaQueryWrapper<SysNoticePo>()
                .eq(SysNoticePo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysNoticeDto.getId()), SysNoticePo::getId, sysNoticeDto.getId());
    }

    @Override
    public TableResult<FindPageSysNoticeEntity> findPageSysNotice(SysNoticeDto sysNoticeDto, PageQuery pageQuery) {
        Page<FindPageSysNoticeEntity> pagePo = sysNoticeMapper.findPageSysNotice(sysNoticeDto, pageQuery.build());
        return TableResult.build(pagePo);
    }

    @Override
    public List<FindPublishedAnnouncementEntity> findPublishedAnnouncement() {
        List<FindPublishedAnnouncementEntity> findPublishedAnnouncementEntityList = sysNoticeMapper.findPublishedAnnouncement();
        for (FindPublishedAnnouncementEntity entity : findPublishedAnnouncementEntityList) {
            String content = entity.getContent();
            if (UEmpty.isNotEmpty(content)) {
                entity.setContent("《" + entity.getTypeName() + "》" + content);
            }
        }
        return findPublishedAnnouncementEntityList;
    }

    @Override
    public Integer findUnreadCount(SysNoticeVo sysNoticeVo) {
        SysNoticeDto sysNoticeDto = sysNoticeConverter.convertVo2Dto(sysNoticeVo);
        return sysNoticeMapper.findUnreadCount(sysNoticeDto);
    }

    @Override
    public Integer markRead(MarkReadDto markReadDto) {
        sysNoticeRepository.markRead(markReadDto);
        SysNoticeVo sysNoticeVo = new SysNoticeVo();
        sysNoticeVo.setType(markReadDto.getType());
        sysNoticeVo.setUserId(markReadDto.getUserId());
        sysNoticeVo.setCreateTimeFrom(markReadDto.getCreateTimeFrom());
        sysNoticeVo.setCreateTimeTo(markReadDto.getCreateTimeTo());
        return this.findUnreadCount(sysNoticeVo);
    }

    @Override
    public Integer markAllRead(String type, Long userId) {
        sysNoticeRepository.markAllRead(type, userId);
        SysNoticeVo sysNoticeVo = new SysNoticeVo();
        sysNoticeVo.setType(type);
        sysNoticeVo.setUserId(userId);
        return this.findUnreadCount(sysNoticeVo);
    }

    @Override
    public List<FindPageSysNoticeEntity> findListSysNotice(SysNoticeDto sysNoticeDto) {
        return sysNoticeMapper.findListSysNotice(sysNoticeDto);
    }

    @Override
    @Async
    public void checkSaveAnnouncement(Long userId) {
        List<FindPublishedAnnouncementEntity> publishedAnnouncementList = sysNoticeMapper.findPublishedAnnouncement();
        if (UEmpty.isNotEmpty(publishedAnnouncementList)) {
            List<Long> announcementIdList = publishedAnnouncementList.stream().map(BaseDto::getId).toList();
            SysNoticeDto sysNoticeDto = new SysNoticeDto();
            sysNoticeDto.setUserId(userId);
            sysNoticeDto.setAnnouncementIdList(announcementIdList);
            List<Long> existsIdList = sysNoticeMapper.findExistsAnnouncement(sysNoticeDto);
            if (existsIdList.size() < announcementIdList.size()) {
                List<Long> disjunctionIdList = new ArrayList<>(CollUtil.disjunction(existsIdList, announcementIdList));
                if (UEmpty.isNotEmpty(disjunctionIdList)) {
                    List<SysNoticePo> sysNoticePoList = sysNoticeRepository.findAllById(disjunctionIdList);
                    sysNoticePoList = sysNoticePoList.stream().peek(item -> {
                        item.setAnnouncementId(item.getId());
                        item.setId(null);
                        item.setCreator(null);
                        item.setCreateTime(null);
                        item.setModifier(null);
                        item.setModifyTime(null);
                        item.setTenantId(null);
                        item.setUserId(userId);
                        item.setReadFlag(false);
                    }).collect(Collectors.toList());
                    sysNoticeRepository.saveAll(sysNoticePoList);
                }
            }
        }
    }
}
