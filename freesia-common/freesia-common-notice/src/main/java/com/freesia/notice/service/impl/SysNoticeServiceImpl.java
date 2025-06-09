package com.freesia.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.mapper.SysNoticeMapper;
import com.freesia.notice.po.SysNoticePo;
import com.freesia.notice.repository.SysNoticeRepository;
import com.freesia.notice.service.SysNoticeService;
import com.freesia.po.BasePo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.sse.component.SseEmitterManager;
import com.freesia.sse.constant.SseTopic;
import com.freesia.sse.dto.SseMessageDto;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 消息公告表 业务逻辑类
 * @date 2025-06-06
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNoticePo> implements SysNoticeService {
    private final SysNoticeRepository sysNoticeRepository;
    private final SseEmitterManager sseEmitterManager;

    @Override
    public SysNoticeDto saveUpdate(SysNoticeDto sysNoticeDto) {
        SysNoticePo sysNoticePo = UCopy.copyDto2Po(sysNoticeDto, SysNoticePo.class);
        sysNoticePo.setPublisherId(USecurity.getUserId());
        SysNoticePo po = sysNoticeRepository.saveAndFlush(sysNoticePo);
        SseMessageDto sseMessageDto = new SseMessageDto();
        sseMessageDto.setTopicList(Collections.singletonList(SseTopic.GLOBAL_SSE.getKey()));
        sseMessageDto.setContent(sysNoticeDto.getContent());
        sseEmitterManager.publishAll(sseMessageDto);
        return UCopy.copyPo2Dto(po, SysNoticeDto.class);

    }

    @Override
    public List<SysNoticeDto> saveUpdateBatch(List<SysNoticeDto> list) {
        List<SysNoticePo> sysNoticePoList = UCopy.fullCopyList(list, SysNoticePo.class);
        return UCopy.fullCopyList(sysNoticeRepository.saveAllAndFlush(sysNoticePoList), SysNoticeDto.class);
    }

    @Override
    public TableResult<SysNoticeDto> findPageSysNotice(SysNoticeDto sysNoticeDto, PageQuery pageQuery) {
        LambdaQueryWrapper<SysNoticePo> wrapper = new LambdaQueryWrapper<SysNoticePo>()
                .eq(SysNoticePo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysNoticeDto.getId()), SysNoticePo::getId, sysNoticeDto.getId())
                .orderByDesc(BasePo::getId);
        Page<SysNoticePo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, SysNoticeDto.class));
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
}
