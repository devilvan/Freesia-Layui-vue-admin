package com.freesia.excel;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.SysDictValueDto;
import com.freesia.entity.SysDictValueImportEntity;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.pojo.BaseImportEntity;
import com.freesia.exception.ServiceException;
import com.freesia.service.SysDictValueService;
import com.freesia.util.UCollection;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.validation.util.USpringValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 字典键导入 Excel监听器
 * @date 2025-01-20
 */
@Slf4j
@AllArgsConstructor
public class DictValueImportListener<T extends BaseImportEntity> extends BaseImportEntityListener<T> {
    private final SysDictValueService sysDictValueService;
    private final String dictKey;
    private final Long keyId;

    @Override
    public void invoke(T sysDictValueImportEntity, AnalysisContext context) {
        cachedDataList.add(sysDictValueImportEntity);
        if (cachedDataList.size() >= BATCH_COUNT) {
            transactionSaveSysDictValue();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        transactionSaveSysDictValue();
    }

    private void transactionSaveSysDictValue() {
        List<SysDictValueDto> sysDictValueDtoList = UCollection.optimizeInitialCapacityArrayList(cachedDataList.size());
        Integer maxOrderNum = Convert.toInt(sysDictValueService.findMaxOrderNumByKeyId(keyId), 0);
        maxOrderNum = (maxOrderNum / 10) * 10;
        for (T sysDictValueImportEntity : cachedDataList) {
            // 数据校验
            errorMsg.addAll(USpringValidation.errorMsg(sysDictValueImportEntity));
            SysDictValueDto sysDictValueDto = buildSysDictValueDto((SysDictValueImportEntity) sysDictValueImportEntity, maxOrderNum);
            sysDictValueDtoList.add(sysDictValueDto);
            maxOrderNum += 10;
        }
        if (UEmpty.isNotEmpty(errorMsg)) {
            throw new ServiceException(UCollection.join(errorMsg, "\n"));
        }
        if (UEmpty.isNotEmpty(sysDictValueDtoList)) {
            // 过滤相同用户名的数据
            UCopy.SyncAdditionCollectionDto<SysDictValueDto> sysDictValueSyncAdditionCollectionDto = UCopy.syncAddition(
                    sysDictValueDtoList,
                    sysDictValueDto -> sysDictValueDto,
                    sysDictValueDto -> {
                        List<String> distinctDictValueNameList = sysDictValueDto.stream().map(SysDictValueDto::getValueName).collect(Collectors.toList());
                        return sysDictValueService.findDistinctDictValueNameList(distinctDictValueNameList, dictKey, keyId);
                    },
                    SysDictValueDto::getValueName,
                    sysDictValueDto -> sysDictValueDto,
                    (outer, existInner) -> {
                        existInner.setValueName(outer.getValueName());
                        existInner.setValue(outer.getValue());
                        return existInner;
                    });
            log.info(JSONObject.toJSONString(sysDictValueSyncAdditionCollectionDto));
            // 保存
            Collection<SysDictValueDto> mergeCollection = sysDictValueSyncAdditionCollectionDto.getMergeCollection();
            if (UEmpty.isNotEmpty(mergeCollection)) {
                sysDictValueService.saveUpdateBatch(new ArrayList<>(mergeCollection));
            }
        }
        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    private SysDictValueDto buildSysDictValueDto(SysDictValueImportEntity sysDictValueImportEntity, Integer maxOrderNum) {
        SysDictValueDto sysDictValueDto = new SysDictValueDto();
        sysDictValueDto.setKeyId(keyId);
        sysDictValueDto.setDictKey(dictKey);
        sysDictValueDto.setOrderNum(maxOrderNum + 10);
        sysDictValueDto.setValueName(sysDictValueImportEntity.getValueName());
        sysDictValueDto.setValue(sysDictValueImportEntity.getValue());
        return sysDictValueDto;
    }
}
