package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.entity.FindTreeDeptSelectEntity;
import com.freesia.exception.DeptException;
import com.freesia.mapper.SysDeptMapper;
import com.freesia.model.LoginUserModel;
import com.freesia.po.SysDeptPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysDeptRepository;
import com.freesia.service.SysDeptService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UTree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 部门信息表 业务逻辑类
 * @date 2023-08-17
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDeptPo> implements SysDeptService {
    private final SysDeptRepository sysDeptRepository;
    private final SysDeptMapper sysDeptMapper;

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
        return sysDeptMapper.findPageSysDeptList(buildWrapper(sysDeptDto));
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
    public SysDeptDto deleteDept(SysDeptDto sysDeptDto) {
        return null;
    }

    @Override
    public List<FindTreeDeptSelectEntity> findTreeDeptSelect(LoginUserModel loginUserModel) {
        Long tenantId = loginUserModel.getTenantId();
        QueryWrapper<SysDeptPo> wrapper = Wrappers.<SysDeptPo>query()
                .eq("D.DEPT_STATUS", FlagConstant.ENABLED)
                .eq("D.LOGIC_DEL", FlagConstant.DISABLED)
                .eq(UEmpty.isNotNull(tenantId), "D.TENANT_ID", tenantId)
                .orderByAsc("D.ORDER_NUM");
        List<FindTreeDeptSelectEntity> findTreeDeptSelectEntityList = sysDeptMapper.findTreeDeptSelect(wrapper);
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
        Long tenantId = loginUserModel.getTenantId();
        QueryWrapper<SysDeptPo> wrapper = Wrappers.<SysDeptPo>query()
                .eq("D.DEPT_STATUS", FlagConstant.ENABLED)
                .eq("D.LOGIC_DEL", FlagConstant.DISABLED)
                .eq(UEmpty.isNotNull(tenantId), "D.TENANT_ID", tenantId)
                .orderByAsc("D.ORDER_NUM");
        List<FindTreeDeptSelectEntity> findTreeDeptSelectEntityList = sysDeptMapper.findTreeDeptSelect(wrapper);
        return UTree.buildTree(findTreeDeptSelectEntityList);
    }

    private SysDeptPo buildSaveDeptPo(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = new SysDeptPo();
        Long id = sysDeptDto.getId();
        if (UEmpty.isNotNull(id)) {
            sysDeptPo = sysDeptRepository.findById(id).orElseThrow(() -> new DeptException("dept.query.failed", id));
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
                .eq(ObjectUtil.isNotNull(sysDeptPo.getDeptStatus()), "D.DEPT_STATUS", sysDeptPo.getDeptStatus())
                .eq(ObjectUtil.isNotNull(sysDeptPo.getParentId()), "D.PARENT_ID", sysDeptPo.getParentId())
                .like(ObjectUtil.isNotNull(sysDeptPo.getDeptName()), "D.DEPT_NAME", sysDeptPo.getDeptName())
                .between(ObjectUtil.isNotNull(sysDeptDto.getCreateTimeFrom()) && ObjectUtil.isNotNull(sysDeptDto.getCreateTimeTo())
                        , "D.CREATE_TIME", sysDeptDto.getCreateTimeFrom(), sysDeptDto.getCreateTimeTo())
                .orderByAsc("D.PARENT_ID")
                .orderByAsc("D.ORDER_NUM");
    }
}
