package com.freesia.account.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.converter.AccountBudgetConverter;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.FindBudgetCapacityDto;
import com.freesia.account.entity.FindBudgetCapacityEntity;
import com.freesia.account.exception.AccountException;
import com.freesia.account.mapper.AccountBudgetMapper;
import com.freesia.account.po.AccountBudgetPo;
import com.freesia.account.repository.AccountBudgetRepository;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.vo.AccountBudgetVo;
import com.freesia.constant.BudgetType;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.entity.EchartCapacityOptionEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 业务逻辑类
 * @date 2025-03-04
 */
@Service
@RequiredArgsConstructor
public class AccountBudgetServiceImpl extends BaseServiceImpl<AccountBudgetMapper, AccountBudgetVo, AccountBudgetDto, AccountBudgetPo> implements AccountBudgetService {
    private final AccountBudgetRepository accountBudgetRepository;
    private final AccountBudgetMapper accountBudgetMapper;
    private final AccountBudgetConverter accountBudgetConverter;
    private final TransactionTemplate transactionTemplate;


    @Override
    protected MapStructConverter<AccountBudgetVo, AccountBudgetDto, AccountBudgetPo> getMapStructConverter() {
        return accountBudgetConverter;
    }

    @Override
    protected JpaRepository<AccountBudgetPo, Long> getRepository() {
        return accountBudgetRepository;
    }

    @Override
    protected Class<AccountBudgetDto> getDtoClass() {
        return AccountBudgetDto.class;
    }

    @Override
    protected Class<AccountBudgetPo> getPoClass() {
        return AccountBudgetPo.class;
    }

    @Override
    protected Wrapper<AccountBudgetPo> buildQueryWrapper(@NonNull AccountBudgetDto accountBudgetDto) {
        return new LambdaQueryWrapper<AccountBudgetPo>()
                .eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountBudgetDto.getId()), AccountBudgetPo::getId, accountBudgetDto.getId())
                .eq(UEmpty.isNotEmpty(accountBudgetDto.getUserId()), AccountBudgetPo::getUserId, accountBudgetDto.getUserId())
                .eq(UEmpty.isNotEmpty(accountBudgetDto.getBudgetType()), AccountBudgetPo::getBudgetType, accountBudgetDto.getBudgetType())
                .eq(UEmpty.isNotEmpty(accountBudgetDto.getId()), AccountBudgetPo::getId, accountBudgetDto.getId());
    }

    @Override
    public AccountBudgetDto saveUpdate(AccountBudgetDto accountBudgetDto) {
        Long userId = accountBudgetDto.getUserId();
        // 新增则需要校验是否已经设置了该预算类型的数据
        if (UEmpty.isNull(accountBudgetDto.getId())) {
            String budgetType = accountBudgetDto.getBudgetType();
            if (!BudgetType.CUSTOM.getCode().equals(budgetType)) {
                LambdaQueryWrapper<AccountBudgetPo> wrapper = new LambdaQueryWrapper<AccountBudgetPo>().eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED).eq(AccountBudgetPo::getBudgetType, budgetType).eq(AccountBudgetPo::getUserId, userId);
                List<AccountBudgetPo> accountBudgetPoList = accountBudgetMapper.selectList(wrapper);
                if (UEmpty.isNotEmpty(accountBudgetPoList)) {
                    throw new AccountException("account.budget.exists", new Object[]{});
                }
            }
        }
        return transactionTemplate.execute(status -> {
            AccountBudgetDto afterSaveAccountBudgetDto = super.saveUpdate(accountBudgetDto);
            cacheBudget(userId);
            return afterSaveAccountBudgetDto;
        });
    }

    @Override
    public void cacheBudget(Long userId) {
        AccountBudgetDto queryParam = new AccountBudgetDto();
        queryParam.setUserId(userId);
        List<AccountBudgetDto> accountBudgetDtoList = super.findList(queryParam);
        if (UEmpty.isNotEmpty(accountBudgetDtoList)) {
            // 20260302-Bliss 保存预算时添加到缓存
            String cacheKey = CacheConstant.FIND_BUDGET + userId;
            URedis.set(cacheKey, accountBudgetDtoList);
        }
    }

    @Override
    public TableResult<AccountBudgetDto> findPage(AccountBudgetDto accountBudget, PageQuery pageQuery) {
        Wrapper<AccountBudgetPo> wrapper = buildQueryWrapper(accountBudget);
        Page<AccountBudgetPo> pagePo = page(pageQuery.build(), wrapper);
        if (pagePo != null) {
            List<AccountBudgetPo> records = pagePo.getRecords();
            records.sort(Comparator.comparingInt(e -> {
                String budgetType = e.getBudgetType();
                if (BudgetType.CUSTOM.getCode().equals(budgetType)) {
                    return 5;
                } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
                    return 4;
                } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
                    return 3;
                } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
                    return 2;
                } else if (BudgetType.DAY.getCode().equals(budgetType)) {
                    return 1;
                } else {
                    return Integer.MAX_VALUE;
                }
            }));
            return TableResult.build(accountBudgetConverter.convertPagePo2Dto(pagePo));
        }
        return TableResult.build();
    }

    @Override
    public AccountBudgetDto findOne(AccountBudgetDto dto) {
        return super.findOne(dto);
    }

    @Override
    public List<EchartCapacityOptionEntity> findBudgetCapacity(FindBudgetCapacityDto findBudgetCapacityDto) {
        List<EchartCapacityOptionEntity> echartCapacityOptionEntityList = new ArrayList<>();
        List<AccountBudgetDto> accountBudgetDtoList = accountBudgetMapper.findListBudget(findBudgetCapacityDto);
        if (UEmpty.isEmpty(accountBudgetDtoList)) {
            return echartCapacityOptionEntityList;
        }
        for (AccountBudgetDto accountBudgetDto : accountBudgetDtoList) {
            String budgetType = accountBudgetDto.getBudgetType();
            if (BudgetType.DAY.getCode().equals(budgetType)) {
                setCurrentPeriod(findBudgetCapacityDto, BudgetType.DAY);
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findDayBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetDto);
                echartCapacityOptionEntity.buildDuration(budgetType);
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
                setCurrentPeriod(findBudgetCapacityDto, BudgetType.WEEK);
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findWeekBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetDto);
                echartCapacityOptionEntity.buildDuration(budgetType);
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
                setCurrentPeriod(findBudgetCapacityDto, BudgetType.MONTH);
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findMonthBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetDto);
                echartCapacityOptionEntity.buildDuration(budgetType);
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
                setCurrentPeriod(findBudgetCapacityDto, BudgetType.YEAR);
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findYearBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetDto);
                echartCapacityOptionEntity.buildDuration(budgetType);
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.CUSTOM.getCode().equals(budgetType)) {
                Date durationFrom = accountBudgetDto.getDurationFrom();
                Date durationTo = accountBudgetDto.getDurationTo();
                findBudgetCapacityDto.setDurationFrom(durationFrom);
                findBudgetCapacityDto.setDurationTo(durationTo);
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findCustomBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetDto);
                    if (!DateUtil.isIn(new Date(), durationFrom, durationTo)) {
                        echartCapacityOptionEntity.setName(UMessage.message("budget.expired", echartCapacityOptionEntity.getName()));
                    }
                    echartCapacityOptionEntity.setDurationFrom(durationFrom);
                    echartCapacityOptionEntity.setDurationTo(durationTo);
                    echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
                }
            }
        }
        sortEchartCapacityOptionEntityList(echartCapacityOptionEntityList);
        return echartCapacityOptionEntityList;
    }

    /**
     * 设置当前时间周期
     *
     * @param findBudgetCapacityDto 预算容量查询参数
     * @param budgetType            预算类型
     */
    private void setCurrentPeriod(FindBudgetCapacityDto findBudgetCapacityDto, BudgetType budgetType) {
        Date now = new Date();
        switch (budgetType) {
            case DAY -> {
                findBudgetCapacityDto.setDurationFrom(DateUtil.beginOfDay(now));
                findBudgetCapacityDto.setDurationTo(DateUtil.endOfDay(now));
            }
            case WEEK -> {
                findBudgetCapacityDto.setDurationFrom(DateUtil.beginOfWeek(now));
                findBudgetCapacityDto.setDurationTo(DateUtil.endOfWeek(now));
            }
            case MONTH -> {
                findBudgetCapacityDto.setDurationFrom(DateUtil.beginOfMonth(now));
                findBudgetCapacityDto.setDurationTo(DateUtil.endOfMonth(now));
            }
            case YEAR -> {
                findBudgetCapacityDto.setDurationFrom(DateUtil.beginOfYear(now));
                findBudgetCapacityDto.setDurationTo(DateUtil.endOfYear(now));
            }
            default -> {
            }
        }
    }

    /**
     * 排序容量图实体列表
     *
     * @param echartCapacityOptionEntityList 容量图实体列表
     */
    private void sortEchartCapacityOptionEntityList(List<EchartCapacityOptionEntity> echartCapacityOptionEntityList) {
        if (UEmpty.isNotEmpty(echartCapacityOptionEntityList)) {
            echartCapacityOptionEntityList.sort(Comparator.comparingInt(e -> {
                String budgetType = e.getBudgetType();
                if (BudgetType.CUSTOM.getCode().equals(budgetType)) {
                    return 5;
                } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
                    return 4;
                } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
                    return 3;
                } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
                    return 2;
                } else if (BudgetType.DAY.getCode().equals(budgetType)) {
                    return 1;
                } else {
                    return Integer.MAX_VALUE;
                }
            }));
        }
    }

    private EchartCapacityOptionEntity buildEchartCapacityOptionEntity(List<FindBudgetCapacityEntity> findBudgetCapacityEntityList, AccountBudgetDto accountBudgetDto) {
        EchartCapacityOptionEntity echartCapacityOptionEntity = new EchartCapacityOptionEntity();
        BigDecimal sumOutlay = BigDecimal.ZERO;
        echartCapacityOptionEntity.setId(accountBudgetDto.getId());
        echartCapacityOptionEntity.setName(accountBudgetDto.getBudgetDesc());
        echartCapacityOptionEntity.setBudget(accountBudgetDto.getOutlay());
        echartCapacityOptionEntity.setOutlay(sumOutlay.setScale(2, RoundingMode.HALF_UP));
        echartCapacityOptionEntity.setBudgetType(accountBudgetDto.getBudgetType());
        echartCapacityOptionEntity.setValue(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        echartCapacityOptionEntity.setTenantName(accountBudgetDto.getTenantName());
        if (UEmpty.isEmpty(findBudgetCapacityEntityList)) {
            return echartCapacityOptionEntity;
        }
        FindBudgetCapacityEntity findBudgetCapacityEntity = findBudgetCapacityEntityList.get(0);
        sumOutlay = findBudgetCapacityEntityList.stream().map(AccountBudgetDto::getOutlay).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal rate = sumOutlay.divide(accountBudgetDto.getOutlay(), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        echartCapacityOptionEntity.setOutlay(sumOutlay.setScale(2, RoundingMode.HALF_UP));
        echartCapacityOptionEntity.setSaveUp(accountBudgetDto.getOutlay().subtract(sumOutlay));
        echartCapacityOptionEntity.setValue(rate);
        // 如果是自定义类型，则赋值自定义的时间范围
        if (BudgetType.CUSTOM.getCode().equals(accountBudgetDto.getBudgetType())) {
            echartCapacityOptionEntity.setDurationFrom(accountBudgetDto.getDurationFrom());
            echartCapacityOptionEntity.setDurationTo(accountBudgetDto.getDurationTo());
        } else {
            echartCapacityOptionEntity.setDurationFrom(findBudgetCapacityEntity.getDurationFrom());
            echartCapacityOptionEntity.setDurationTo(findBudgetCapacityEntity.getDurationTo());
        }
        return echartCapacityOptionEntity;
    }
}
