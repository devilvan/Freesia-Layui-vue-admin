package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.freesia.satoken.bean.SysSensitiveLogBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.DeptModule;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SysDeptConverter;
import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindDeptRolesByDeptIdEntity;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.entity.FindTreeDeptSelectEntity;
import com.freesia.exception.DeptException;
import com.freesia.exception.UserException;
import com.freesia.json.util.UJSON;
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
import com.freesia.vo.SysDeptVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 部门信息表 业务逻辑类
 * @date 2023-08-17
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends BaseServiceImpl<SysDeptMapper, SysDeptVo, SysDeptDto, SysDeptPo> implements SysDeptService {
    private final TransactionTemplate transactionTemplate;
    private final SysDeptRepository sysDeptRepository;
    private final SysDeptMapper sysDeptMapper;
    private final SysRoleDeptRepository sysRoleDeptRepository;
    private final SysUserService sysUserService;
    private final SysDeptConverter sysDeptConverter;


    @Override
    protected MapStructConverter<SysDeptVo, SysDeptDto, SysDeptPo> getMapStructConverter() {
        return sysDeptConverter;
    }

    @Override
    protected JpaRepository<SysDeptPo, Long> getRepository() {
        return sysDeptRepository;
    }

    @Override
    protected Class<SysDeptDto> getDtoClass() {
        return SysDeptDto.class;
    }

    @Override
    protected Class<SysDeptPo> getPoClass() {
        return SysDeptPo.class;
    }

    @Override
    protected Wrapper<SysDeptPo> buildQueryWrapper(@NonNull SysDeptDto dto) {
        return Wrappers.<SysDeptPo>query()
                .eq("D.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("D.DEPT_STATUS", UEmpty.isEmpty(dto.getDeptStatus()) ? FlagConstant.ENABLED : dto.getDeptStatus())
                .eq(ObjectUtil.isNotNull(dto.getParentId()), "D.PARENT_ID", dto.getParentId())
                .like(ObjectUtil.isNotNull(dto.getDeptName()), "D.DEPT_NAME", dto.getDeptName())
                .between(ObjectUtil.isNotNull(dto.getCreateTimeFrom()) && ObjectUtil.isNotNull(dto.getCreateTimeTo())
                        , "D.CREATE_TIME", dto.getCreateTimeFrom(), dto.getCreateTimeTo())
                .orderByAsc("D.PARENT_ID")
                .orderByAsc("D.ORDER_NUM");
    }

    @Override
    public List<FindPageSysDeptListEntity> findListSysDept(SysDeptDto sysDeptDto) {
        // 是否管理员
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new UserException("user.info.null", new Object[]{}));
        boolean isAdmin = Convert.toBool(sysUserService.isAdmin(userId), false);
        if (isAdmin) {
            return sysDeptMapper.findPageSysDeptList(buildQueryWrapper(sysDeptDto));
        } else {
            List<FindPageSysDeptListEntity> list = sysDeptMapper.findPageSysDeptList(buildQueryWrapper(sysDeptDto));
            // 根据查询出的部门，查找其上级部门
//            List<Long> ancestorIdList = list.stream()
//                    .map(FindPageSysDeptListEntity::getAncestors)
//                    .flatMap(ancestor -> Arrays.stream(ancestor.split(",")))
//                    .map(Long::parseLong)
//                    .distinct()
//                    .collect(Collectors.toList());
//            List<SysDeptPo> sysDeptPoList = sysDeptMapper.selectBatchIds(ancestorIdList);
//            List<FindPageSysDeptListEntity> sysDeptListEntityList = UCopy.fullCopyList(sysDeptPoList, FindPageSysDeptListEntity.class);
//            list.addAll(sysDeptListEntityList);
            return list;
        }
    }

    @Override
    public TableResult<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptDto sysDeptDto, PageQuery pageQuery) {
        return sysDeptMapper.findPageSysDeptList(pageQuery.build(), buildQueryWrapper(sysDeptDto));
    }

    @Override
    public List<FindPageSysDeptListEntity> findDeptTreeList(SysDeptDto sysDeptDto) {
        List<FindPageSysDeptListEntity> findPageSysDeptListEntityList = findListSysDept(sysDeptDto);
        return UTree.buildTree(findPageSysDeptListEntityList);
    }

    @Override
    public SysDeptDto findOne(SysDeptDto sysDeptDto) {
        LambdaQueryWrapper<SysDeptPo> queryWrapper = new LambdaQueryWrapper<SysDeptPo>()
                .select(SysDeptPo::getDeptName)
                .eq(SysDeptPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysDeptPo::getId, sysDeptDto.getId());
        return findOne(sysDeptDto, queryWrapper);
    }

    @Override
    @LogRecord(module = DeptModule.DEPT_MANAGEMENT, subModule = DeptModule.SubModule.DELETE_DEPT, message = "dept.delete")
    public SysDeptDto deleteDept(Long deptId) {
        SysDeptPo sysDeptPo = sysDeptRepository.findById(deptId).orElseThrow(() -> new DeptException("dept.not.exists", new Object[]{}));
        sysDeptPo.setLogicDel(true);
        sysDeptPo.setDeptStatus(FlagConstant.DISABLED);
        return sysDeptConverter.convertPo2Dto(sysDeptRepository.save(sysDeptPo));
    }

    @Override
    public List<FindTreeDeptSelectEntity> findTreeDeptSelect(LoginUserModel loginUserModel) {
        List<FindTreeDeptSelectEntity> findTreeDeptSelectEntityList = findTreeDeptSelectEntityList(loginUserModel);
        FindTreeDeptSelectEntity deptTopParent = buildDeptTopParent();
        findTreeDeptSelectEntityList = UTree.buildTree(findTreeDeptSelectEntityList);
        deptTopParent.setChildren(findTreeDeptSelectEntityList);
        return Collections.singletonList(deptTopParent);
    }

    @Override
    public SysDeptDto saveDept(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = buildSaveDeptPo(sysDeptDto);
        return sysDeptConverter.convertPo2Dto(sysDeptRepository.saveAndFlush(sysDeptPo));
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
            SysSensitiveLogBean saveSysSensitiveLogBean = null;
            try {
                if (UEmpty.isNotEmpty(beforeSysRoleDeptPoSet)) {
                    sysRoleDeptRepository.deleteAllInBatch(beforeSysRoleDeptPoSet);
                }
                if (UEmpty.isNotEmpty(afterSysRoleDeptPoSet)) {
                    sysRoleDeptRepository.saveAll(afterSysRoleDeptPoSet);
                }
                saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
                    sysSensitiveLogBean.setModule(DeptModule.DEPT_MANAGEMENT);
                    sysSensitiveLogBean.setSubModule(DeptModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setType(DeptModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setResult(FlagConstant.SUCCESS);
                    sysSensitiveLogBean.setContextOld("分配前角色ID：" + UJSON.toJSONString(beforeRoleIdList));
                    sysSensitiveLogBean.setContext("分配后角色ID：" + UJSON.toJSONString(afterRoleIdSet));
                    sysSensitiveLogBean.setRemark(UMessage.message("assign_role_permissions_success"));
                    return sysSensitiveLogBean;
                });
            } catch (Exception e) {
                saveSysSensitiveLogBean = USecurity.recordSensitiveLog(sysSensitiveLogBean -> {
                    sysSensitiveLogBean.setModule(DeptModule.DEPT_MANAGEMENT);
                    sysSensitiveLogBean.setSubModule(DeptModule.SubModule.ASSIGN_ROLE);
                    sysSensitiveLogBean.setType(DeptModule.SubModule.ASSIGN_ROLE);
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
    public FindDeptRolesByDeptIdEntity findDeptRolesByDeptId(Long deptId) {
        // 获取部门对象
        SysDeptPo sysDeptPo = sysDeptRepository.findById(deptId).orElseThrow(() -> new UserException("dept.query.failed", new Object[]{deptId}));
        // 获取角色
        Set<SysRolePo> sysRolePoSet = sysDeptPo.getSysRolePoSet();
        return buildFindDeptRolesByDeptIdEntity(sysDeptPo, sysRolePoSet);
    }

    private FindTreeDeptSelectEntity buildDeptTopParent() {
        FindTreeDeptSelectEntity findTreeDeptSelectEntity = new FindTreeDeptSelectEntity();
        findTreeDeptSelectEntity.setId(AdminConstant.DEPT_TOP_PARENT_ID);
        findTreeDeptSelectEntity.setParentId(AdminConstant.DEPT_TOP_PARENT_ID);
        findTreeDeptSelectEntity.setTitle("顶级目录");
        return findTreeDeptSelectEntity;
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
            sysDeptConverter.updateSysDeptDto2Po(sysDeptDto, sysDeptPo);
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
}
