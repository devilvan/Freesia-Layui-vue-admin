package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.SysTenantType;
import com.freesia.constant.UserModule;
import com.freesia.converter.SysUserConverter;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysUserDto;
import com.freesia.log.annotation.LogRecord;
import com.freesia.po.*;
import com.freesia.properties.LoginPasswordProperties;
import com.freesia.properties.MenuProperties;
import com.freesia.repository.*;
import com.freesia.satoken.constant.UserType;
import com.freesia.service.*;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 注册功能 业务逻辑实现类
 * @date 2023-08-22
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SysRegisterServiceImpl implements SysRegisterService {
    private final TransactionTemplate transactionTemplate;
    private final CommonIconTemplateHeaderProviderService commonIconTemplateHeaderProviderService;
    private final SysUserConverter sysUserConverter;
    private final LoginPasswordProperties loginPasswordProperties;
    private final SysUserRepository sysUserRepository;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleService sysRoleService;
    private final SysTenantRepository sysTenantRepository;
    private final SysTenantUserRepository sysTenantUserRepository;
    private final MenuProperties menuProperties;
    private final SysMenuRepository sysMenuRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;

    @Override
    @LogRecord(module = UserModule.USER_MANAGEMENT, subModule = UserModule.SubModule.REGISTER, message = "user.register")
    public SysUserPo register(SysUserDto sysUserDto) {
        return transactionTemplate.execute(status -> {
            SysUserPo sysUserPo = sysUserConverter.convertDto2Po(sysUserDto);
            sysUserPo.setPassword(org.springframework.security.crypto.bcrypt.BCrypt.hashpw(loginPasswordProperties.getInitPassword(), BCrypt.gensalt()));
            sysUserPo.setAccountStatus(FlagConstant.ENABLED);
            sysUserPo.setLogicDel(false);
            if (UEmpty.isEmpty(sysUserPo.getUserType())) {
                sysUserPo.setUserType(UserType.SYS_USER.getUserType());
            }
            sysUserPo = sysUserRepository.save(sysUserPo);

            // 赋予默认角色并分配菜单权限
            try {
                SysRoleDto defaultRole = sysRoleService.findCacheDefaultRole();
                if (ObjectUtil.isNotNull(defaultRole)) {
                    SysUserRolePo ur = new SysUserRolePo();
                    // 构建用户对应的角色
                    defaultRole.setId(null);
                    defaultRole.setRecVer(0L);
                    defaultRole.setBuildIn(false);
                    SysRoleDto afterSaveSysRoleDto = sysRoleService.saveUpdate(defaultRole);
                    ur.setSysUserRolePk(new SysUserRolePk(sysUserPo.getId(), afterSaveSysRoleDto.getId()));
                    sysUserRoleRepository.saveAndFlush(ur);
                    log.info("注册用户[{}]成功赋予角色: {}", sysUserDto.getUserName(), defaultRole.getRoleKey());
                    // 分配菜单权限
                    if (UEmpty.isNotEmpty(menuProperties.getPath())) {
                        List<SysMenuPo> sysMenuPoList = sysMenuRepository.findByPathIn(menuProperties.getPath());
                        List<Long> sysMenuIdList = sysMenuPoList.stream().map(BasePo::getId).toList();
                        List<SysRoleMenuPo> sysRoleMenuList = sysMenuIdList.stream().map(menuId -> new SysRoleMenuPo(new SysRoleMenuPk(menuId, afterSaveSysRoleDto.getId()))).toList();
                        sysRoleMenuRepository.saveAll(sysRoleMenuList);
                    }

                } else {
                    log.warn("注册用户[{}]未找到默认角色", sysUserDto.getUserName());
                }
            } catch (Exception e) {
                log.error("注册用户[{}]赋予默认角色失败: {}", sysUserDto.getUserName(), e.getMessage(), e);
            }
            // 初始化租户
            if (UEmpty.isEmpty(sysUserDto.getTenantId())) {
                SysTenantPo sysTenantPo = new SysTenantPo();
                String code = sysUserPo.getUserName() + "-" + "commonTenant";
                sysTenantPo.setCode(code);
                sysTenantPo.setName("我的账本");
                sysTenantPo.setType(SysTenantType.INDIVIDUAL.getCode());
                sysTenantPo.setStatus(true);
                sysTenantPo.setRemark(code);
                sysTenantPo.setContactName(sysUserPo.getUserName());
                sysTenantPo = sysTenantRepository.save(sysTenantPo);
                sysTenantUserRepository.save(new SysTenantUserPo(new SysTenantUserPk(sysTenantPo.getId(), sysUserPo.getId())));
            }
            // 初始化图标模板
            commonIconTemplateHeaderProviderService.initUserIconTemplate(sysUserPo.getId());
            return sysUserPo;
        });
    }
}
