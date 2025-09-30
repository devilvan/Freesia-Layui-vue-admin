package com.freesia.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.BaseDto;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.entity.FindPageSysNoticeEntity;
import com.freesia.notice.entity.FindPublishedAnnouncementEntity;
import com.freesia.notice.mapper.SysNoticeMapper;
import com.freesia.notice.po.SysNoticePo;
import com.freesia.notice.repository.SysNoticeRepository;
import com.freesia.notice.service.SysNoticeService;
import com.freesia.notice.dto.MarkReadDto;
import com.freesia.notice.vo.SysNoticeVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 消息公告表 业务逻辑类
 * @date 2025-06-06
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNoticePo> implements SysNoticeService {
    private final SysNoticeRepository sysNoticeRepository;
    private final SysNoticeMapper sysNoticeMapper;

    @Override
    public SysNoticeDto saveUpdate(SysNoticeDto sysNoticeDto) {
        SysNoticePo sysNoticePo = UCopy.copyDto2Po(sysNoticeDto, SysNoticePo.class);
        SysNoticePo po = sysNoticeRepository.saveAndFlush(sysNoticePo);
        return UCopy.copyPo2Dto(po, SysNoticeDto.class);
    }

    @Override
    public List<SysNoticeDto> saveUpdateBatch(List<SysNoticeDto> list) {
        List<SysNoticePo> sysNoticePoList = UCopy.fullCopyList(list, SysNoticePo.class);
        return UCopy.fullCopyList(sysNoticeRepository.saveAllAndFlush(sysNoticePoList), SysNoticeDto.class);
    }

    @Override
    public TableResult<FindPageSysNoticeEntity> findPageSysNotice(SysNoticeDto sysNoticeDto, PageQuery pageQuery) {
        Page<FindPageSysNoticeEntity> pagePo = sysNoticeMapper.findPageSysNotice(sysNoticeDto, pageQuery.build());
        return TableResult.build(UCopy.convertPageEntity2Dto(pagePo, FindPageSysNoticeEntity.class));
    }

    @Override
    public SysNoticeDto findSysNotice(SysNoticeDto sysNoticeDto) {
        LambdaQueryWrapper<SysNoticePo> wrapper = new LambdaQueryWrapper<SysNoticePo>()
                .eq(SysNoticePo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysNoticeDto.getId()), SysNoticePo::getId, sysNoticeDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), SysNoticeDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysNotice(List<Long> idList) {
        removeBatchByIds(idList);
    }

    @Override
    public List<FindPublishedAnnouncementEntity> findPublishedAnnouncement() {
        List<FindPublishedAnnouncementEntity> findPublishedAnnouncementEntityList = sysNoticeMapper.findPublishedAnnouncement();
        for (FindPublishedAnnouncementEntity entity : findPublishedAnnouncementEntityList) {
            String content = entity.getContent();
            if (UEmpty.isNotEmpty(content)) {
                entity.setContent("【" + entity.getTypeName() + "】" + content);
            }
        }
        return findPublishedAnnouncementEntityList;
    }

    @Override
    public Integer findUnreadCount(SysNoticeVo sysNoticeVo) {
        SysNoticeDto sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        return sysNoticeMapper.findUnreadCount(sysNoticeDto);
    }

    @Override
    public Integer markRead(MarkReadDto markReadDto) {
        sysNoticeRepository.markRead(markReadDto);
        SysNoticeDto sysNoticeDto = new SysNoticeDto();
        sysNoticeDto.setType(markReadDto.getType());
        sysNoticeDto.setUserId(markReadDto.getUserId());
        return sysNoticeMapper.findUnreadCount(sysNoticeDto);
    }

    @Override
    public List<FindPageSysNoticeEntity> findListSysNotice(SysNoticeDto sysNoticeDto) {
        return sysNoticeMapper.findListSysNotice(sysNoticeDto);
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void checkSaveAnnouncement(Long userId) {
        List<FindPublishedAnnouncementEntity> publishedAnnouncementList = sysNoticeMapper.findPublishedAnnouncement();
        if (UEmpty.isNotEmpty(publishedAnnouncementList)) {
            List<Long> announcementIdList = publishedAnnouncementList.stream().map(BaseDto::getId).toList();
            SysNoticeDto sysNoticeDto = new SysNoticeDto();
            sysNoticeDto.setUserId(userId);
            sysNoticeDto.setIdList(announcementIdList);
            List<Long> existsIdList = sysNoticeMapper.findExistsAnnouncement(sysNoticeDto);
            // 取差集
            List<Long> disjunctionIdList = new ArrayList<>(CollUtil.disjunction(announcementIdList, existsIdList));
            if (UEmpty.isNotEmpty(disjunctionIdList)) {
                // 如果不存在则生成
                List<SysNoticePo> sysNoticePoList = sysNoticeRepository.findAllById(disjunctionIdList);
                sysNoticePoList = sysNoticePoList.stream().peek(item -> {
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
