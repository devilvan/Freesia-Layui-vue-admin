package com.freesia.excel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.UserModule;
import com.freesia.constant.UserType;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.SysUserImportEntity;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.pojo.BaseImportEntity;
import com.freesia.exception.ServiceException;
import com.freesia.service.SysUserService;
import com.freesia.util.UCollection;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.USpringValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 用户导入 Excel数理类
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
            sysUserDtoList = sysUserDtoList.stream()
                    .filter(UCopy.distinctByKey(SysUserDto::getUserName))
                    .collect(Collectors.toList());
            // 查询是否有重复用户名
            final List<String> distinctUserNameList = sysUserDtoList.stream().map(SysUserDto::getUserName).collect(Collectors.toList());
            List<SysUserDto> distinctSysUserDtoList = sysUserService.findDistinctUserNameList(distinctUserNameList);
            if (UEmpty.isNotEmpty(distinctSysUserDtoList)) {
                final List<String> nonUniqueUserNameList = distinctSysUserDtoList.stream().map(SysUserDto::getUserName).collect(Collectors.toList());
                final String nonUniqueUserNameJoin = StrUtil.join("\n", nonUniqueUserNameList);
                throw new ServiceException(UserModule.SubModule.USER_IMPORT, "user.name.not.unique", nonUniqueUserNameJoin);
            }
            // 保存
            sysUserService.saveUpdateBatch(sysUserDtoList);
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
            final UserType userType = UserType.getInstanceByKey(sysUserImportEntity.getUserType());
            sysUserDto.setUserType(userType.getUserType());
        } else {
            sysUserDto.setUserType(UserType.SYS_USER.getUserType());
        }
        return sysUserDto;
    }
}
