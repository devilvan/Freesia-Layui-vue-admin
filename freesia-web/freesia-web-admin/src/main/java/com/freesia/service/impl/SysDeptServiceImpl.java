package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.DeptModule;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindDeptRolesByDeptIdEntity;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.entity.FindTreeDeptSelectEntity;
import com.freesia.exception.DeptException;
import com.freesia.exception.UserException;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysDeptMapper;
import com.freesia.po.SysDeptPo;
import com.freesia.po.SysRoleDeptPk;
import com.freesia.po.SysRoleDeptPo;
import com.freesia.po.SysRolePo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysDeptRepository;
import com.freesia.repository.SysRoleDeptRepository;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysDeptService;
import com.freesia.service.SysUserService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 部门信息表 业务逻辑类
 * @date 2023-08-17
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDeptPo> implements SysDeptService {
    private final TransactionTemplate transactionTemplate;
    private final SysDeptRepository sysDeptRepository;
    private final SysDeptMapper sysDeptMapper;
    private final SysRoleDeptRepository sysRoleDeptRepository;
    private final SysUserService sysUserService;

    @Override
    public SysDeptDto saveUpdate(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = new SysDeptPo();
        UCopy.fullCopy(sysDeptDto, sysDeptPo);
        sysDeptPo = sysDeptRepository.saveAndFlush(sysDeptPo);
        return UCopy.copyPo2Dto(sysDeptPo, SysDeptDto.class);
    }

    @Override
    public List<SysDeptDto> saveUpdateBatch(List<SysDeptDto> list) {
        List<SysDeptPo> sysDeptPoList = UCopy.fullCopyList(list, SysDeptPo.class);
        sysDeptPoList = sysDeptRepository.saveAllAndFlush(sysDeptPoList);
        return UCopy.fullCopyList(sysDeptPoList, SysDeptDto.class);
    }

    @Override
    public List<FindPageSysDeptListEntity> findListSysDept(SysDeptDto sysDeptDto) {
        // 是否管理员
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.info.null", new Object[] {}));
        boolean isAdmin = Convert.toBool(sysUserService.isAdmin(userId), false);
        if (isAdmin) {
            return sysDeptMapper.findPageSysDeptList(buildWrapper(sysDeptDto));
        } else {
            List<FindPageSysDeptListEntity> list = sysDeptMapper.findPageSysDeptList(buildWrapper(sysDeptDto));
            // 根据查询出的部门，查找其上级部门
            List<Long> ancestorIdList = list.stream()
                    .map(FindPageSysDeptListEntity::getAncestors)
                    .flatMap(ancestor -> Arrays.stream(ancestor.split(",")))
                    .map(Long::parseLong)
                    .distinct()
                    .collect(Collectors.toList());
            List<SysDeptPo> sysDeptPoList = sysDeptMapper.selectBatchIds(ancestorIdList);
            List<FindPageSysDeptListEntity> sysDeptListEntityList = UCopy.fullCopyList(sysDeptPoList, FindPageSysDeptListEntity.class);
            list.addAll(sysDeptListEntityList);
            return list;
        }
    }

    @Override
    public TableResult<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptDto sysDeptDto, PageQuery pageQuery) {
        return sysDeptMapper.findPageSysDeptList(pageQuery.build(), buildWrapper(sysDeptDto));
    }

    @Override
    public List<FindPageSysDeptListEntity> findDeptTreeList(SysDeptDto sysDeptDto) {
        List<FindPageSysDeptListEntity> findPageSysDeptListEntityList = findListSysDept(sysDeptDto);
        return UTree.buildTree(findPageSysDeptListEntityList);
    }

    @Override
    public SysDeptDto findDeptById(Long deptId) {
        LambdaQueryWrapper<SysDeptPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysDeptPo::getDeptName)
                .eq(SysDeptPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysDeptPo::getId, deptId);
        SysDeptPo sysDeptPo = this.getOne(queryWrapper);
        return UCopy.copyPo2Dto(sysDeptPo, SysDeptDto.class);
    }

    @Override
    @LogRecord(module = DeptModule.DEPT_MANAGEMENT, subModule = DeptModule.SubModule.DELETE_DEPT, message = "dept.delete")
    public SysDeptDto deleteDept(Long deptId) {
        SysDeptPo sysDeptPo = sysDeptRepository.findById(deptId).orElseThrow(() -> new DeptException("dept.not.exists", new Object[]{}));
        sysDeptPo.setLogicDel(true);
        sysDeptPo.setDeptStatus(FlagConstant.DISABLED);
        SysDeptPo saveSysDeptPo = sysDeptRepository.save(sysDeptPo);
        return UCopy.copyPo2Dto(saveSysDeptPo, SysDeptDto.class);
    }

    @Override
    public List<FindTreeDeptSelectEntity> findTreeDeptSelect(LoginUserModel loginUserModel) {
        List<FindTreeDeptSelectEntity> findTreeDeptSelectEntityList = findTreeDeptSelectEntityList(loginUserModel);
        FindTreeDeptSelectEntity deptTopParent = buildDeptTopParent();
        findTreeDeptSelectEntityList = UTree.buildTree(findTreeDeptSelectEntityList);
        deptTopParent.setChildren(findTreeDeptSelectEntityList);
        return Collections.singletonList(deptTopParent);
    }

    private FindTreeDeptSelectEntity buildDeptTopParent() {
        FindTreeDeptSelectEntity findTreeDeptSelectEntity = new FindTreeDeptSelectEntity();
        findTreeDeptSelectEntity.setId(AdminConstant.DEPT_TOP_PARENT_ID);
        findTreeDeptSelectEntity.setParentId(AdminConstant.DEPT_TOP_PARENT_ID);
        findTreeDeptSelectEntity.setTitle("顶级目录");
        return findTreeDeptSelectEntity;
    }

    @Override
    public SysDeptDto saveDept(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = buildSaveDeptPo(sysDeptDto);
        sysDeptPo = sysDeptRepository.saveAndFlush(sysDeptPo);
        return UCopy.copyPo2Dto(sysDeptPo, SysDeptDto.class);
    }

    @Override
    public Long findIncrementOrderNum(SysDeptDto sysDeptDto) {
        Long parentId = sysDeptDto.getParentId();
        Long maxOrderNum = sysDeptMapper.findMaxOrderNum(parentId);
        return maxOrderNum == null ? 10L : ((int) (maxOrderNum / 10)) * 10L + 10L;
    }

    @Override
    public List<FindTreeDeptSelectEntity> findTreeAssignDeptSelect(LoginUserModel loginUserModel) {
        List<FindTreeDeptSelectEntity> findTreeDeptSelectEntityList = findTreeDeptSelectEntityList(loginUserModel);
        return UTree.buildTree(findTreeDeptSelectEntityList);
    }

    @Override
    public void assignRole(Long deptId, Set<Long> afterRoleIdSet) {
        SysDeptPo sysDeptPo = sysDeptRepository.findById(deptId).orElseThrow(() -> new DeptException("dept.not.exists", new Object[]{}));
        // 获取并修改分配后的角色
        Set<SysRolePo> sysRolePoSet = sysDeptPo.getSysRolePoSet();
        List<Long> beforeRoleIdList = sysRolePoSet.stream().map(SysRolePo::getId).collect(Collectors.toList());
        Set<SysRoleDeptPo> beforeSysRoleDeptPoSet = UCollection.optimizeInitialCapacitySet(beforeRoleIdList.size());
        for (Long roleId : beforeRoleIdList) {
            SysRoleDeptPo sysRoleDeptPo = new SysRoleDeptPo();
            sysRoleDeptPo.setSysRoleDeptPk(new SysRoleDeptPk(deptId, roleId));
            beforeSysRoleDeptPoSet.add(sysRoleDeptPo);
        }
        Set<SysRoleDeptPo> afterSysRoleDeptPoSet = UCollection.optimizeInitialCapacitySet(afterRoleIdSet.size());
        for (Long roleId : afterRoleIdSet) {
            SysRoleDeptPo sysRoleDeptPo = new SysRoleDeptPo();
            sysRoleDeptPo.setSysRoleDeptPk(new SysRoleDeptPk(deptId, roleId));
            afterSysRoleDeptPoSet.add(sysRoleDeptPo);
        }
        transactionTemplate.execute(status -> {
            SysSensitiveLogBean sysSensitiveLogBean = null;
            try {
                if (UEmpty.isNotEmpty(beforeSysRoleDeptPoSet)) {
                    sysRoleDeptRepository.deleteAllInBatch(beforeSysRoleDeptPoSet);
                }
                if (UEmpty.isNotEmpty(afterSysRoleDeptPoSet)) {
                    sysRoleDeptRepository.saveAll(afterSysRoleDeptPoSet);
                }
                sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                    SysSensitiveLogBean sensitiveLog = new SysSensitiveLogBean();
                    sensitiveLog.setModule(DeptModule.DEPT_MANAGEMENT);
                    sensitiveLog.setSubModule(DeptModule.SubModule.ASSIGN_ROLE);
                    sensitiveLog.setType(DeptModule.SubModule.ASSIGN_ROLE);
                    sensitiveLog.setResult(FlagConstant.SUCCESS);
                    sensitiveLog.setContextOld("分配前角色ID：" + JSONObject.toJSONString(beforeRoleIdList));
                    sensitiveLog.setContext("分配后角色ID：" + JSONObject.toJSONString(afterRoleIdSet));
                    sensitiveLog.setRemark(UMessage.message("assign_role_permissions_success"));
                    return sensitiveLog;
                });
            } catch (Exception e) {
                sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                    SysSensitiveLogBean sensitiveLog = new SysSensitiveLogBean();
                    sensitiveLog.setModule(DeptModule.DEPT_MANAGEMENT);
                    sensitiveLog.setSubModule(DeptModule.SubModule.ASSIGN_ROLE);
                    sensitiveLog.setType(DeptModule.SubModule.ASSIGN_ROLE);
                    sensitiveLog.setResult(FlagConstant.FAILED);
                    sensitiveLog.setRemark(UMessage.message("assign_role_permissions_failed"));
                    return sensitiveLog;
                });
                throw e;
            } finally {
                if (UEmpty.isNotNull(sysSensitiveLogBean)) {
                    USpring.context().publishEvent(sysSensitiveLogBean);
                }
            }
            return status;
        });
    }

    @Override
    public FindDeptRolesByDeptIdEntity findDeptRolesByDeptId(Long deptId) {
        // 获取部门对象
        SysDeptPo sysDeptPo = sysDeptRepository.findById(deptId).orElseThrow(() -> new UserException("dept.query.failed", new Object[] {deptId}));
        // 获取角色
        Set<SysRolePo> sysRolePoSet = sysDeptPo.getSysRolePoSet();
        return buildFindDeptRolesByDeptIdEntity(sysDeptPo, sysRolePoSet);
    }

    private List<FindTreeDeptSelectEntity> findTreeDeptSelectEntityList(LoginUserModel loginUserModel) {
        Long tenantId = loginUserModel.getTenantId();
        QueryWrapper<SysDeptPo> wrapper = Wrappers.<SysDeptPo>query()
                .eq("D.DEPT_STATUS", FlagConstant.ENABLED)
                .eq("D.LOGIC_DEL", FlagConstant.DISABLED)
                .eq(UEmpty.isNotNull(tenantId), "D.TENANT_ID", tenantId)
                .orderByAsc("D.ORDER_NUM");
        return sysDeptMapper.findTreeDeptSelect(wrapper);
    }

    private FindDeptRolesByDeptIdEntity buildFindDeptRolesByDeptIdEntity(SysDeptPo sysDeptPo, Set<SysRolePo> sysRolePoSet) {
        FindDeptRolesByDeptIdEntity findDeptRolesByDeptIdEntity = new FindDeptRolesByDeptIdEntity();
        findDeptRolesByDeptIdEntity.setDeptId(sysDeptPo.getId());
        findDeptRolesByDeptIdEntity.setDeptName(sysDeptPo.getDeptName());
        Set<Long> sysRoleIdList = sysRolePoSet.stream().map(SysRolePo::getId).collect(Collectors.toSet());
        findDeptRolesByDeptIdEntity.setSelectedRoles(sysRoleIdList);
        return findDeptRolesByDeptIdEntity;
    }

    private SysDeptPo buildSaveDeptPo(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = new SysDeptPo();
        Long id = sysDeptDto.getId();
        if (UEmpty.isNotNull(id)) {
            sysDeptPo = sysDeptRepository.findById(id).orElseThrow(() -> new DeptException("dept.query.failed", new Object[]{id}));
            UCopy.halfCopy(sysDeptDto, sysDeptPo);
        } else {
            Long parentId = sysDeptDto.getParentId();
            sysDeptPo.setParentId(parentId);
            sysDeptPo.setAncestors(sysDeptDto.getAncestors());
            sysDeptPo.setDeptName(sysDeptDto.getDeptName());
            sysDeptPo.setOrderNum(sysDeptDto.getOrderNum());
            sysDeptPo.setLeader(sysDeptDto.getLeader());
            sysDeptPo.setTelNo(sysDeptDto.getTelNo());
            sysDeptPo.setEmail(sysDeptDto.getEmail());
            sysDeptPo.setDeptStatus(FlagConstant.ENABLED);
            sysDeptPo.setRemark(sysDeptDto.getRemark());
        }
        return sysDeptPo;
    }

    /**
     * 构建SQL
     *
     * @param sysDeptDto 查询参数
     * @return 构建出的SQL对象
     */
    private Wrapper<SysDeptPo> buildWrapper(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = new SysDeptPo();
        UCopy.fullCopy(sysDeptDto, sysDeptPo);
        return Wrappers.<SysDeptPo>query()
                .eq("D.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("D.DEPT_STATUS", UEmpty.isEmpty(sysDeptPo.getDeptStatus()) ? FlagConstant.ENABLED : sysDeptPo.getDeptStatus())
                .eq(ObjectUtil.isNotNull(sysDeptPo.getParentId()), "D.PARENT_ID", sysDeptPo.getParentId())
                .like(ObjectUtil.isNotNull(sysDeptPo.getDeptName()), "D.DEPT_NAME", sysDeptPo.getDeptName())
                .between(ObjectUtil.isNotNull(sysDeptDto.getCreateTimeFrom()) && ObjectUtil.isNotNull(sysDeptDto.getCreateTimeTo())
                        , "D.CREATE_TIME", sysDeptDto.getCreateTimeFrom(), sysDeptDto.getCreateTimeTo())
                .orderByAsc("D.PARENT_ID")
                .orderByAsc("D.ORDER_NUM");
    }
}
