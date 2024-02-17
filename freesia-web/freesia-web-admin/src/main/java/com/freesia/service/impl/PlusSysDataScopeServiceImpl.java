package com.freesia.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.helper.DataBaseHelper;
import com.freesia.mapper.SysDeptMapper;
import com.freesia.po.SysDeptPo;
import com.freesia.po.SysRolePo;
import com.freesia.repository.SysRoleRepository;
import com.freesia.service.PlusSysDataScopeService;
import com.freesia.util.UStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description Mybatis Plus 数据权限 业务逻辑类
 * @date 2023-09-06
 */
@Component("sdss")
@RequiredArgsConstructor
public class PlusSysDataScopeServiceImpl implements PlusSysDataScopeService {

    private final SysRoleRepository sysRoleRepository;
    private final SysDeptMapper sysDeptMapper;

    @Override
    public String getRoleCustom(Long roleId) {
        SysRolePo sysRolePo = sysRoleRepository.findById(roleId).orElseGet(SysRolePo::new);
        Set<SysDeptPo> sysDeptPoSet = sysRolePo.getSysDeptPoSet();
        if (CollUtil.isNotEmpty(sysDeptPoSet)) {
            return UStream.join(sysDeptPoSet, rd -> Convert.toStr(rd.getId()));
        }
        return null;
    }

    @Override
    public String getDeptAndChild(Long deptId) {
        List<SysDeptPo> deptList = sysDeptMapper.selectList(new LambdaQueryWrapper<SysDeptPo>()
                .select(SysDeptPo::getId)
                .apply(DataBaseHelper.findInSet(deptId, "ancestors")));
        List<Long> ids = UStream.toList(deptList, SysDeptPo::getId);
        ids.add(deptId);
        List<SysDeptPo> list = sysDeptMapper.selectList(new LambdaQueryWrapper<SysDeptPo>()
                .select(SysDeptPo::getId)
                .in(SysDeptPo::getId, ids));
        if (CollUtil.isNotEmpty(list)) {
            return UStream.join(list, d -> Convert.toStr(d.getId()));
        }
        return null;
    }
}
