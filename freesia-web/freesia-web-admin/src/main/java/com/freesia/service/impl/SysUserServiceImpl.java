package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindPageSysUserByDeptEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.helper.DataBaseHelper;
import com.freesia.mapper.SysDeptMapper;
import com.freesia.mapper.SysUserMapper;
import com.freesia.po.SysDeptPo;
import com.freesia.po.SysUserPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysUserRepository;
import com.freesia.service.SysUserService;
import com.freesia.util.UCopy;
import com.freesia.util.USql;
import com.freesia.util.UStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 用户信息表 业务逻辑类
 * @date 2023-08-14
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserPo> implements SysUserService {
    private final SysUserRepository sysUserRepository;
    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;

    @Override
    public SysUserPo saveUpdate(SysUserDto sysUserDto) {
        SysUserPo sysUserPo = new SysUserPo();
        UCopy.fullCopy(sysUserDto, sysUserPo);
        return sysUserRepository.saveAndFlush(sysUserPo);
    }

    @Override
    public List<SysUserPo> saveUpdateBatch(List<SysUserDto> list) {
        List<SysUserPo> sysUserPoList = UCopy.fullCopyCollections(list, SysUserPo.class);
        return sysUserRepository.saveAllAndFlush(sysUserPoList);
    }

    @Override
    public SysUserPo findOneByUsername(String username) {
        LambdaQueryWrapper<SysUserPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUserPo::getUserName, SysUserPo::getAccountStatus);
        queryWrapper.eq(SysUserPo::getUserName, username);
        return this.getOne(queryWrapper);
    }

    @Override
    public SysUserPo findByUsername(String username) {
        return sysUserRepository.findByUserNameAndLogicDel(username, false);
    }

    @Override
    public SysUserDto findUserById(Long userId) {
        SysUserPo sysUserPo = sysUserRepository.findById(userId).orElseGet(SysUserPo::new);
        SysUserDto sysUserDto = new SysUserDto();
        UCopy.fullCopy(sysUserPo, sysUserDto);
        return sysUserDto;
    }

    @Override
    public boolean checkUserNameUnique(SysUserDto sysUserDto) {
        SysUserPo sysUserPo = new SysUserPo();
        UCopy.fullCopy(sysUserDto, sysUserPo);
        return sysUserMapper.checkUserNameUnique(sysUserPo);
    }

    @Override
    public boolean register(SysUserDto sysUserDto) {
        SysUserPo sysUserPo = new SysUserPo();
        UCopy.fullCopy(sysUserDto, sysUserPo);
        return Convert.toBool(sysUserRepository.save(sysUserPo), false);
    }

    @Override
    public TableResult<FindPageSysUserListEntity> findPageSysUserList(SysUserDto sysUserDto, PageQuery pageQuery) {
        // 构建SQL 通过部门权限限制查询当前用户下能够查找的用户的列表
        Wrapper<SysUserPo> sysUserPoWrapper = USql.buildQueryWrapper(() -> Wrappers.<SysUserPo>query()
                .eq("U.LOGIC_DEL", FlagConstant.ENABLED)
                .eq("U.ACCOUNT_STATUS", FlagConstant.ENABLED)
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
        Page<FindPageSysUserListEntity> page = sysUserMapper.findPageSysUserList(pageQuery.build(), sysUserPoWrapper);
        return TableResult.build(page);
    }

    @Override
    public TableResult<FindPageSysUserByDeptEntity> findPageSysUserByDept(SysUserDto sysUserDto, PageQuery pageQuery) {
        Page<FindPageSysUserByDeptEntity> page = sysUserMapper.findPageSysUserByDept(pageQuery.build(), Wrappers.<SysUserPo>query()
                .eq("U.LOGIC_DEL", FlagConstant.ENABLED)
                .eq("U.ACCOUNT_STATUS", FlagConstant.ENABLED)
                .eq("D.LOGIC_DEL", FlagConstant.ENABLED)
                .eq("D.DEPT_STATUS", FlagConstant.ENABLED)
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
        LambdaQueryWrapper<SysUserPo> queryWrapper = new LambdaQueryWrapper<SysUserPo>()
                .eq(SysUserPo::getLogicDel, FlagConstant.ENABLED)
                .eq(SysUserPo::getId, userId);
        SysUserPo sysUserPo = sysUserMapper.findCurrentUserProfile(queryWrapper);
        return UCopy.copyPo2Dto(sysUserPo, SysUserDto.class);
    }

    @Override
    public void saveUserInfo(SysUserDto sysUserDto) {
        Long id = sysUserDto.getId();
        SysUserPo sysUserPo = sysUserRepository.getById(id);
        UCopy.halfCopy(sysUserDto, sysUserPo);
        sysUserRepository.save(sysUserPo);
    }
}
