package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.MenuModule;
import com.freesia.constant.SysTenantType;
import com.freesia.constant.UserModule;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SysTenantConverter;
import com.freesia.converter.SysUserConverter;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindPageSysUserByDeptEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.entity.FindUserRolesByUserIdEntity;
import com.freesia.exception.UserException;
import com.freesia.helper.DataBaseHelper;
import com.freesia.json.util.UJSON;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysDeptMapper;
import com.freesia.mapper.SysUserMapper;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.po.*;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.properties.LoginPasswordProperties;
import com.freesia.repository.SysTenantUserRepository;
import com.freesia.repository.SysUserRepository;
import com.freesia.repository.SysUserRoleRepository;
import com.freesia.satoken.bean.SysSensitiveLogBean;
import com.freesia.satoken.constant.UserType;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.CommonIconTemplateHeaderProviderService;
import com.freesia.service.SysRoleService;
import com.freesia.service.SysTenantService;
import com.freesia.service.SysUserService;
import com.freesia.util.*;
import com.freesia.vo.SysUserVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 用户信息表 业务逻辑类
 * @date 2023-08-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUserVo, SysUserDto, SysUserPo> implements SysUserService {
    private final TransactionTemplate transactionTemplate;
    private final LoginPasswordProperties loginPasswordProperties;
    private final SysUserRepository sysUserRepository;
    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysTenantService sysTenantService;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleService sysRoleService;
    private final SysUserConverter sysUserConverter;
    private final SysTenantConverter sysTenantConverter;
    private final SysTenantUserRepository sysTenantUserRepository;
    private final CommonIconTemplateHeaderProviderService commonIconTemplateHeaderProviderService;

    @Override
    protected MapStructConverter<SysUserVo, SysUserDto, SysUserPo> getMapStructConverter() {
        return sysUserConverter;
    }

    @Override
    protected JpaRepository<SysUserPo, Long> getRepository() {
        return sysUserRepository;
    }

    @Override
    protected Class<SysUserDto> getDtoClass() {
        return SysUserDto.class;
    }

    @Override
    protected Class<SysUserPo> getPoClass() {
        return SysUserPo.class;
    }

    @Override
    protected Wrapper<SysUserPo> buildQueryWrapper(@NonNull SysUserDto sysUserDto) {
        return USql.buildQueryWrapper(() -> Wrappers.<SysUserPo>query()
                .eq("U.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("U.ACCOUNT_STATUS", FlagConstant.ENABLED)
                .eq("STU.TENANT_ID", USecurity.getTenantId())
                .like(ObjectUtil.isNotNull(sysUserDto.getNickName()), "U.NICK_NAME", sysUserDto.getNickName())
                .likeRight(ObjectUtil.isNotNull(sysUserDto.getUserName()), "U.USER_NAME", sysUserDto.getUserName())
                .likeRight(ObjectUtil.isNotNull(sysUserDto.getEmail()), "U.EMAIL", sysUserDto.getEmail())
                .likeRight(ObjectUtil.isNotNull(sysUserDto.getTelNo()), "U.TEL_NO", sysUserDto.getTelNo())
                .between(ObjectUtil.isNotNull(sysUserDto.getCreateTimeFrom()) && ObjectUtil.isNotNull(sysUserDto.getCreateTimeTo()),
                        "U.CREATE_TIME", sysUserDto.getCreateTimeFrom(), sysUserDto.getCreateTimeTo())
                .and(ObjectUtil.isNotNull(sysUserDto.getDeptId()), m -> {
                    List<SysDeptPo> sysDeptPoList = sysDeptMapper.selectList(new LambdaQueryWrapper<SysDeptPo>()
                            .select(SysDeptPo::getId)
                            .apply(DataBaseHelper.findInSet(sysUserDto.getDeptId(), "ancestors"))
                    );
                    List<Long> deptIdList = UStream.toList(sysDeptPoList, SysDeptPo::getId);
                    deptIdList.add(sysUserDto.getDeptId());
                    m.in("U.DEPT_ID", deptIdList);
                }));
    }

    @Override
    public SysUserPo findOneByUsername(String username) {
        LambdaQueryWrapper<SysUserPo> queryWrapper = new LambdaQueryWrapper<SysUserPo>()
                .select(SysUserPo::getUserName, SysUserPo::getAccountStatus, SysUserPo::getLogicDel)
                .eq(SysUserPo::getUserName, username);
        return this.getOne(queryWrapper);
    }

    @Override
    public SysUserPo findByUsername(String username) {
        return sysUserRepository.findByUserNameAndLogicDel(username, false);
    }

    @Override
    public SysUserDto findUserById(Long userId) {
        SysUserPo sysUserPo = sysUserRepository.findById(userId).orElseGet(SysUserPo::new);
        return sysUserConverter.convertPo2Dto(sysUserPo);
    }

    @Override
    public List<SysUserDto> findUserByIdList(List<Long> userIdList) {
        List<SysUserPo> sysUserPoList = Optional.of(sysUserRepository.findAllById(userIdList)).orElseGet(ArrayList::new);
        return sysUserConverter.convertBatchPo2Dto(sysUserPoList);
    }

    @Override
    public boolean checkUserNameUnique(SysUserDto sysUserDto) {
        SysUserPo sysUserPo = sysUserConverter.convertDto2Po(sysUserDto);
        return sysUserMapper.checkUserNameUnique(sysUserPo);
    }

    @Override
    public TableResult<FindPageSysUserListEntity> findPageSysUserList(SysUserDto sysUserDto, PageQuery pageQuery) {
        // 构建SQL 通过部门权限限制查询当前用户下能够查找的用户的列表
        Wrapper<SysUserPo> sysUserPoWrapper = buildQueryWrapper(sysUserDto);
        Page<FindPageSysUserListEntity> page = sysUserMapper.findPageSysUserList(pageQuery.build(), sysUserPoWrapper);
        return TableResult.build(page);
    }

    @Override
    public TableResult<FindPageSysUserByDeptEntity> findPageSysUserByDept(SysUserDto sysUserDto, PageQuery pageQuery) {
        Page<FindPageSysUserByDeptEntity> page = sysUserMapper.findPageSysUserByDept(pageQuery.build(), Wrappers.<SysUserPo>query()
                .eq("U.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("U.ACCOUNT_STATUS", FlagConstant.ENABLED)
                .eq("D.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("D.DEPT_STATUS", FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(sysUserDto.getTenantId()), "STU.TENANT_ID", sysUserDto.getTenantId())
                .and(ObjectUtil.isNotNull(sysUserDto.getDeptId()), m -> {
                    List<SysDeptPo> sysDeptPoList = sysDeptMapper.selectList(new LambdaQueryWrapper<SysDeptPo>()
                            .select(SysDeptPo::getId)
                            .apply(DataBaseHelper.findInSet(sysUserDto.getDeptId(), "ancestors"))
                    );
                    List<Long> deptIdList = UStream.toList(sysDeptPoList, SysDeptPo::getId);
                    deptIdList.add(sysUserDto.getDeptId());
                    m.in("D.ID", deptIdList);
                }));
        return TableResult.build(page);
    }

    @Override
    public SysUserDto findCurrentUserProfile(Long userId) {
        OssHandler ossHandler = OssFactory.getInstance();
        LambdaQueryWrapper<SysUserPo> queryWrapper = new LambdaQueryWrapper<SysUserPo>()
                .eq(SysUserPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysUserPo::getId, userId);
        SysUserPo sysUserPo = sysUserMapper.findCurrentUserProfile(queryWrapper);
        sysUserPo.setAvatar(ossHandler.convertEndpoint2Domain(sysUserPo.getAvatar()));
        return sysUserConverter.convertPo2Dto(sysUserPo);
    }

    @Override
    public void saveUserInfo(SysUserDto sysUserDto) {
        Long id = sysUserDto.getId();
        SysUserPo sysUserPo;
        if (UEmpty.isNotNull(id)) {
            sysUserPo = sysUserRepository.findById(id).orElseThrow(() -> new UserException("user.query.failed", new Object[]{}));
            sysUserConverter.updateSysUserDto2Po(sysUserDto, sysUserPo);
            sysUserRepository.save(sysUserPo);
        } else {
            sysUserPo = buildInsertSysUserPo(sysUserDto);
            sysUserPo = sysUserRepository.save(sysUserPo);
            Long tenantId = USecurity.getTenantId();
            sysTenantService.assignTenant2User(tenantId, Collections.singletonList(sysUserPo.getId()));
        }
    }

    @Override
    public FindUserRolesByUserIdEntity findUserRolesByUserId(Long userId) {
        // 获取用户对象
        SysUserPo sysUserPo = sysUserRepository.findById(userId).orElseThrow(() -> new UserException("user.query.failed", new Object[]{userId}));
        // 获取角色
        Set<SysRolePo> sysRolePoSet = sysUserPo.getSysRolePoSet();
        return buildFindUserRolesByUserIdEntity(sysUserPo, sysRolePoSet);
    }

    @Override
    public void assignRole(Long userId, Set<Long> afterRoleIdSet) {
        SysUserPo sysUserPo = sysUserRepository.findById(userId).orElseThrow(() -> new UserException("user.not.exists", new Object[]{}));
        // 获取并修改分配后的角色
        Set<SysRolePo> sysRolePoSet = sysUserPo.getSysRolePoSet();
        List<Long> beforeRoleIdList = sysRolePoSet.stream().map(SysRolePo::getId).collect(Collectors.toList());
        Set<SysUserRolePo> beforeSysUserRolePoSet = UCollection.optimizeInitialCapacitySet(afterRoleIdSet.size());
        for (Long beforeRoleId : beforeRoleIdList) {
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            sysUserRolePo.setSysRoleMenuPk(new SysUserRolePk(userId, beforeRoleId));
            beforeSysUserRolePoSet.add(sysUserRolePo);
        }
        Set<SysUserRolePo> afterSysUserRolePoSet = UCollection.optimizeInitialCapacitySet(afterRoleIdSet.size());
        for (Long roleId : afterRoleIdSet) {
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            sysUserRolePo.setSysRoleMenuPk(new SysUserRolePk(userId, roleId));
            afterSysUserRolePoSet.add(sysUserRolePo);
        }
        transactionTemplate.execute(status -> {
            SysSensitiveLogBean saveSysSensitiveLogBean = null;
            try {
                if (UEmpty.isNotEmpty(beforeSysUserRolePoSet)) {
                    sysUserRoleRepository.deleteAllInBatch(beforeSysUserRolePoSet);
                }
                if (UEmpty.isNotEmpty(afterSysUserRolePoSet)) {
                    sysUserRoleRepository.saveAll(afterSysUserRolePoSet);
                }
                saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
                    sysSensitiveLogBean.setModule(UserModule.USER_MANAGEMENT);
                    sysSensitiveLogBean.setSubModule(MenuModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setType(MenuModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setResult(FlagConstant.SUCCESS);
                    sysSensitiveLogBean.setContextOld("分配前角色ID：" + UJSON.toJSONString(beforeRoleIdList));
                    sysSensitiveLogBean.setContext("分配后角色ID：" + UJSON.toJSONString(afterRoleIdSet));
                    sysSensitiveLogBean.setRemark(UMessage.message("assign_role_permissions_success"));
                    return sysSensitiveLogBean;
                });
            } catch (Exception e) {
                saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
                    sysSensitiveLogBean.setModule(UserModule.USER_MANAGEMENT);
                    sysSensitiveLogBean.setSubModule(MenuModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setType(MenuModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setResult(FlagConstant.FAILED);
                    sysSensitiveLogBean.setRemark(UMessage.message("assign_role_permissions_failed"));
                    return sysSensitiveLogBean;
                });
                throw e;
            } finally {
                if (UEmpty.isNotNull(saveSysSensitiveLogBean)) {
                    USpring.context().publishEvent(saveSysSensitiveLogBean);
                }
            }
            return status;
        });
    }

    @Override
    public TableResult<SysUserDto> findPageUserByTenantId(Long tenantId, PageQuery pageQuery) {
        Page<SysUserPo> sysUserPoPage = sysUserMapper.findPageUserByTenantId(tenantId, pageQuery.build());
        return TableResult.build(sysUserConverter.convertPagePo2Dto(sysUserPoPage));
    }

    @Override
    public TableResult<SysUserDto> findPageAllowAssignUserByTenantId(SysTenantDto sysTenantDto, PageQuery pageQuery) {
        SysTenantPo sysTenantPo = sysTenantConverter.convertDto2Po(sysTenantDto);
        Page<SysUserPo> sysUserPoPage = sysUserMapper.findPageAllowAssignUserByTenantId(sysTenantPo, pageQuery.build());
        return TableResult.build(sysUserConverter.convertPagePo2Dto(sysUserPoPage));
    }

    @Override
    public Boolean isAdmin(Long id) {
        return sysUserMapper.isAdmin(id);
    }

    @Override
    public List<SysUserDto> findDistinctUserNameList(List<String> distinctUserNameList) {
        Wrapper<SysUserPo> queryWrapper = new LambdaQueryWrapper<SysUserPo>()
                .eq(SysUserPo::getLogicDel, FlagConstant.DISABLED)
                .in(SysUserPo::getUserName, distinctUserNameList);
        List<SysUserPo> sysUserPoList = this.list(queryWrapper);
        return sysUserConverter.convertBatchPo2Dto(sysUserPoList);
    }

    @Override
    @LogRecord(module = UserModule.USER_MANAGEMENT, subModule = UserModule.SubModule.DELETE_USER, message = "user.delete")
    public List<SysUserDto> deleteUser(List<Long> idList) {
        List<SysUserPo> sysUserPoList = sysUserRepository.findAllById(idList);
        sysUserPoList = sysUserPoList.stream().peek(sysUserPo -> sysUserPo.setLogicDel(true)).collect(Collectors.toList());
        sysUserRepository.saveAll(sysUserPoList);
        return sysUserConverter.convertBatchPo2Dto(sysUserPoList);
    }

    @Override
    @LogRecord(module = UserModule.USER_MANAGEMENT, subModule = UserModule.SubModule.ASSIGN_DEPT, message = "user.assignDept")
    public Map<String, Object> assignDept(List<Long> userIdList, Long deptId) {
        List<SysUserPo> sysUserPoList = sysUserRepository.findAllById(userIdList);
        for (SysUserPo sysUserPo : sysUserPoList) {
            sysUserPo.setDeptId(deptId);
        }
        sysUserRepository.saveAll(sysUserPoList);
        return Map.of("deptId", deptId, "userIdList", userIdList);
    }

    @Override
    @LogRecord(module = UserModule.USER_MANAGEMENT, subModule = UserModule.SubModule.AVATAR_UPDATE, message = "user.avatarUpdate")
    public void avatarUpdate(String avatar) {
        OssHandler ossHandler = OssFactory.getInstance();
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseThrow(() -> new UserException("user.info.null", new Object[]{}));
        Long userId = loginUser.getUserId();
        SysUserPo sysUserPo = sysUserRepository.findById(userId).orElseGet(SysUserPo::new);
        sysUserPo.setAvatar(ossHandler.convertDomain2Endpoint(avatar));
        sysUserRepository.save(sysUserPo);
    }

    @Override
    public TableResult<FindPageSysUserListEntity> findPageSysUserWithoutDataScope(SysUserDto sysUserDto, PageQuery pageQuery) {
        // 构建SQL 通过部门权限限制查询当前用户下能够查找的用户的列表
        Wrapper<SysUserPo> sysUserPoWrapper = buildQueryWrapper(sysUserDto);
        Page<FindPageSysUserListEntity> page = sysUserMapper.findPageSysUserWithoutDataScope(pageQuery.build(), sysUserPoWrapper);
        return TableResult.build(page);
    }

    @Override
    public List<FindPageSysUserListEntity> findListSysUserById(List<Long> idList) {
        Wrapper<SysUserPo> wrapper = new LambdaQueryWrapper<SysUserPo>()
                .eq(SysUserPo::getLogicDel, false)
                .in(SysUserPo::getId, idList);
        List<SysUserPo> sysUserPoList = sysUserMapper.selectList(wrapper);
        if (UEmpty.isNotEmpty(sysUserPoList)) {
            return UCopy.fullCopyList(sysUserPoList, FindPageSysUserListEntity.class);
        }
        return null;
    }

    /**
     * 构建新增用户实体
     *
     * @param sysUserDto 前端采集的用户信息
     * @return 构建后的新增用户实体
     */
    private SysUserPo buildInsertSysUserPo(SysUserDto sysUserDto) {
        SysUserPo sysUserPo = sysUserConverter.convertDto2Po(sysUserDto);
        sysUserPo.setPassword(BCrypt.hashpw(loginPasswordProperties.getInitPassword(), BCrypt.gensalt()));
        return sysUserPo;
    }

    /**
     * 构建 {@link FindUserRolesByUserIdEntity} 对象
     *
     * @param sysUserPo    用户信息
     * @param sysRolePoSet 角色信息
     * @return 构建后的对象
     */
    private FindUserRolesByUserIdEntity buildFindUserRolesByUserIdEntity(SysUserPo sysUserPo, Set<SysRolePo> sysRolePoSet) {
        FindUserRolesByUserIdEntity findUserRolesByUserIdEntity = new FindUserRolesByUserIdEntity();
        findUserRolesByUserIdEntity.setUserId(sysUserPo.getId());
        findUserRolesByUserIdEntity.setUserName(sysUserPo.getUserName());
        Set<Long> sysRoleIdList = sysRolePoSet.stream().map(SysRolePo::getId).collect(Collectors.toSet());
        findUserRolesByUserIdEntity.setSelectedRoles(sysRoleIdList);
        return findUserRolesByUserIdEntity;
    }
}
