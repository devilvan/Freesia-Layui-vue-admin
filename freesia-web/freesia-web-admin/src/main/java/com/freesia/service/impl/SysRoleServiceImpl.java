package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.AdminConstant;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.SysModule;
import com.freesia.dto.SysRoleDto;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.mapper.SysRoleMapper;
import com.freesia.model.LoginUserModel;
import com.freesia.po.SysMenuPo;
import com.freesia.po.SysRolePo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysMenuRepository;
import com.freesia.repository.SysRoleRepository;
import com.freesia.service.SysRoleService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
        List<SysRolePo> sysRolePoList = UCopy.fullCopyCollections(list, SysRolePo.class);
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
        Wrapper<SysRolePo> wrapper = buildQueryWrapper(sysRoleDto);
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
            String ip = UServlet.getInitiatedRequestIp();
            LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
            sensitiveLog.setOperatorId(loginUser.getUserId());
            sensitiveLog.setOperatorName(loginUser.getUsername());
            sensitiveLog.setDeptId(loginUser.getDeptId());
            sensitiveLog.setDeptName(loginUser.getDeptName());
            sensitiveLog.setMethodType(UServlet.getMethod());
            sensitiveLog.setUrl(UServlet.getRequestUri());
            sensitiveLog.setBeOperatedId(loginUser.getUserId());
            sensitiveLog.setBeOperatedName(loginUser.getUsername());
            sensitiveLog.setIpAddress(ip);
            sensitiveLog.setLocation(URegion.getRealAddressByIp(ip));
            sensitiveLog.setOperateTime(new Date());
            sensitiveLog.setBrowser(UServlet.getBrowser());
            sensitiveLog.setOs(UServlet.getOs());
            sensitiveLog.setModule(SysModule.ROLE_MANAGEMENT);
            sensitiveLog.setSubModule(SysModule.ASSIGN_MENU_PERMISSIONS);
            sensitiveLog.setType(SysModule.ASSIGN_MENU_PERMISSIONS);
            sensitiveLog.setResult(FlagConstant.SUCCESS);
            sensitiveLog.setContextOld(JSONObject.toJSONString(oldMenuIdList));
            sensitiveLog.setContext(JSONObject.toJSONString(newMenuIdList));
            sensitiveLog.setSign(loginUser.getUsername());
            sensitiveLog.setRemark(UMessage.message("assigned_menu_permissions_success"));
            return sensitiveLog;
        });
        USpring.context().publishEvent(sysSensitiveLogBean);
    }

    /**
     * 构建SQL
     *
     * @param sysRoleDto 查询参数
     * @return 构建出的SQL对象
     */
    private Wrapper<SysRolePo> buildQueryWrapper(SysRoleDto sysRoleDto) {
        SysRolePo sysRolePo = new SysRolePo();
        UCopy.fullCopy(sysRoleDto, sysRolePo);
        return Wrappers.<SysRolePo>query()
                .eq("R.LOGIC_DEL", FlagConstant.ENABLED)
                .eq(UEmpty.isNotEmpty(sysRolePo.getStatus()), "R.STATUS", FlagConstant.ENABLED)
                .like(ObjectUtil.isNotNull(sysRolePo.getRoleName()), "R.ROLE_NAME", sysRolePo.getRoleName())
                .like(ObjectUtil.isNotNull(sysRolePo.getRoleKey()), "R.ROLE_KEY", sysRolePo.getRoleKey())
                .between(ObjectUtil.isNotNull(sysRoleDto.getCreateTimeFrom()) && ObjectUtil.isNotNull(sysRoleDto.getCreateTimeTo()),
                        "R.CREATE_TIME", sysRoleDto.getCreateTimeFrom(), sysRoleDto.getCreateTimeTo())
                .orderByAsc("R.ORDER_NUM");
    }
}
