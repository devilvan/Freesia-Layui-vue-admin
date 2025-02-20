package com.freesia.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.SysUserImportEntity;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.pojo.BaseImportEntity;
import com.freesia.exception.ServiceException;
import com.freesia.satoken.constant.UserType;
import com.freesia.service.SysUserService;
import com.freesia.util.UCollection;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.validation.util.USpringValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 用户导入 Excel监听器
 * @date 2024-08-02
 */
@Slf4j
@AllArgsConstructor
public class UserImportListener<T extends BaseImportEntity> extends BaseImportEntityListener<T> {
    private final SysUserService sysUserService;
    private final String avatar;

    @Override
    public void invoke(T sysUserImportEntity, AnalysisContext context) {
        cachedDataList.add(sysUserImportEntity);
        if (cachedDataList.size() >= BATCH_COUNT) {
            transactionSaveSysUser();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        transactionSaveSysUser();
    }

    private void transactionSaveSysUser() {
        List<SysUserDto> sysUserDtoList = UCollection.optimizeInitialCapacityArrayList(cachedDataList.size());
        for (T sysUserImportEntity : cachedDataList) {
            // 数据校验
            errorMsg.addAll(USpringValidation.errorMsg(sysUserImportEntity));
            SysUserDto sysUserDto = buildSysUserDto((SysUserImportEntity) sysUserImportEntity);
            sysUserDtoList.add(sysUserDto);
        }
        if (UEmpty.isNotEmpty(errorMsg)) {
            throw new ServiceException(UCollection.join(errorMsg, "\n"));
        }
        if (sysUserDtoList.size() > 0) {
            // 过滤相同用户名的数据
            UCopy.SyncAdditionCollectionDto<SysUserDto> sysUserDtoSyncAdditionCollectionDto = UCopy.syncAddition(
                    sysUserDtoList,
                    sysUserDtoCollection -> sysUserDtoCollection,
                    sysUserDtoCollection -> {
                        final List<String> nonUniqueUserNameList = sysUserDtoCollection.stream().map(SysUserDto::getUserName).collect(Collectors.toList());
                        return sysUserService.findDistinctUserNameList(nonUniqueUserNameList);
                    },
                    SysUserDto::getUserName,
                    sysUserDto -> sysUserDto,
                    (outer, existInner) -> {
                        existInner.setNickName(outer.getNickName());
                        existInner.setEmail(outer.getEmail());
                        existInner.setUserType(outer.getUserType());
                        existInner.setGender(outer.getGender());
                        existInner.setRemark(outer.getRemark());
                        return existInner;
                    });
            System.out.println(JSONObject.toJSONString(sysUserDtoSyncAdditionCollectionDto));
            // 保存
            Collection<SysUserDto> mergeCollection = sysUserDtoSyncAdditionCollectionDto.getMergeCollection();
            if (UEmpty.isNotEmpty(mergeCollection)) {
                sysUserService.saveUpdateBatch(new ArrayList<>(mergeCollection));
            }
        }
        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    private SysUserDto buildSysUserDto(SysUserImportEntity sysUserImportEntity) {
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setUserName(sysUserImportEntity.getUserName());
        sysUserDto.setNickName(sysUserImportEntity.getNickName());
        sysUserDto.setEmail(sysUserImportEntity.getEmail());
        sysUserDto.setTelNo(sysUserImportEntity.getTelNo());
        sysUserDto.setPassword(BCrypt.hashpw(sysUserImportEntity.getPassword(), BCrypt.gensalt()));
        sysUserDto.setAccountStatus(FlagConstant.ENABLED);
        sysUserDto.setRemark(sysUserImportEntity.getRemark());
        sysUserDto.setAvatar(avatar);
        final String gender = sysUserImportEntity.getGender();
        if (UEmpty.isNotEmpty(gender)) {
            sysUserDto.setGender(gender);
        } else {
            sysUserDto.setGender("U");
        }
        if (UEmpty.isNotEmpty(sysUserImportEntity.getUserType())) {
            UserType userType = UserType.getInstanceByKey(sysUserImportEntity.getUserType());
            userType = Optional.ofNullable(userType).orElse(UserType.SYS_USER);
            sysUserDto.setUserType(userType.getUserType());
        } else {
            sysUserDto.setUserType(UserType.SYS_USER.getUserType());
        }
        return sysUserDto;
    }
}
