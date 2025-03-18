package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.log.annotation.LogRecord;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.*;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindDeptRolesByRoleIdEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.exception.RoleException;
import com.freesia.mapper.SysRoleMapper;
import com.freesia.po.*;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.*;
import com.freesia.constant.AdminConstant;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysRoleService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRolePo> implements SysRoleService {
    private final TransactionTemplate transactionTemplate;
    private final SysRoleRepository sysRoleRepository;
    private final SysMenuRepository sysMenuRepository;
    private final SysUserRepository sysUserRepository;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final SysRoleDeptRepository sysRoleDeptRepository;

    @Override
    public SysRolePo saveUpdate(SysRoleDto sysRoleDto) {
        SysRolePo sysRolePo = new SysRolePo();
        UCopy.fullCopy(sysRoleDto, sysRolePo);
        return sysRoleRepository.saveAndFlush(sysRolePo);
    }

    @Override
    public List<SysRolePo> saveUpdateBatch(List<SysRoleDto> list) {
        List<SysRolePo> sysRolePoList = UCopy.fullCopyList(list, SysRolePo.class);
        return sysRoleRepository.saveAllAndFlush(sysRolePoList);
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
        Wrapper<SysRolePo> wrapper = USql.buildQueryWrapper(() -> {
            SysRolePo sysRolePo = new SysRolePo();
            UCopy.fullCopy(sysRoleDto, sysRolePo);
            return Wrappers.<SysRolePo>query()
                    .eq("R.LOGIC_DEL", FlagConstant.DISABLED)
                    .eq(UEmpty.isNotEmpty(sysRolePo.getStatus()), "R.STATUS", FlagConstant.ENABLED)
                    .like(ObjectUtil.isNotNull(sysRolePo.getRoleName()), "R.ROLE_NAME", sysRolePo.getRoleName())
                    .like(ObjectUtil.isNotNull(sysRolePo.getRoleKey()), "R.ROLE_KEY", sysRolePo.getRoleKey())
                    .between(ObjectUtil.isNotNull(sysRoleDto.getCreateTimeFrom()) && ObjectUtil.isNotNull(sysRoleDto.getCreateTimeTo()),
                            "R.CREATE_TIME", sysRoleDto.getCreateTimeFrom(), sysRoleDto.getCreateTimeTo())
                    .orderByAsc("R.ORDER_NUM");
        });
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
            sysSensitiveLogBean.setContextOld(JSONObject.toJSONString(oldMenuIdList));
            sysSensitiveLogBean.setContext(JSONObject.toJSONString(newMenuIdList));
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
        return UCopy.fullCopyList(sysRolePoList, FindAllRolesEntity.class);
    }

    @Override
    public SysRoleDto findRoleById(Long roleId) {
        Wrapper<SysRolePo> queryWrapper = new LambdaQueryWrapper<SysRolePo>()
                .select(
                        SysRolePo::getId, SysRolePo::getRoleName,
                        SysRolePo::getRoleKey, SysRolePo::getStatus,
                        SysRolePo::getDataScope, SysRolePo::getRemark
                )
                .eq(SysRolePo::getId, roleId)
                .eq(SysRolePo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysRolePo::getStatus, FlagConstant.ENABLED);
        SysRolePo sysRolePo = this.getOne(queryWrapper);
        return UCopy.copyPo2Dto(sysRolePo, SysRoleDto.class);
    }

    @Override
    public TableResult<SysUserDto> findPageUserByRoleId(SysRoleDto sysRoleDto, PageQuery pageQuery) {
        Wrapper<SysRolePo> queryWrapper = Wrappers.<SysRolePo>query()
                .eq("R.ID", sysRoleDto.getId())
                .eq("R.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("R.STATUS", FlagConstant.ENABLED)
                .eq("U.LOGIC_DEL", FlagConstant.DISABLED);
        Page<SysRolePo> pageUserByRoleId = sysRoleMapper.findPageUserByRoleId(queryWrapper, pageQuery.build());
        return TableResult.build(UCopy.convertPagePo2Dto(pageUserByRoleId, SysUserDto.class));
    }

    @Override
    public TableResult<SysUserDto> findPageAllowAssignUserByRoleId(SysRoleDto sysRoleDto, PageQuery pageQuery) {
        SysRolePo sysRolePo = UCopy.copyDto2Po(sysRoleDto, SysRolePo.class);
        Page<SysUserPo> userPoPage = sysRoleMapper.findPageAllowAssignUserByRoleId(sysRolePo, pageQuery.build());
        return TableResult.build(UCopy.convertPagePo2Dto(userPoPage, SysUserDto.class));
    }

    @Override
    @LogRecord(module = RoleModule.ROLE_MANAGEMENT, subModule = RoleModule.SubModule.ASSIGN_USER, message = "role.assignUser")
    public SysRolePo assignUser(Long roleId, List<Long> userIdList) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseGet(SysRolePo::new);
        Set<SysUserRolePo> sysUserRolePoSet = sysRolePo.getSysUserRolePoSet();
        for (Long userId : userIdList) {
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            SysUserRolePk sysUserRolePk = new SysUserRolePk();
            sysUserRolePk.setRoleId(roleId);
            sysUserRolePk.setUserId(userId);
            sysUserRolePo.setSysRoleMenuPk(sysUserRolePk);
            sysUserRolePoSet.add(sysUserRolePo);
        }
        sysRolePo.setSysUserRolePoSet(sysUserRolePoSet);
        return sysRoleRepository.save(sysRolePo);
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
                    sysSensitiveLogBean.setContextOld("分配前部门ID：" + JSONObject.toJSONString(beforeDeptIdList));
                    sysSensitiveLogBean.setContext("分配后部门ID：" + JSONObject.toJSONString(deptIdSet));
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
    public SysRoleDto saveRole(SysRoleDto sysRoleDto) {
        Long roleId = sysRoleDto.getId();
        SysRolePo sysRolePo;
        if (UEmpty.isNull(roleId)) {
            // 新增
            sysRolePo = new SysRolePo();
            UCopy.fullCopy(sysRoleDto, sysRolePo);
        } else {
            // 修改
            sysRolePo = findSysRolePoById(roleId);
            UCopy.halfCopy(sysRoleDto, sysRolePo);
        }
        SysRolePo save = sysRoleRepository.save(sysRolePo);
        return UCopy.copyPo2Dto(save, SysRoleDto.class);
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
