package com.freesia.account.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.constant.BudgetType;
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
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.entity.EchartCapacityOptionEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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
                .eq(AccountBudgetPo::getUserId, accountBudgetDto.getUserId())
                .eq(UEmpty.isNotEmpty(accountBudgetDto.getId()), AccountBudgetPo::getId, accountBudgetDto.getId());
    }

    @Override
    public AccountBudgetDto saveUpdate(AccountBudgetDto accountBudgetDto) {
        // 新增则需要校验是否已经设置了该预算类型的数据
        if (UEmpty.isNull(accountBudgetDto.getId())) {
            Long userId = accountBudgetDto.getUserId();
            String budgetType = accountBudgetDto.getBudgetType();
            if (!BudgetType.CUSTOM.getCode().equals(budgetType)) {
                LambdaQueryWrapper<AccountBudgetPo> wrapper = new LambdaQueryWrapper<AccountBudgetPo>()
                        .eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED)
                        .eq(AccountBudgetPo::getBudgetType, budgetType)
                        .eq(AccountBudgetPo::getUserId, userId);
                List<AccountBudgetPo> accountBudgetPoList = accountBudgetMapper.selectList(wrapper);
                if (UEmpty.isNotEmpty(accountBudgetPoList)) {
                    throw new AccountException("account.budget.exists", new Object[]{});
                }
            }

        }
        return super.saveUpdate(accountBudgetDto);
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
    public List<EchartCapacityOptionEntity> findBudgetCapacity(FindBudgetCapacityDto findBudgetCapacityDto) {
        List<EchartCapacityOptionEntity> echartCapacityOptionEntityList = new ArrayList<>();
        List<AccountBudgetPo> accountCostPoList = accountBudgetMapper.findListBudget(findBudgetCapacityDto);
//        if (UEmpty.isEmpty(accountCostPoList)) {
//            return echartCapacityOptionEntityList;
//        }
        String cacheKey = "findBudgetCapacity:" +
                findBudgetCapacityDto.getUserId() + "@" +
                findBudgetCapacityDto.getTenantId();
        List<EchartCapacityOptionEntity> echartCapacityOptionEntityListCache = URedis.get(cacheKey);
        if (UEmpty.isNotNull(echartCapacityOptionEntityListCache)) {
            return echartCapacityOptionEntityListCache;
        }
        for (AccountBudgetPo accountBudgetPo : accountCostPoList) {
            String budgetType = accountBudgetPo.getBudgetType();
            if (BudgetType.DAY.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findDayBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                echartCapacityOptionEntity.setDurationFrom(calendar.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                echartCapacityOptionEntity.setDurationTo(calendar.getTime());
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findWeekBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                echartCapacityOptionEntity.setDurationTo(calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_WEEK) + 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                echartCapacityOptionEntity.setDurationFrom(calendar.getTime());
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findMonthBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                echartCapacityOptionEntity.setDurationFrom(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                echartCapacityOptionEntity.setDurationTo(calendar.getTime());
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findYearBudgetCapacity(findBudgetCapacityDto);
                EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                echartCapacityOptionEntity.setDurationFrom(calendar.getTime());
                calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                echartCapacityOptionEntity.setDurationTo(calendar.getTime());
                echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
            } else if (BudgetType.CUSTOM.getCode().equals(budgetType)) {
                findBudgetCapacityDto.setDurationFrom(accountBudgetPo.getDurationFrom());
                findBudgetCapacityDto.setDurationTo(accountBudgetPo.getDurationTo());
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findCustomBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                    echartCapacityOptionEntity.setDurationFrom(accountBudgetPo.getDurationFrom());
                    echartCapacityOptionEntity.setDurationTo(accountBudgetPo.getDurationTo());
                    echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
                }
            }
        }
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
            URedis.set(cacheKey, echartCapacityOptionEntityList, Duration.ofSeconds(30));
        }
        return echartCapacityOptionEntityList;
    }

    private EchartCapacityOptionEntity buildEchartCapacityOptionEntity(List<FindBudgetCapacityEntity> findBudgetCapacityEntityList, AccountBudgetPo accountBudgetPo) {
        EchartCapacityOptionEntity echartCapacityOptionEntity = new EchartCapacityOptionEntity();
        BigDecimal sumOutlay = BigDecimal.ZERO;
        echartCapacityOptionEntity.setName(accountBudgetPo.getBudgetDesc());
        echartCapacityOptionEntity.setBudget(accountBudgetPo.getOutlay());
        echartCapacityOptionEntity.setOutlay(sumOutlay.setScale(2, RoundingMode.HALF_UP));
        echartCapacityOptionEntity.setBudgetType(accountBudgetPo.getBudgetType());
        if (UEmpty.isEmpty(findBudgetCapacityEntityList)) {
            return echartCapacityOptionEntity;
        }
        FindBudgetCapacityEntity findBudgetCapacityEntity = findBudgetCapacityEntityList.get(0);
        sumOutlay = findBudgetCapacityEntityList.stream()
                .map(AccountBudgetDto::getOutlay)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal rate = sumOutlay.divide(accountBudgetPo.getOutlay(), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        echartCapacityOptionEntity.setOutlay(sumOutlay.setScale(2, RoundingMode.HALF_UP));
        echartCapacityOptionEntity.setValue(Convert.toBigDecimal(rate, BigDecimal.ZERO));
        // 如果是自定义类型，则赋值自定义的时间范围
        if (BudgetType.CUSTOM.getCode().equals(accountBudgetPo.getBudgetType())) {
            echartCapacityOptionEntity.setDurationFrom(accountBudgetPo.getDurationFrom());
            echartCapacityOptionEntity.setDurationTo(accountBudgetPo.getDurationTo());
        } else {
            echartCapacityOptionEntity.setDurationFrom(findBudgetCapacityEntity.getDurationFrom());
            echartCapacityOptionEntity.setDurationTo(findBudgetCapacityEntity.getDurationTo());
        }
        return echartCapacityOptionEntity;
    }
}
