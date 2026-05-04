package com.freesia.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SysColumnDetailConverter;
import com.freesia.dto.SysColumnDetailDto;
import com.freesia.mapper.SysColumnDetailMapper;
import com.freesia.po.SysColumnDetailPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.repository.SysColumnDetailRepository;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysColumnDetailService;
import com.freesia.util.UEmpty;
import com.freesia.vo.DefaultColumnVo;
import com.freesia.vo.SysColumnDetailVo;
import com.freesia.vo.SysColumnHeaderVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 业务逻辑类
 * @date 2026-03-27
 */
@Service
@RequiredArgsConstructor
public class SysColumnDetailServiceImpl extends BaseServiceImpl<SysColumnDetailMapper, SysColumnDetailVo, SysColumnDetailDto, SysColumnDetailPo> implements SysColumnDetailService {
    private final SysColumnDetailRepository sysColumnDetailRepository;
    private final SysColumnDetailMapper sysColumnDetailMapper;
    private final SysColumnDetailConverter sysColumnDetailConverter;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected MapStructConverter<SysColumnDetailVo, SysColumnDetailDto, SysColumnDetailPo> getMapStructConverter() {
        return sysColumnDetailConverter;
    }

    @Override
    protected JpaRepository<SysColumnDetailPo, Long> getRepository() {
        return sysColumnDetailRepository;
    }

    @Override
    protected Class<SysColumnDetailDto> getDtoClass() {
        return SysColumnDetailDto.class;
    }

    @Override
    protected Class<SysColumnDetailPo> getPoClass() {
        return SysColumnDetailPo.class;
    }

    @Override
    protected Wrapper<SysColumnDetailPo> buildQueryWrapper(@NonNull SysColumnDetailDto sysColumnDetailDto) {
        return new LambdaQueryWrapper<SysColumnDetailPo>()
                .eq(SysColumnDetailPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getId()), SysColumnDetailPo::getId, sysColumnDetailDto.getId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getUserId()), SysColumnDetailPo::getUserId, sysColumnDetailDto.getUserId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getHeaderId()), SysColumnDetailPo::getHeaderId, sysColumnDetailDto.getHeaderId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getMiddleId()), SysColumnDetailPo::getMiddleId, sysColumnDetailDto.getMiddleId())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getTitle()), SysColumnDetailPo::getTitle, sysColumnDetailDto.getTitle())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getName()), SysColumnDetailPo::getName, sysColumnDetailDto.getName())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getEnabled()), SysColumnDetailPo::getEnabled, sysColumnDetailDto.getEnabled())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getFixed()), SysColumnDetailPo::getFixed, sysColumnDetailDto.getFixed())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getEllipsisTooltip()), SysColumnDetailPo::getEllipsisTooltip, sysColumnDetailDto.getEllipsisTooltip())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getWidth()), SysColumnDetailPo::getWidth, sysColumnDetailDto.getWidth())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getMinWidth()), SysColumnDetailPo::getMinWidth, sysColumnDetailDto.getMinWidth())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getOrderNum()), SysColumnDetailPo::getOrderNum, sysColumnDetailDto.getOrderNum())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getSorted()), SysColumnDetailPo::getSorted, sysColumnDetailDto.getSorted())
                .eq(UEmpty.isNotEmpty(sysColumnDetailDto.getResizeFlag()), SysColumnDetailPo::getResizeFlag, sysColumnDetailDto.getResizeFlag())
                ;
    }

    @Override
    public List<SysColumnDetailDto> saveUpdateBatch(List<SysColumnDetailDto> list) {
        List<SysColumnDetailDto> afterSaveDtoList = super.saveUpdateBatch(list);
        if (UEmpty.isNotEmpty(afterSaveDtoList)) {
            SysColumnDetailDto columnDetailDto = afterSaveDtoList.get(0);
            String cacheKey = CacheConstant.SYS_COLUMN_DETAIL + '@' + columnDetailDto.getHeaderId() + '@' + columnDetailDto.getUserId();
            URedis.put(cacheKey, columnDetailDto.getHeaderId().toString(), afterSaveDtoList);
            URedis.expire(cacheKey, Duration.parse("PT" + "5H" + RandomUtil.randomInt(2, 11) + "M"));
            return afterSaveDtoList;
        }
        return null;
    }

    @Override
    public TableResult<SysColumnDetailDto> findPage(SysColumnDetailDto dto, PageQuery pageQuery) {
        Page<SysColumnDetailPo> page = sysColumnDetailMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysColumnDetailConverter.convertPagePo2Dto(page));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SysColumnDetailDto> findCacheList(SysColumnDetailDto sysColumnDetailDto) {
        Long headerId = sysColumnDetailDto.getHeaderId();
        Long userId = sysColumnDetailDto.getUserId();
//        String cacheKey = CacheConstant.SYS_COLUMN_DETAIL + '@' + headerId + '@' + userId;
//        List<SysColumnDetailDto> sysColumnDetailDtoList = (List<SysColumnDetailDto>) URedis.hashGet(cacheKey, headerId.toString());
//        if (UEmpty.isNotEmpty(sysColumnDetailDtoList)) {
//            return sysColumnDetailDtoList;
//        }
        List<SysColumnDetailPo> list = sysColumnDetailMapper.findList(sysColumnDetailDto);
//        if (UEmpty.isNotEmpty(list)) {
//            URedis.put(cacheKey, headerId.toString(), list);
//            URedis.expire(cacheKey, Duration.parse("PT" + "5H" + RandomUtil.randomInt(2, 11) + "M"));
//            return list;
//        }
        return sysColumnDetailConverter.convertBatchPo2Dto(list);
    }

    @Override
    public void resorted(SysColumnHeaderVo sysColumnHeaderVo) {
        List<DefaultColumnVo> defaultColumnVoList = sysColumnHeaderVo.getDefaultColumnVoList();
        // 删除该用户旧的排序信息
        SysColumnDetailDto sysColumnDetailDto = new SysColumnDetailDto();
        sysColumnDetailDto.setUserId(USecurity.getUserId());
        sysColumnDetailDto.setHeaderId(sysColumnHeaderVo.getId());
        List<SysColumnDetailPo> list = sysColumnDetailMapper.findList(sysColumnDetailDto);
        if (UEmpty.isNotEmpty(list)) {
            List<Long> idList = defaultColumnVoList.stream().map(DefaultColumnVo::getId).toList();
            List<SysColumnDetailPo> sortedList = sortByOrderedIds(idList, list);
            int orderNum = 10;
            for (SysColumnDetailPo sysColumnDetailPo : sortedList) {
                sysColumnDetailPo.setOrderNum(orderNum);
                orderNum += 10;
            }
            sysColumnDetailRepository.saveAll(sortedList);
        }
    }

    @Override
    public SysColumnDetailDto findOne(SysColumnDetailDto dto) {
        return sysColumnDetailConverter.convertPo2Dto(sysColumnDetailMapper.findOne(dto));
    }

    /**
     * 方式1：使用Map + 自定义Comparator（最高效）
     */
    public static List<SysColumnDetailPo> sortByOrderedIds(List<Long> orderedIds, List<SysColumnDetailPo> columns) {
        // 构建ID到对象的映射
        Map<Long, SysColumnDetailPo> idToColumnMap = columns.stream()
                .collect(Collectors.toMap(SysColumnDetailPo::getId, column -> column));
        // 按照orderedIds的顺序重新排列
        return orderedIds.stream()
                .filter(idToColumnMap::containsKey) // 过滤不存在的ID
                .map(idToColumnMap::get)
                .collect(Collectors.toList());
    }
}
