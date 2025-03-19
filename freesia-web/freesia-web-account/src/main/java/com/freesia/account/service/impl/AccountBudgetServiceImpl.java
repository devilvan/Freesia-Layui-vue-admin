package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.account.constant.BudgetType;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.FindBudgetCapacityDto;
import com.freesia.account.entity.FindBudgetCapacityEntity;
import com.freesia.account.exception.AccountException;
import com.freesia.account.mapper.AccountBudgetMapper;
import com.freesia.account.po.AccountBudgetPo;
import com.freesia.account.repository.AccountBudgetRepository;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.constant.FlagConstant;
import com.freesia.entity.EchartCapacityOptionEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 业务逻辑类
 * @date 2025-03-04
 */
@Service
@RequiredArgsConstructor
public class AccountBudgetServiceImpl extends ServiceImpl<AccountBudgetMapper, AccountBudgetPo> implements AccountBudgetService {
    private final AccountBudgetRepository accountBudgetRepository;
    private final AccountBudgetMapper accountBudgetMapper;

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
        AccountBudgetPo accountBudgetPo = UCopy.copyDto2Po(accountBudgetDto, AccountBudgetPo.class);
        accountBudgetPo = accountBudgetRepository.saveAndFlush(accountBudgetPo);
        return UCopy.copyPo2Dto(accountBudgetPo, AccountBudgetDto.class);
    }

    @Override
    public List<AccountBudgetDto> saveUpdateBatch(List<AccountBudgetDto> list) {
        List<AccountBudgetPo> accountBudgetPoList = UCopy.fullCopyList(list, AccountBudgetPo.class);
        return UCopy.fullCopyList(accountBudgetRepository.saveAllAndFlush(accountBudgetPoList), AccountBudgetDto.class);
    }

    @Override
    public TableResult<AccountBudgetDto> findPageAccountBudget(AccountBudgetDto accountBudget, PageQuery pageQuery) {
        LambdaQueryWrapper<AccountBudgetPo> wrapper = new LambdaQueryWrapper<AccountBudgetPo>()
                .eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(AccountBudgetPo::getUserId, accountBudget.getUserId())
                .eq(UEmpty.isNotEmpty(accountBudget.getId()), AccountBudgetPo::getId, accountBudget.getId());
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
            return TableResult.build(UCopy.convertPagePo2Dto(pagePo, AccountBudgetDto.class));
        }
        return TableResult.build();
    }

    @Override
    public AccountBudgetDto findAccountBudget(AccountBudgetDto accountBudget) {
        LambdaQueryWrapper<AccountBudgetPo> wrapper = new LambdaQueryWrapper<AccountBudgetPo>()
                .eq(AccountBudgetPo::getLogicDel, FlagConstant.DISABLED)
                .eq(AccountBudgetPo::getUserId, accountBudget.getUserId())
                .eq(UEmpty.isNotEmpty(accountBudget.getId()), AccountBudgetPo::getId, accountBudget.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), AccountBudgetDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccountBudget(List<Long> idList) {
        removeBatchByIds(idList);
    }

    @Override
    public List<EchartCapacityOptionEntity> findBudgetCapacity(FindBudgetCapacityDto findBudgetCapacityDto) {
        List<EchartCapacityOptionEntity> echartCapacityOptionEntityList = new ArrayList<>();
        List<AccountBudgetPo> accountCostPoList = accountBudgetMapper.findListBudget(findBudgetCapacityDto);
        if (UEmpty.isEmpty(accountCostPoList)) {
            return echartCapacityOptionEntityList;
        }
        String cacheKey = "findBudgetCapacity:" +
                findBudgetCapacityDto.getUserId() + "@" +
                findBudgetCapacityDto.getTenantId() + "@";
        List<EchartCapacityOptionEntity> echartCapacityOptionEntityListCache = URedis.get(cacheKey);
        if (UEmpty.isNotNull(echartCapacityOptionEntityListCache)) {
            return echartCapacityOptionEntityListCache;
        }
        for (AccountBudgetPo accountBudgetPo : accountCostPoList) {
            String budgetType = accountBudgetPo.getBudgetType();
            if (BudgetType.DAY.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findDayBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                    echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
                }
            } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findWeekBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                    echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
                }
            } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findMonthBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                    echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
                }
            } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findYearBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
                    echartCapacityOptionEntityList.add(echartCapacityOptionEntity);
                }
            } else if (BudgetType.CUSTOM.getCode().equals(budgetType)) {
                findBudgetCapacityDto.setDurationFrom(accountBudgetPo.getDurationFrom());
                findBudgetCapacityDto.setDurationTo(accountBudgetPo.getDurationTo());
                List<FindBudgetCapacityEntity> findBudgetCapacityEntityList = accountBudgetMapper.findCustomBudgetCapacity(findBudgetCapacityDto);
                if (UEmpty.isNotEmpty(findBudgetCapacityEntityList)) {
                    EchartCapacityOptionEntity echartCapacityOptionEntity = buildEchartCapacityOptionEntity(findBudgetCapacityEntityList, accountBudgetPo);
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
        FindBudgetCapacityEntity findBudgetCapacityEntity = findBudgetCapacityEntityList.get(0);
        double sumOutlay = findBudgetCapacityEntityList.stream().mapToDouble(item -> item.getOutlay().doubleValue()).sum();
        BigDecimal rate = new BigDecimal(sumOutlay)
                .divide(accountBudgetPo.getOutlay(), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        echartCapacityOptionEntity.setName(accountBudgetPo.getBudgetDesc());
        echartCapacityOptionEntity.setValue(rate);
        echartCapacityOptionEntity.setBudget(accountBudgetPo.getOutlay());
        echartCapacityOptionEntity.setOutlay(new BigDecimal(sumOutlay).setScale(2, RoundingMode.HALF_UP));
        echartCapacityOptionEntity.setBudgetType(accountBudgetPo.getBudgetType());
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
