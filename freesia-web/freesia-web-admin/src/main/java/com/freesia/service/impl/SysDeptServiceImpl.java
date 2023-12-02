package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysDeptDto;
import com.freesia.entity.FindPageSysDeptListEntity;
import com.freesia.mapper.SysDeptMapper;
import com.freesia.po.SysDeptPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysDeptRepository;
import com.freesia.service.SysDeptService;
import com.freesia.util.UCopy;
import com.freesia.util.UTree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public SysDeptPo saveUpdate(SysDeptDto sysDeptDto) {
        SysDeptPo sysDeptPo = new SysDeptPo();
        UCopy.fullCopy(sysDeptDto, sysDeptPo);
        return sysDeptRepository.saveAndFlush(sysDeptPo);
    }

    @Override
    public List<SysDeptPo> saveUpdateBatch(List<SysDeptDto> list) {
        List<SysDeptPo> sysDeptPoList = UCopy.fullCopyList(list, SysDeptPo.class);
        return sysDeptRepository.saveAllAndFlush(sysDeptPoList);
    }

    @Override
    public List<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptDto sysDeptDto) {
        return sysDeptMapper.findPageSysDeptList(buildWrapper(sysDeptDto));
    }

    @Override
    public TableResult<FindPageSysDeptListEntity> findPageSysDeptList(SysDeptDto sysDeptDto, PageQuery pageQuery) {
        return sysDeptMapper.findPageSysDeptList(pageQuery.build(), buildWrapper(sysDeptDto));
    }

    @Override
    public List<FindPageSysDeptListEntity> findDeptTreeList(SysDeptDto sysDeptDto) {
        List<FindPageSysDeptListEntity> findPageSysDeptListEntityList = findPageSysDeptList(sysDeptDto);
        return UTree.buildTree(findPageSysDeptListEntityList);
    }

    @Override
    public SysDeptDto findDeptById(Long deptId) {
        LambdaQueryWrapper<SysDeptPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysDeptPo::getDeptName)
                .eq(SysDeptPo::getLogicDel, FlagConstant.ENABLED)
                .eq(SysDeptPo::getId, deptId);
        SysDeptPo sysDeptPo = this.getOne(queryWrapper);
        return UCopy.copyPo2Dto(sysDeptPo, SysDeptDto.class);
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
                .eq("D.LOGIC_DEL", FlagConstant.ENABLED)
                .eq(ObjectUtil.isNotNull(sysDeptPo.getDeptStatus()), "D.DEPT_STATUS", sysDeptPo.getDeptStatus())
                .eq(ObjectUtil.isNotNull(sysDeptPo.getParentId()), "D.PARENT_ID", sysDeptPo.getParentId())
                .like(ObjectUtil.isNotNull(sysDeptPo.getDeptName()), "D.DEPT_NAME", sysDeptPo.getDeptName())
                .between(ObjectUtil.isNotNull(sysDeptDto.getCreateTimeFrom()) && ObjectUtil.isNotNull(sysDeptDto.getCreateTimeTo())
                        , "D.CREATE_TIME", sysDeptDto.getCreateTimeFrom(), sysDeptDto.getCreateTimeTo())
                .orderByAsc("D.PARENT_ID")
                .orderByAsc("D.ORDER_NUM");
    }
}
