package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.MenuModule;
import com.freesia.constant.RoleModule;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindDeptRolesByRoleIdEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.exception.RoleException;
import com.freesia.exception.UserException;
import com.freesia.mapper.SysRoleMapper;
import com.freesia.po.*;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysMenuRepository;
import com.freesia.repository.SysRoleRepository;
import com.freesia.service.SysRoleService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SysRoleRepository sysRoleRepository;
    private final SysMenuRepository sysMenuRepository;
    private final SysRoleMapper sysRoleMapper;

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
        Set<SysMenuPo> oldSysMenuPoSet = sysRolePo.getSysMenuPoSet();
        List<Long> oldMenuIdList = UStream.toList(oldSysMenuPoSet, SysMenuPo::getId);
        if (AdminConstant.ADMIN.equals(sysRolePo.getRoleKey())) {
            return;
        }
        List<SysMenuPo> sysMenuPoList = sysMenuRepository.findAllById(menuIdList);
        sysRolePo.setDataScope(dataScope);
        sysRolePo.setSysMenuPoSet(new HashSet<>(sysMenuPoList));
        SysRolePo saveSysRolePo = sysRoleRepository.save(sysRolePo);
        // 记录操作日志
        Set<SysMenuPo> newSysMenuPoSet = saveSysRolePo.getSysMenuPoSet();
        List<Long> newMenuIdList = UStream.toList(newSysMenuPoSet, SysMenuPo::getId);
        SysSensitiveLogBean sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
            SysSensitiveLogBean sensitiveLog = new SysSensitiveLogBean();
            sensitiveLog.setModule(RoleModule.ROLE_MANAGEMENT);
            sensitiveLog.setSubModule(MenuModule.SubModule.ASSIGN_MENU_PERMISSIONS);
            sensitiveLog.setType(MenuModule.SubModule.ASSIGN_MENU_PERMISSIONS);
            sensitiveLog.setResult(FlagConstant.SUCCESS);
            sensitiveLog.setContextOld(JSONObject.toJSONString(oldMenuIdList));
            sensitiveLog.setContext(JSONObject.toJSONString(newMenuIdList));
            sensitiveLog.setRemark(UMessage.message("assigned_menu_permissions_success"));
            return sensitiveLog;
        });
        USpring.context().publishEvent(sysSensitiveLogBean);
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
    public void assignUser(Long roleId, List<Long> userIdList) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseGet(SysRolePo::new);
        Set<SysUserRolePo> sysUserRolePoSet = sysRolePo.getSysUserRolePoSet();
        for (Long userId : userIdList) {
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            SysUserRolePo.SysUserRolePk sysUserRolePk = new SysUserRolePo.SysUserRolePk();
            sysUserRolePk.setRoleId(roleId);
            sysUserRolePk.setUserId(userId);
            sysUserRolePo.setSysRoleMenuPk(sysUserRolePk);
            sysUserRolePoSet.add(sysUserRolePo);
        }
        sysRolePo.setSysUserRolePoSet(sysUserRolePoSet);
        sysRoleRepository.save(sysRolePo);
    }

    @Override
    public void cancelAssignUser(Long roleId, List<Long> userIdList) {
        sysRoleRepository.cancelAssignUser(roleId, userIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDept(Long roleId, Set<Long> deptIdSet) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseThrow(() -> new RoleException("role.not.exists"));
        // 获取并修改分配后的角色
        Set<SysDeptPo> sysDeptPoSet = sysRolePo.getSysDeptPoSet();
        List<Long> deptIdList = sysDeptPoSet.stream().map(SysDeptPo::getId).collect(Collectors.toList());
        Set<SysRoleDeptPo> afterSysRoleDeptPoSet = UCollection.optimizeInitialCapacitySet(deptIdSet.size());
        for (Long deptId : deptIdSet) {
            SysRoleDeptPo sysRoleDeptPo = new SysRoleDeptPo();
            sysRoleDeptPo.setSysRoleDeptPk(new SysRoleDeptPo.SysRoleDeptPk(deptId, roleId));
            afterSysRoleDeptPoSet.add(sysRoleDeptPo);
        }
        SysSensitiveLogBean sysSensitiveLogBean;
        try {
            sysRoleRepository.removeDeptRelationByRoleId(roleId);
            // 设置分配后的部门-角色关联对象要在删除之后，否则会触发级联操作，导致SQL执行顺序变为insert->update->delete影响操作结果
            sysRolePo.setSysRoleDeptPoSet(afterSysRoleDeptPoSet);
            sysRoleRepository.save(sysRolePo);
            sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean sensitiveLog = new SysSensitiveLogBean();
                sensitiveLog.setModule(RoleModule.ROLE_MANAGEMENT);
                sensitiveLog.setSubModule(RoleModule.SubModule.ASSIGN_DEPT);
                sensitiveLog.setType(RoleModule.SubModule.ASSIGN_DEPT);
                sensitiveLog.setResult(FlagConstant.SUCCESS);
                sensitiveLog.setContextOld("分配前部门ID：" + JSONObject.toJSONString(deptIdList));
                sensitiveLog.setContext("分配后部门ID：" + JSONObject.toJSONString(deptIdSet));
                sensitiveLog.setRemark(UMessage.message("assign_dept_permissions_success"));
                return sensitiveLog;
            });
        } catch (Exception e) {
            e.printStackTrace();
            sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean sensitiveLog = new SysSensitiveLogBean();
                sensitiveLog.setModule(RoleModule.ROLE_MANAGEMENT);
                sensitiveLog.setSubModule(RoleModule.SubModule.ASSIGN_DEPT);
                sensitiveLog.setType(RoleModule.SubModule.ASSIGN_DEPT);
                sensitiveLog.setResult(FlagConstant.FAILED);
                sensitiveLog.setRemark(UMessage.message("assign_dept_permissions_failed"));
                return sensitiveLog;
            });
        }
        USpring.context().publishEvent(sysSensitiveLogBean);
    }

    @Override
    public FindDeptRolesByRoleIdEntity findDeptRolesByRoleId(Long roleId) {
        // 获取角色对象
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseThrow(() -> new UserException("role.query.failed", roleId));
        // 获取部门
        Set<SysDeptPo> sysDeptPoSet = sysRolePo.getSysDeptPoSet();
        return buildFindDeptRolesByRoleIdEntity(sysRolePo, sysDeptPoSet);
    }

    private FindDeptRolesByRoleIdEntity buildFindDeptRolesByRoleIdEntity(SysRolePo sysRolePo, Set<SysDeptPo> sysDeptPoSet) {
        FindDeptRolesByRoleIdEntity findDeptRolesByRoleIdEntity = new FindDeptRolesByRoleIdEntity();
        findDeptRolesByRoleIdEntity.setRoleId(sysRolePo.getId());
        findDeptRolesByRoleIdEntity.setRoleName(sysRolePo.getRoleName());
        Set<Long> sysDeptIdList = sysDeptPoSet.stream().map(SysDeptPo::getId).collect(Collectors.toSet());
        findDeptRolesByRoleIdEntity.setSelectedDept(sysDeptIdList);
        return findDeptRolesByRoleIdEntity;
    }
}
