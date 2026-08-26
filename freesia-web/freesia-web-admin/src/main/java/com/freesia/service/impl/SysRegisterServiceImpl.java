package com.freesia.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.SysTenantType;
import com.freesia.constant.UserModule;
import com.freesia.converter.SysUserConverter;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysDeptDto;
import com.freesia.dto.SysUserDto;
import com.freesia.exception.ServiceException;
import com.freesia.log.annotation.LogRecord;
import com.freesia.po.*;
import com.freesia.properties.LoginPasswordProperties;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.repository.SysTenantRepository;
import com.freesia.repository.SysTenantUserRepository;
import com.freesia.repository.SysUserRepository;
import com.freesia.repository.SysUserRoleRepository;
import com.freesia.satoken.constant.UserType;
import com.freesia.service.CommonIconTemplateHeaderProviderService;
import com.freesia.service.SysDeptService;
import com.freesia.service.SysRegisterService;
import com.freesia.service.SysRoleService;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

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
    private static final String DEFAULT_NICK_NAME_PREFIX = "Freesia用户";
    private static final int DEFAULT_AVATAR_COUNT = 6;

    private final TransactionTemplate transactionTemplate;
    private final CommonIconTemplateHeaderProviderService commonIconTemplateHeaderProviderService;
    private final SysUserConverter sysUserConverter;
    private final LoginPasswordProperties loginPasswordProperties;
    private final SysUserRepository sysUserRepository;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleService sysRoleService;
    private final SysDeptService sysDeptService;
    private final SysTenantRepository sysTenantRepository;
    private final SysTenantUserRepository sysTenantUserRepository;

    @Override
    @LogRecord(module = UserModule.USER_MANAGEMENT, subModule = UserModule.SubModule.REGISTER, message = "user.register")
    public SysUserPo register(SysUserDto sysUserDto) {
        return transactionTemplate.execute(status -> {
            ensureDefaultDept(sysUserDto);
            ensureUserNameAndNickName(sysUserDto);
            ensureDefaultAvatar(sysUserDto);
            validateUnique(sysUserDto);
            SysUserPo sysUserPo = sysUserConverter.convertDto2Po(sysUserDto);
            String rawPassword = UEmpty.isNotEmpty(sysUserDto.getPassword()) ? sysUserDto.getPassword() : loginPasswordProperties.getInitPassword();
            sysUserPo.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
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
                    ur.setSysUserRolePk(new SysUserRolePk(sysUserPo.getId(), defaultRole.getId()));
                    sysUserRoleRepository.saveAndFlush(ur);
                    log.info("注册用户[{}]成功赋予角色: {}", sysUserDto.getUserName(), defaultRole.getRoleKey());
//                    sysRoleService.saveInitRoleMenu(defaultRole.getId());
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

    @Override
    public void resetPassword(SysUserDto sysUserDto) {
        transactionTemplate.executeWithoutResult(status -> {
            if (UEmpty.isEmpty(sysUserDto.getEmail())) {
                throw new ServiceException(UserModule.SubModule.REGISTER, "email.invalid");
            }
            SysUserPo sysUserPo = sysUserRepository.findByEmailAndLogicDel(sysUserDto.getEmail(), false);
            if (ObjectUtil.isNull(sysUserPo)) {
                throw new ServiceException(UserModule.SubModule.REGISTER, "user.email.not.exists", new Object[]{sysUserDto.getEmail()});
            }
            String rawPassword = UEmpty.isNotEmpty(sysUserDto.getPassword()) ? sysUserDto.getPassword() : loginPasswordProperties.getInitPassword();
            sysUserPo.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            sysUserRepository.save(sysUserPo);
        });
    }

    private void ensureDefaultDept(SysUserDto sysUserDto) {
        if (UEmpty.isNotNull(sysUserDto.getDeptId())) {
            return;
        }
        SysDeptDto defaultDept = sysDeptService.findCacheDefaultDept();
        if (ObjectUtil.isNull(defaultDept)) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "default.dept.not.found");
        }
        sysUserDto.setDeptId(defaultDept.getId());
    }

    private void ensureUserNameAndNickName(SysUserDto sysUserDto) {
        if (UEmpty.isEmpty(sysUserDto.getUserName())) {
            if (UEmpty.isNotEmpty(sysUserDto.getEmail())) {
                sysUserDto.setUserName(sysUserDto.getEmail());
            } else {
                throw new ServiceException(UserModule.SubModule.REGISTER, "user.username.not.null");
            }
        }
        if (UEmpty.isEmpty(sysUserDto.getNickName())) {
            sysUserDto.setNickName(DEFAULT_NICK_NAME_PREFIX + RandomUtil.randomString(8));
        }
    }

    private void ensureDefaultAvatar(SysUserDto sysUserDto) {
        if (UEmpty.isNotEmpty(sysUserDto.getAvatar())) {
            return;
        }
        try {
            OssHandler ossHandler = OssFactory.getInstance();
            int avatarIndex = RandomUtil.randomInt(1, DEFAULT_AVATAR_COUNT + 1);
            sysUserDto.setAvatar(ossHandler.convertDomain2Endpoint(ossHandler.getUrl()) + "/avatar/avatar" + avatarIndex + ".png");
        } catch (Exception e) {
            log.warn("注册用户[{}]初始化默认头像失败: {}", sysUserDto.getUserName(), e.getMessage());
        }
    }

    private void validateUnique(SysUserDto sysUserDto) {
        if (ObjectUtil.isNotNull(sysUserRepository.findByUserNameAndLogicDel(sysUserDto.getUserName(), false))) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "user.register.not.unique", new Object[]{sysUserDto.getUserName()});
        }
        if (UEmpty.isNotEmpty(sysUserDto.getEmail())
                && ObjectUtil.isNotNull(sysUserRepository.findByEmailAndLogicDel(sysUserDto.getEmail(), false))) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "user.email.registered", new Object[]{sysUserDto.getEmail()});
        }
    }
}
