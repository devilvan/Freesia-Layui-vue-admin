package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.*;
import com.freesia.properties.MenuProperties;
import com.freesia.redis.util.URedis;
import com.freesia.satoken.bean.SysSensitiveLogBean;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SysRoleConverter;
import com.freesia.converter.SysUserConverter;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindDeptRolesByRoleIdEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.exception.RoleException;
import com.freesia.json.util.UJSON;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysRoleMapper;
import com.freesia.po.*;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.*;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysRoleService;
import com.freesia.util.*;
import com.freesia.vo.SysRoleVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 角色信息表 业务逻辑类
 * @date 2023-08-17
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRoleVo, SysRoleDto, SysRolePo> implements SysRoleService {
    private final TransactionTemplate transactionTemplate;
    private final SysRoleRepository sysRoleRepository;
    private final SysMenuRepository sysMenuRepository;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final SysRoleDeptRepository sysRoleDeptRepository;
    private final SysRoleConverter sysRoleConverter;
    private final SysUserConverter sysUserConverter;
    private final MenuProperties menuProperties;

    @Override
    protected MapStructConverter<SysRoleVo, SysRoleDto, SysRolePo> getMapStructConverter() {
        return sysRoleConverter;
    }

    @Override
    protected JpaRepository<SysRolePo, Long> getRepository() {
        return sysRoleRepository;
    }

    @Override
    protected Class<SysRoleDto> getDtoClass() {
        return SysRoleDto.class;
    }

    @Override
    protected Class<SysRolePo> getPoClass() {
        return SysRolePo.class;
    }

    @Override
    protected Wrapper<SysRolePo> buildQueryWrapper(@NonNull SysRoleDto dto) {
        return Wrappers.<SysRolePo>query()
                .eq("R.LOGIC_DEL", FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(dto.getStatus()), "R.STATUS", FlagConstant.ENABLED)
                .like(ObjectUtil.isNotNull(dto.getRoleName()), "R.ROLE_NAME", dto.getRoleName())
                .like(ObjectUtil.isNotNull(dto.getRoleKey()), "R.ROLE_KEY", dto.getRoleKey())
                .between(ObjectUtil.isNotNull(dto.getCreateTimeFrom()) && ObjectUtil.isNotNull(dto.getCreateTimeTo()),
                        "R.CREATE_TIME", dto.getCreateTimeFrom(), dto.getCreateTimeTo())
                .orderByAsc("R.ORDER_NUM");
    }

    @Override
    public Set<String> findRolePermissionStrByUserId(Long id) {
        Set<SysRolePo> sysRolePoSet = sysRoleMapper.findRolePermissionStrByUserId(id);
        Set<String> sysRoleStrSet = UCollection.optimizeInitialCapacitySet(sysRolePoSet.size());
        for (SysRolePo sysRolePo : sysRolePoSet) {
            sysRoleStrSet.addAll(UString.splitList(sysRolePo.getRoleKey().trim()));
        }
        return sysRoleStrSet;
    }

    @Override
    public TableResult<FindPageSysRoleListEntity> findPageSysRoleList(SysRoleDto sysRoleDto, PageQuery pageQuery) {
        Wrapper<SysRolePo> wrapper = buildQueryWrapper(sysRoleDto);
        Page<FindPageSysRoleListEntity> page = sysRoleMapper.findPageSysRoleList(pageQuery.build(), wrapper);
        return TableResult.build(page);
    }

    @Override
    public void saveRoleMenuPrivilege(List<Long> menuIdList, Long roleId, String dataScope) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseGet(SysRolePo::new);
        if (AdminConstant.ADMIN.equals(sysRolePo.getRoleKey())) {
            return;
        }
        Set<SysMenuPo> oldSysMenuPoSet = sysRolePo.getSysMenuPoSet();
        List<Long> oldMenuIdList = UStream.toList(oldSysMenuPoSet, SysMenuPo::getId);
        List<SysMenuPo> sysMenuPoList = sysMenuRepository.findAllById(menuIdList);
        sysRolePo.setDataScope(dataScope);
        sysRolePo.setSysMenuPoSet(new HashSet<>(sysMenuPoList));
        SysRolePo saveSysRolePo = sysRoleRepository.save(sysRolePo);
        // 记录操作日志
        Set<SysMenuPo> newSysMenuPoSet = saveSysRolePo.getSysMenuPoSet();
        List<Long> newMenuIdList = UStream.toList(newSysMenuPoSet, SysMenuPo::getId);
        SysSensitiveLogBean saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
            sysSensitiveLogBean.setModule(RoleModule.ROLE_MANAGEMENT);
            sysSensitiveLogBean.setSubModule(MenuModule.SubModule.ASSIGN_MENU_PERMISSIONS);
            sysSensitiveLogBean.setType(MenuModule.SubModule.ASSIGN_MENU_PERMISSIONS);
            sysSensitiveLogBean.setResult(FlagConstant.SUCCESS);
            sysSensitiveLogBean.setContextOld(UJSON.toJSONString(oldMenuIdList));
            sysSensitiveLogBean.setContext(UJSON.toJSONString(newMenuIdList));
            sysSensitiveLogBean.setRemark(UMessage.message("assigned_menu_permissions_success"));
            return sysSensitiveLogBean;
        });
        USpring.context().publishEvent(saveSysSensitiveLogBean);
    }

    @Override
    public List<FindAllRolesEntity> findAllRoles() {
        LambdaQueryWrapper<SysRolePo> queryWrapper = new LambdaQueryWrapper<SysRolePo>()
                .select(
                        SysRolePo::getId, SysRolePo::getRoleKey, SysRolePo::getRoleName,
                        SysRolePo::getDataScope, SysRolePo::getStatus, SysRolePo::getRemark
                )
                .eq(SysRolePo::getLogicDel, FlagConstant.DISABLED);
        List<SysRolePo> sysRolePoList = this.list(queryWrapper);
        return sysRoleConverter.convertBatchPo2FindAllRolesEntity(sysRolePoList);
    }

    @Override
    public SysRoleDto findOne(SysRoleDto sysRoleDto) {
        Wrapper<SysRolePo> queryWrapper = new LambdaQueryWrapper<SysRolePo>()
                .select(
                        SysRolePo::getId, SysRolePo::getRoleName,
                        SysRolePo::getRoleKey, SysRolePo::getStatus,
                        SysRolePo::getDataScope, SysRolePo::getRemark
                )
                .eq(SysRolePo::getId, sysRoleDto.getId())
                .eq(SysRolePo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysRolePo::getStatus, FlagConstant.ENABLED);
        return findOne(sysRoleDto, queryWrapper);
    }

    @Override
    public TableResult<SysUserDto> findPageUserByRoleId(SysRoleDto sysRoleDto, PageQuery pageQuery) {
        Wrapper<SysRolePo> queryWrapper = Wrappers.<SysRolePo>query()
                .eq("R.ID", sysRoleDto.getId())
                .eq("R.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("R.STATUS", FlagConstant.ENABLED)
                .eq("U.LOGIC_DEL", FlagConstant.DISABLED);
        Page<SysUserPo> sysUserPoPage = sysRoleMapper.findPageUserByRoleId(queryWrapper, pageQuery.build());
        return TableResult.build(sysUserConverter.convertPagePo2Dto(sysUserPoPage));
    }

    @Override
    public TableResult<SysUserDto> findPageAllowAssignUserByRoleId(SysRoleDto sysRoleDto, PageQuery pageQuery) {
        SysRolePo sysRolePo = sysRoleConverter.convertDto2Po(sysRoleDto);
        Page<SysUserPo> userPoPage = sysRoleMapper.findPageAllowAssignUserByRoleId(sysRolePo, pageQuery.build());
        return TableResult.build(sysUserConverter.convertPagePo2Dto(userPoPage));
    }

    @Override
    @LogRecord(module = RoleModule.ROLE_MANAGEMENT, subModule = RoleModule.SubModule.ASSIGN_USER, message = "role.assignUser")
    public void assignUser(Long roleId, List<Long> userIdList) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseGet(SysRolePo::new);
        Set<SysUserRolePo> sysUserRolePoSet = sysRolePo.getSysUserRolePoSet();
        for (Long userId : userIdList) {
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            SysUserRolePk sysUserRolePk = new SysUserRolePk();
            sysUserRolePk.setRoleId(roleId);
            sysUserRolePk.setUserId(userId);
            sysUserRolePo.setSysUserRolePk(sysUserRolePk);
            sysUserRolePoSet.add(sysUserRolePo);
        }
        sysRolePo.setSysUserRolePoSet(sysUserRolePoSet);
        sysRoleRepository.save(sysRolePo);
    }

    @Override
    @LogRecord(module = RoleModule.ROLE_MANAGEMENT, subModule = RoleModule.SubModule.CANCEL_ASSIGN_USER, message = "role.cancel.assignUser")
    public void cancelAssignUser(Long roleId, List<Long> userIdList) {
        sysRoleRepository.cancelAssignUser(roleId, userIdList);
    }

    @Override
    public void assignDept(Long roleId, Set<Long> deptIdSet) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseThrow(() -> new RoleException("role.not.exists", new Object[]{}));
        // 获取并修改分配后的角色
        Set<SysDeptPo> sysDeptPoSet = sysRolePo.getSysDeptPoSet();
        List<Long> beforeDeptIdList = sysDeptPoSet.stream().map(SysDeptPo::getId).collect(Collectors.toList());
        Set<SysRoleDeptPo> beforeSysRoleDeptPoSet = UCollection.optimizeInitialCapacitySet(beforeDeptIdList.size());
        for (Long beforeDeptId : beforeDeptIdList) {
            SysRoleDeptPo sysRoleDeptPo = new SysRoleDeptPo();
            sysRoleDeptPo.setSysRoleDeptPk(new SysRoleDeptPk(beforeDeptId, roleId));
            beforeSysRoleDeptPoSet.add(sysRoleDeptPo);
        }
        Set<SysRoleDeptPo> afterSysRoleDeptPoSet = UCollection.optimizeInitialCapacitySet(deptIdSet.size());
        for (Long deptId : deptIdSet) {
            SysRoleDeptPo sysRoleDeptPo = new SysRoleDeptPo();
            sysRoleDeptPo.setSysRoleDeptPk(new SysRoleDeptPk(deptId, roleId));
            afterSysRoleDeptPoSet.add(sysRoleDeptPo);
        }
        transactionTemplate.execute(status -> {
            SysSensitiveLogBean saveSysSensitiveLogBean = null;
            try {
                if (UEmpty.isNotEmpty(beforeSysRoleDeptPoSet)) {
                    sysRoleDeptRepository.deleteAllInBatch(beforeSysRoleDeptPoSet);
                }
                if (UEmpty.isNotEmpty(afterSysRoleDeptPoSet)) {
                    sysRoleDeptRepository.saveAll(afterSysRoleDeptPoSet);
                }
                saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
                    sysSensitiveLogBean.setModule(RoleModule.ROLE_MANAGEMENT);
                    sysSensitiveLogBean.setSubModule(RoleModule.SubModule.ASSIGN_DEPT);
                    sysSensitiveLogBean.setType(RoleModule.SubModule.ASSIGN_DEPT);
                    sysSensitiveLogBean.setResult(FlagConstant.SUCCESS);
                    sysSensitiveLogBean.setContextOld("分配前部门ID：" + UJSON.toJSONString(beforeDeptIdList));
                    sysSensitiveLogBean.setContext("分配后部门ID：" + UJSON.toJSONString(deptIdSet));
                    sysSensitiveLogBean.setRemark(UMessage.message("assign_dept_permissions_success"));
                    return sysSensitiveLogBean;
                });
            } catch (Exception e) {
                saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
                    sysSensitiveLogBean.setModule(RoleModule.ROLE_MANAGEMENT);
                    sysSensitiveLogBean.setSubModule(RoleModule.SubModule.ASSIGN_DEPT);
                    sysSensitiveLogBean.setType(RoleModule.SubModule.ASSIGN_DEPT);
                    sysSensitiveLogBean.setResult(FlagConstant.FAILED);
                    sysSensitiveLogBean.setRemark(UMessage.message("assign_dept_permissions_failed"));
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
    public FindDeptRolesByRoleIdEntity findDeptRolesByRoleId(Long roleId) {
        // 获取角色对象
        SysRolePo sysRolePo = findSysRolePoById(roleId);
        // 获取部门
        Set<SysDeptPo> sysDeptPoSet = sysRolePo.getSysDeptPoSet();
        return buildFindDeptRolesByRoleIdEntity(sysRolePo, sysDeptPoSet);
    }

    @Override
    @LogRecord(module = RoleModule.ROLE_MANAGEMENT, subModule = RoleModule.SubModule.SAVE_ROLE, message = "role.save")
    public SysRoleDto saveUpdate(SysRoleDto sysRoleDto) {
        Long roleId = sysRoleDto.getId();
        SysRolePo sysRolePo;
        if (UEmpty.isNull(roleId)) {
            // 新增
            sysRolePo = sysRoleConverter.convertDto2Po(sysRoleDto);
        } else {
            // 修改
            sysRolePo = findSysRolePoById(roleId);
            sysRoleConverter.updateSysRoleDto2Po(sysRoleDto, sysRolePo);
        }
        SysRolePo save = sysRoleRepository.save(sysRolePo);
        return sysRoleConverter.convertPo2Dto(save);
    }

    @Override
    @LogRecord(module = RoleModule.ROLE_MANAGEMENT, subModule = RoleModule.SubModule.DELETE_ROLE, message = "role.delete")
    public void deleteRole(SysRoleDto sysRoleDto) {
        Long roleId = sysRoleDto.getId();
        SysRolePo sysRolePo = findSysRolePoById(roleId);
        // 用户-角色关联
        Set<SysUserRolePo> sysUserRolePoSet = sysRolePo.getSysUserRolePoSet();
        // 角色-部门关联
        Set<SysRoleDeptPo> sysRoleDeptPoSet = sysRolePo.getSysRoleDeptPoSet();
        // 角色-菜单关联
        Set<SysRoleMenuPo> sysRoleMenuPoSet = sysRolePo.getSysRoleMenuPoSet();
        transactionTemplate.execute(status -> {
            sysUserRoleRepository.deleteAllInBatch(sysUserRolePoSet);
            sysRoleMenuRepository.deleteAllInBatch(sysRoleMenuPoSet);
            sysRoleDeptRepository.deleteAllInBatch(sysRoleDeptPoSet);
            sysRoleRepository.delete(sysRolePo);
            return null;
        });
    }

    @Override
    public void buildInitDefaultSysRole() {
        SysRolePo sysRolePo = sysRoleRepository.findCacheDefaultRole(AdminConstant.RoleKey.COMMON.getCode());
        if (sysRolePo == null) {
            transactionTemplate.execute(status -> {
                SysRolePo newSysRolePo = buildDefaultSysRolePo();
                SysRolePo saveSysRolePo = sysRoleRepository.save(newSysRolePo);
                SysRoleDto sysRoleDto = UCopy.copyPo2Dto(saveSysRolePo, SysRoleDto.class);
                URedis.set(CacheConstant.DEFAULT_ROLE, sysRoleDto);
                return null;
            });
        } else {
            SysRoleDto sysRoleDto = UCopy.copyPo2Dto(sysRolePo, SysRoleDto.class);
            URedis.set(CacheConstant.DEFAULT_ROLE, sysRoleDto);
        }
    }

    private SysRolePo buildDefaultSysRolePo() {
        SysRolePo newSysRolePo = new SysRolePo();
        newSysRolePo.setRoleName("Freesia普通角色");
        newSysRolePo.setRoleKey(AdminConstant.RoleKey.COMMON.getCode());
        newSysRolePo.setStatus(FlagConstant.ENABLED);
        newSysRolePo.setOrderNum(1);
        newSysRolePo.setDataScope(DataScope.OWN.getCode());
        newSysRolePo.setMenuCheckStrictly(true);
        newSysRolePo.setDeptCheckStrictly(true);
        newSysRolePo.setRemark("Freesia普通角色");
        newSysRolePo.setBuildIn(true);
        return newSysRolePo;
    }

    @Override
    public SysRoleDto findCacheDefaultRole() {
        SysRoleDto sysRoleDto = URedis.get(CacheConstant.DEFAULT_ROLE);
        if (UEmpty.isNotNull(sysRoleDto)) {
            return sysRoleDto;
        }
        return UCopy.copyPo2Dto(sysRoleRepository.findCacheDefaultRole(AdminConstant.RoleKey.COMMON.getCode()), SysRoleDto.class);
    }


    @Override
    public void saveInitRoleMenu(Long roleId) {
        List<SysRoleMenuPo> sysRoleMenuList = new ArrayList<>();
        // 分配菜单权限
        if (UEmpty.isNotEmpty(menuProperties.getPath())) {
            List<SysMenuPo> sysMenuPoList = sysMenuRepository.findByPathIn(menuProperties.getPath());
            List<Long> sysMenuIdList = sysMenuPoList.stream().map(BasePo::getId).toList();
            List<SysRoleMenuPo> list = sysMenuIdList.stream().map(menuId -> new SysRoleMenuPo(new SysRoleMenuPk(menuId, roleId))).toList();
            if (UEmpty.isNotEmpty(list)) {
                sysRoleMenuList.addAll(list);
            }
        }
        if (UEmpty.isNotEmpty(menuProperties.getPermission())) {
            List<SysMenuPo> sysMenuPoList = sysMenuRepository.findByPermsIn(menuProperties.getPermission());
            List<Long> sysMenuIdList = sysMenuPoList.stream().map(BasePo::getId).toList();
            List<SysRoleMenuPo> list = sysMenuIdList.stream().map(menuId -> new SysRoleMenuPo(new SysRoleMenuPk(menuId, roleId))).toList();
            if (UEmpty.isNotEmpty(list)) {
                sysRoleMenuList.addAll(list);
            }
        }
        if (UEmpty.isNotEmpty(sysRoleMenuList)) {
            sysRoleMenuRepository.saveAll(sysRoleMenuList);
        }
    }

    private FindDeptRolesByRoleIdEntity buildFindDeptRolesByRoleIdEntity(SysRolePo sysRolePo, Set<SysDeptPo> sysDeptPoSet) {
        FindDeptRolesByRoleIdEntity findDeptRolesByRoleIdEntity = new FindDeptRolesByRoleIdEntity();
        findDeptRolesByRoleIdEntity.setRoleId(sysRolePo.getId());
        findDeptRolesByRoleIdEntity.setRoleName(sysRolePo.getRoleName());
        Set<Long> sysDeptIdList = sysDeptPoSet.stream().map(SysDeptPo::getId).collect(Collectors.toSet());
        findDeptRolesByRoleIdEntity.setSelectedDept(sysDeptIdList);
        return findDeptRolesByRoleIdEntity;
    }

    private SysRolePo findSysRolePoById(Long roleId) {
        return sysRoleRepository.findById(roleId).orElseThrow(() -> new RoleException("role.query.failed", new Object[]{roleId}));
    }

}
