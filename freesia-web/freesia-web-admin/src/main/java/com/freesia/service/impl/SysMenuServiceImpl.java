package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.*;
import com.freesia.dto.AssignButtonDto;
import com.freesia.dto.MetaDto;
import com.freesia.dto.RouterDto;
import com.freesia.dto.SysMenuDto;
import com.freesia.entity.FindAllMenuTreeEntity;
import com.freesia.entity.FindMenuListByUserIdEntity;
import com.freesia.entity.FindTreeMenuSelectEntity;
import com.freesia.entity.RouterEntity;
import com.freesia.exception.ServiceException;
import com.freesia.log.annotation.LogRecord;
import com.freesia.mapper.SysMenuMapper;
import com.freesia.mapper.SysRoleMapper;
import com.freesia.po.SysMenuPo;
import com.freesia.po.SysRoleMenuPk;
import com.freesia.po.SysRoleMenuPo;
import com.freesia.po.SysRolePo;
import com.freesia.repository.SysMenuRepository;
import com.freesia.repository.SysRoleMenuRepository;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysMenuService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 业务逻辑类
 * @date 2023-08-17
 */
@Valid
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuPo> implements SysMenuService {
    private static final String PREFIX = "/";
    private static final String NO_REDIRECT = "noRedirect";
    private static final String COMPONENT_REGEX = "([A-Za-z0-9$_])+(/[A-Za-z0-9$_]*)$";

    private final TransactionTemplate transactionTemplate;
    private final SysMenuMapper sysMenuMapper;
    private final SysMenuRepository sysMenuRepository;
    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuRepository sysRoleMenuRepository;

    @Override
    public SysMenuDto saveUpdate(SysMenuDto sysMenuDto) {
        SysMenuPo sysMenuPo = UCopy.copyDto2Po(sysMenuDto, SysMenuPo.class);
        return UCopy.copyPo2Dto(sysMenuRepository.saveAndFlush(sysMenuPo), SysMenuDto.class);
    }

    @Override
    public List<SysMenuDto> saveUpdateBatch(List<SysMenuDto> list) {
        List<SysMenuPo> sysMenuPoList = UCopy.fullCopyList(list, SysMenuPo.class);
        return UCopy.fullCopyList(sysMenuRepository.saveAllAndFlush(sysMenuPoList), SysMenuDto.class);
    }

    @Override
    public Set<String> findMenuPermissionByUserId(Long id) {
        Set<String> sysMenuStrSet = sysMenuMapper.findMenuPermissionByUserId(id);
        Set<String> permsSet = UCollection.optimizeInitialCapacitySet(sysMenuStrSet.size());
        for (String sysMenuStr : sysMenuStrSet) {
            if (UEmpty.isNotEmpty(sysMenuStr)) {
                permsSet.addAll(UString.splitList(sysMenuStr.trim()));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysMenuDto> findMenuTreeByUserId(Long userId) {
        List<SysMenuPo> sysMenuPoList;
        // 管理员可以使用所有目录与菜单
        if (AdminConstant.ADMIN_ID == userId) {
            sysMenuPoList = sysMenuMapper.findAllDirAndMenu();
        } else {
            // 非管理员则查询可用的菜单权限
            sysMenuPoList = sysMenuMapper.findDirAndMenuByUserId(userId);
        }
        List<SysMenuDto> sysMenuDtoList = UCopy.fullCopyList(sysMenuPoList, SysMenuDto.class);
        return UTree.buildTree(sysMenuDtoList);
    }

    @Override
    public List<RouterDto> buildRouters(List<SysMenuDto> sysMenuDtoList) {
        List<RouterDto> routerDtoList = new LinkedList<>();
        for (SysMenuDto menu : sysMenuDtoList) {
            RouterDto routerDto = new RouterDto();
            routerDto.setHidden(FlagConstant.DISABLED.equals(menu.getVisible()));
            if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) || !MenuType.DIR.getType().equals(menu.getMenuType())) {
                routerDto.setComponent(getComponent(menu));
            }
            routerDto.setName(getRouteName(menu));
            routerDto.setPath(getRouterPath(menu));
            routerDto.setQuery(menu.getQueryParam());
            MetaDto meta = new MetaDto(menu.getMenuName(), menu.getIcon(),
                    UString.equals(FlagConstant.ENABLED, menu.getIsCache()), menu.getPath());
            routerDto.setMeta(meta);
            List<SysMenuDto> children = menu.getChildren();
            String component = checkComponent(menu.getComponent());
            if (UEmpty.isNotEmpty(children) && MenuType.DIR.getType().equals(menu.getMenuType())) {
                routerDto.setAlwaysShow(true);
                routerDto.setRedirect(NO_REDIRECT);
                routerDto.setChildren(buildRouters(children));
            } else if (isMenuFrame(menu)) {
                // 如果是菜单内部跳转
                routerDto.setMeta(null);
                List<RouterDto> childrenList = new ArrayList<>();
                RouterDto childrenRouterDto = new RouterDto();
                childrenRouterDto.setPath(menu.getPath());
                childrenRouterDto.setComponent(component);
                childrenRouterDto.setName(StringUtils.capitalize(menu.getPath()));
                childrenRouterDto.setMeta(meta);
                childrenRouterDto.setQuery(menu.getQueryParam());
                childrenList.add(childrenRouterDto);
                routerDto.setChildren(childrenList);
            } else if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && isDirInnerLink(menu)) {
                routerDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                routerDto.setPath(addForwardSlash(component));
                routerDto.setComponent(AdminConstant.BASE_LAYOUT);
                List<RouterDto> childrenList = new ArrayList<>();
                RouterDto childrenRouterDto = new RouterDto();
                childrenRouterDto.setPath(addForwardSlash(component));
                childrenRouterDto.setComponent(component);
                childrenRouterDto.setName(StringUtils.capitalize(innerLinkReplaceEach(menu.getPath())));
                childrenRouterDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(childrenRouterDto);
                routerDto.setChildren(childrenList);
            } else if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && isInnerLink(menu)) {
                routerDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon()));
                routerDto.setPath(PREFIX + innerLinkReplaceEach(menu.getPath()));
                routerDto.setComponent(AdminConstant.BASE_LAYOUT);
                List<RouterDto> childrenList = new ArrayList<>();
                RouterDto childrenRouterDto = new RouterDto();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                childrenRouterDto.setPath(routerPath);
                childrenRouterDto.setComponent(AdminConstant.INNER_LINK);
                childrenRouterDto.setName(StringUtils.capitalize(routerPath));
                childrenRouterDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(childrenRouterDto);
                routerDto.setChildren(childrenList);
            } else if (!AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && MenuType.DIR.getType().equals(menu.getMenuType())) {
                routerDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                routerDto.setPath(addForwardSlash(component));
                List<RouterDto> childrenList = new ArrayList<>();
                RouterDto childrenRouterDto = new RouterDto();
                childrenRouterDto.setPath(addForwardSlash(component));
                childrenRouterDto.setComponent(component);
                childrenRouterDto.setName(StringUtils.capitalize(innerLinkReplaceEach(menu.getPath())));
                childrenRouterDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(childrenRouterDto);
                routerDto.setChildren(childrenList);
            }
            routerDtoList.add(routerDto);
        }
        return routerDtoList;
    }

    @Override
    public List<RouterEntity> buildMenus(List<SysMenuDto> sysMenuDtoList) {
        List<RouterEntity> routerEntityList = new LinkedList<>();
        List<String> linkType = Arrays.asList(AdminConstant.MODAL, AdminConstant.BLANK);
        for (SysMenuDto menu : sysMenuDtoList) {
            RouterEntity routerEntity = new RouterEntity();
            String component = checkComponent(menu.getComponent());
            if (FlagConstant.ENABLED.equals(menu.getIsFrame()) && linkType.contains(component)) {
                routerEntity.setId(menu.getPath());
                routerEntity.setComponent(component);
            } else {
                routerEntity.setId(getRouterPath(menu));
            }
            routerEntity.setIcon(menu.getIcon());
            routerEntity.setTitle(menu.getMenuName());
            List<SysMenuDto> children = menu.getChildren();
            if (UEmpty.isNotEmpty(children) && MenuType.DIR.getType().equals(menu.getMenuType())) {
                routerEntity.setChildren(buildMenus(children));
            } else if (isMenuFrame(menu)) {
                // 如果是菜单内部跳转
                List<RouterEntity> childrenList = new ArrayList<>();
                RouterEntity childrenRouterEntity = new RouterEntity();
                childrenRouterEntity.setId(menu.getPath());
                childrenRouterEntity.setIcon(menu.getIcon());
                childrenRouterEntity.setTitle(menu.getMenuName());
                childrenList.add(childrenRouterEntity);
                routerEntity.setChildren(childrenList);
            } else if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && isDirInnerLink(menu)) {
                routerEntity.setIcon(menu.getIcon());
                routerEntity.setTitle(menu.getMenuName());
                routerEntity.setId(addForwardSlash(component));
            } else if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && isInnerLink(menu)) {
                routerEntity.setIcon(menu.getIcon());
                routerEntity.setTitle(menu.getMenuName());
                routerEntity.setId(PREFIX);
                List<RouterEntity> childrenList = new ArrayList<>();
                RouterEntity childrenRouterEntity = new RouterEntity();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                childrenRouterEntity.setId(routerPath);
                childrenRouterEntity.setIcon(menu.getIcon());
                childrenRouterEntity.setTitle(menu.getMenuName());
                childrenList.add(childrenRouterEntity);
                routerEntity.setChildren(childrenList);
            }
            routerEntityList.add(routerEntity);
        }
        return routerEntityList;
    }

    @Override
    public List<FindAllMenuTreeEntity> findAllMenuTree(Long userId) {
        QueryWrapper<SysMenuPo> wrapper = Wrappers.<SysMenuPo>query()
                .eq("M.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("M.STATUS", FlagConstant.ENABLED)
                .in("M.MENU_TYPE", MenuType.DIR.getType(), MenuType.MENU.getType())
                .orderByAsc("M.PARENT_ID")
                .orderByAsc("M.ORDER_NUM");
        List<SysMenuPo> sysMenuPoList = AdminConstant.ADMIN_ID == userId ? sysMenuMapper.findAllMenuTree(wrapper) :
                sysMenuMapper.findAllMenuTree(wrapper.eq(ObjectUtil.isNotNull(userId), "SUR.USER_ID", userId));
        List<FindAllMenuTreeEntity> findAllMenuTreeEntityList = UCopy.fullCopyList(sysMenuPoList, FindAllMenuTreeEntity.class);
        return UTree.buildTree(findAllMenuTreeEntityList);
    }

    @Override
    public List<FindAllMenuTreeEntity> findAllMenuTree(Long roleId, Long userId) {
        QueryWrapper<SysMenuPo> wrapper = Wrappers.<SysMenuPo>query()
                .eq("M.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("M.STATUS", FlagConstant.ENABLED)
                .eq(ObjectUtil.isNotNull(roleId), "SUR.ROLE_ID", roleId)
                .in("M.MENU_TYPE", MenuType.DIR.getType(), MenuType.MENU.getType())
                .orderByAsc("M.PARENT_ID")
                .orderByAsc("M.ORDER_NUM");
        List<SysMenuPo> sysMenuPoList = sysMenuMapper.findAllMenuTree(wrapper);
        List<FindAllMenuTreeEntity> findAllMenuTreeEntityList = UCopy.fullCopyList(sysMenuPoList, FindAllMenuTreeEntity.class);
        return UTree.buildTree(findAllMenuTreeEntityList);
    }

    @Override
    public List<Long> findSelectedMenuListByRoleId(Long roleId) {
        SysRolePo sysRolePo = sysRoleMapper.selectById(roleId);
        // 20240913 由于角色管理-分配菜单权限 保存时包含基本数据与菜单数据，如果只保存基本数据会导致菜单数据中的按钮数据部分被删除
        if (AdminConstant.ADMIN.equals(sysRolePo.getRoleKey())) {
            return sysMenuMapper.findAdminMenuList();
        }
        // 20240913 由于角色管理-分配菜单权限 保存时包含基本数据与菜单数据，如果只保存基本数据会导致菜单数据中的按钮数据部分被删除
        return sysMenuMapper.findSelectedMenuListByRoleId(roleId);
    }

    @Override
    public List<FindMenuListByUserIdEntity> findMenuListByUserId(SysMenuDto sysMenuDto, Long userId) {
        List<FindMenuListByUserIdEntity> findMenuListByRoleIdEntityList;
        if (USecurity.isAdmin(userId)) {
            findMenuListByRoleIdEntityList = sysMenuMapper.findMenuListByUserId(Wrappers.<SysMenuPo>query()
                    .like(ObjectUtil.isNotNull(sysMenuDto.getMenuName()), "M.MENU_NAME", sysMenuDto.getMenuName())
                    .eq(ObjectUtil.isNotNull(sysMenuDto.getMenuType()), "M.MENU_TYPE", sysMenuDto.getMenuType())
                    .orderByAsc("M.PARENT_ID")
                    .orderByAsc("M.ORDER_NUM"));
        } else {
            findMenuListByRoleIdEntityList = sysMenuMapper.findMenuListByUserId(Wrappers.<SysMenuPo>query()
                    .eq(ObjectUtil.isNotNull(userId), "SUR.USER_ID", userId)
                    .eq(UEmpty.isNotEmpty(sysMenuDto.getVisible()), "M.VISIBLE", sysMenuDto.getVisible())
                    .eq(UEmpty.isNotEmpty(sysMenuDto.getStatus()), "M.STATUS", sysMenuDto.getStatus())
                    .like(ObjectUtil.isNotNull(sysMenuDto.getMenuName()), "M.MENU_NAME", sysMenuDto.getMenuName())
                    .eq(ObjectUtil.isNotNull(sysMenuDto.getMenuType()), "M.MENU_TYPE", sysMenuDto.getMenuType())
                    .orderByAsc("M.PARENT_ID")
                    .orderByAsc("M.ORDER_NUM"));
        }
        return UTree.buildTree(findMenuListByRoleIdEntityList);
    }

    @Override
    public List<FindTreeMenuSelectEntity> findTreeMenuSelect(Long userId, String menuType) {
        QueryWrapper<SysMenuPo> wrapper = Wrappers.<SysMenuPo>query()
                .eq("M.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("M.STATUS", FlagConstant.ENABLED)
                .eq("M.IS_FRAME", FlagConstant.DISABLED)
                .orderByAsc("M.PARENT_ID")
                .orderByAsc("M.ORDER_NUM");
        if (MenuType.MENU.getType().equals(menuType)) {
            wrapper.in("M.MENU_TYPE", MenuType.DIR.getType());
        } else if (MenuType.BUTTON.getType().equals(menuType)) {
            wrapper.in("M.MENU_TYPE", MenuType.DIR.getType(), MenuType.MENU.getType());
        }
        List<FindTreeMenuSelectEntity> findAllMenuTreeEntityList = AdminConstant.ADMIN_ID == userId ? sysMenuMapper.findTreeMenuSelect(wrapper) :
                sysMenuMapper.findTreeMenuSelect(wrapper.eq(ObjectUtil.isNotNull(userId), "SUR.USER_ID", userId));
        return UTree.buildTree(findAllMenuTreeEntityList);
    }

    @Override
    public SysMenuDto findMenuByParentId(Long parentId) {
        LambdaQueryWrapper<SysMenuPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysMenuPo::getId, parentId)
                .eq(SysMenuPo::getMenuType, MenuType.MENU.getType());
        SysMenuPo sysMenuPo = Optional.ofNullable(this.getOne(wrapper)).orElseGet(SysMenuPo::new);
        return UCopy.copyPo2Dto(sysMenuPo, SysMenuDto.class);
    }

    @Override
    @LogRecord(module = MenuModule.MENU_MANAGEMENT, subModule = MenuModule.SubModule.SAVE_MENU, message = "menu.save")
    public SysMenuDto saveMenu(SysMenuDto sysMenuDto) {
        //前端解析menu列表ID的逻辑不要处理后半部分
        if (UEmpty.isEmpty(sysMenuDto.getId())) {
            String component = checkComponent(sysMenuDto.getComponent());
            if (UEmpty.isNotEmpty(component)) {
                boolean componentExistsFlag = sysMenuMapper.findByComponentExists(component);
                if (componentExistsFlag) {
                    // 如果组件路径存在
                    throw new ServiceException(MenuModule.MENU_MANAGEMENT, "menu.component.exists", component);
                }
            }
        }
        if (MenuType.MENU.getType().equals(sysMenuDto.getMenuType())) {
            checkAddMenu(sysMenuDto);
        } else if (MenuType.BUTTON.getType().equals(sysMenuDto.getMenuType())) {
            checkAddButton(sysMenuDto);
            sysMenuDto.setPerms(sysMenuDto.getPerms());
        }
        if (MenuType.LINK.getType().equals(sysMenuDto.getMenuType())) {
            checkAddLink(sysMenuDto);
            boolean flag = Optional.ofNullable(sysMenuDto.getComponentType()).map(AdminConstant.INNER::equals).isPresent();
            if (flag && UEmpty.isEmpty(sysMenuDto.getComponent())) {
                sysMenuDto.setComponent(AdminConstant.INNER_COMPONENT);
            }
        }
        return saveUpdate(sysMenuDto);
    }

    @Override
    @LogRecord(module = MenuModule.MENU_MANAGEMENT, subModule = MenuModule.SubModule.DELETE_MENU, message = "menu.delete")
    public void deleteMenu(Long id, Long userId) {
        QueryWrapper<SysMenuPo> wrapper = Wrappers.<SysMenuPo>query()
                .orderByAsc("M.ID");
        List<SysMenuPo> sysMenuPoList = AdminConstant.ADMIN_ID == userId ? sysMenuMapper.findAllMenuTree(wrapper) :
                sysMenuMapper.findAllMenuTree(wrapper.eq(ObjectUtil.isNotNull(userId), "SUR.USER_ID", userId));
        SysMenuPo sysMenuPo = binarySearch(id, sysMenuPoList);
        List<SysMenuPo> nodeAndChildren = bfs(sysMenuPoList, sysMenuPo);
        List<Long> idList = UStream.toList(nodeAndChildren, SysMenuPo::getId);
        sysMenuRepository.deleteRoleMenu(idList);
        sysMenuRepository.deleteAllById(idList);
    }

    @Override
    public List<SysMenuDto> findAllSysButton(SysMenuDto sysMenuDto) {
        LambdaQueryWrapper<SysMenuPo> queryWrapper = new LambdaQueryWrapper<SysMenuPo>()
                .eq(SysMenuPo::getLogicDel, FlagConstant.DISABLED)
                .eq(SysMenuPo::getMenuType, MenuType.BUTTON.getType())
                .eq(SysMenuPo::getParentId, sysMenuDto.getId())
                .likeRight(UEmpty.isNotEmpty(sysMenuDto.getMenuName()), SysMenuPo::getMenuName, sysMenuDto.getMenuName())
                .orderByAsc(SysMenuPo::getOrderNum);
        List<SysMenuPo> sysMenuPoList = sysMenuMapper.findAllSysButton(queryWrapper);
        return UCopy.fullCopyList(sysMenuPoList, SysMenuDto.class);
    }

    @Override
    public List<Long> findAssignedSysButtonByRoleId(SysMenuDto sysMenuDto, Long roleId) {
        Wrapper<SysMenuPo> queryWrapper = Wrappers.<SysMenuPo>query()
                .eq("M.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("M.MENU_TYPE", MenuType.BUTTON.getType())
                .eq("M.PARENT_ID", sysMenuDto.getId())
                .eq("SRM.ROLE_ID", roleId)
                .like(UEmpty.isNotEmpty(sysMenuDto.getMenuName()), "M.MENU_NAME", sysMenuDto.getMenuName())
                .orderByAsc("M.ORDER_NUM");
        return sysMenuMapper.findAssignedSysButtonByRoleId(queryWrapper);
    }

    @Override
    public void assignButton(AssignButtonDto assignButtonDto) {
        Long roleId = Long.parseLong(assignButtonDto.getRoleId());
        List<Long> beforeAssignButtonIdList = assignButtonDto.getBeforeAssignButtonIdList().stream().map(Long::parseLong).collect(Collectors.toList());
        Set<SysRoleMenuPo> beforeSysRoleMenuPoSet = UCollection.optimizeInitialCapacitySet(beforeAssignButtonIdList.size());
        for (Long beforeAssignButtonId : beforeAssignButtonIdList) {
            SysRoleMenuPo sysRoleMenuPo = new SysRoleMenuPo(new SysRoleMenuPk(beforeAssignButtonId, roleId));
            beforeSysRoleMenuPoSet.add(sysRoleMenuPo);
        }
        List<Long> assignButtonIdList = assignButtonDto.getAssignButtonIdList().stream().map(Long::parseLong).collect(Collectors.toList());
        Set<SysRoleMenuPo> afterSysRoleMenuPoSet = UCollection.optimizeInitialCapacitySet(assignButtonIdList.size());
        for (Long assignButtonId : assignButtonIdList) {
            SysRoleMenuPo sysRoleMenuPo = new SysRoleMenuPo(new SysRoleMenuPk(assignButtonId, roleId));
            afterSysRoleMenuPoSet.add(sysRoleMenuPo);
        }
        transactionTemplate.execute(status -> {
            SysSensitiveLogBean sysSensitiveLogBean = null;
            try {
                if (UEmpty.isNotEmpty(beforeSysRoleMenuPoSet)) {
                    sysRoleMenuRepository.deleteAllInBatch(beforeSysRoleMenuPoSet);
                }
                if (UEmpty.isNotEmpty(afterSysRoleMenuPoSet)) {
                    sysRoleMenuRepository.saveAll(afterSysRoleMenuPoSet);
                }
                sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                    SysSensitiveLogBean assignButtonLogBean = new SysSensitiveLogBean();
                    assignButtonLogBean.setModule(MenuModule.MENU_MANAGEMENT);
                    assignButtonLogBean.setSubModule(MenuModule.SubModule.ASSIGN_BUTTON);
                    assignButtonLogBean.setType(MenuModule.SubModule.ASSIGN_BUTTON);
                    assignButtonLogBean.setResult(FlagConstant.SUCCESS);
                    assignButtonLogBean.setContextOld("分配前菜单ID：" + JSONObject.toJSONString(beforeAssignButtonIdList));
                    assignButtonLogBean.setContext("分配后菜单ID：" + JSONObject.toJSONString(assignButtonIdList));
                    assignButtonLogBean.setRemark(UMessage.message("assigned_menu_permissions_success"));
                    return assignButtonLogBean;
                });
            } catch (Exception e) {
                sysSensitiveLogBean = USecurity.recordSensitiveLog(() -> {
                    SysSensitiveLogBean assignButtonLogBean = new SysSensitiveLogBean();
                    assignButtonLogBean.setModule(MenuModule.MENU_MANAGEMENT);
                    assignButtonLogBean.setSubModule(MenuModule.SubModule.ASSIGN_BUTTON);
                    assignButtonLogBean.setType(MenuModule.SubModule.ASSIGN_BUTTON);
                    assignButtonLogBean.setResult(FlagConstant.FAILED);
                    assignButtonLogBean.setContextOld("分配前菜单ID：" + JSONObject.toJSONString(beforeAssignButtonIdList));
                    assignButtonLogBean.setRemark(UMessage.message("assigned_menu_permissions_failed"));
                    return assignButtonLogBean;
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
    public Long findIncrementOrderNum(SysMenuDto sysMenuDto) {
        Long id = sysMenuDto.getId();
        Long maxOrderNum = sysMenuMapper.findMaxOrderNum(id);
        return maxOrderNum == null ? 10L : ((int) (maxOrderNum / 10)) * 10L + 10L;
    }

    /**
     * 广度优先算法
     * 获取该节点在列表中对应的子节点，并通过队列循环对子节点进行同样操作
     *
     * @param sysMenuPoList 所有可删除的菜单
     * @param sysMenuPo     选取的节点
     * @return 选取的节点及其所有子节点
     */
    private List<SysMenuPo> bfs(List<SysMenuPo> sysMenuPoList, SysMenuPo sysMenuPo) {
        List<SysMenuPo> nodeAndChildren = new ArrayList<>();
        Queue<SysMenuPo> queue = new LinkedList<>();
        queue.offer(sysMenuPo);
        while (!queue.isEmpty()) {
            SysMenuPo current = queue.poll();
            for (SysMenuPo menuPo : sysMenuPoList) {
                if (current.getId().equals(menuPo.getParentId())) {
                    queue.offer(menuPo);
                }
            }
            nodeAndChildren.add(current);
        }
        return nodeAndChildren;
    }

    /**
     * 二分查找
     * 根据菜单ID，在所有可删除的菜单中查找出菜单数据
     *
     * @param id            菜单ID
     * @param sysMenuPoList 所有可删除的菜单
     * @return 查找到菜单数据的下标
     */
    private SysMenuPo binarySearch(Long id, List<SysMenuPo> sysMenuPoList) {
        SysMenuPo tmp = new SysMenuPo();
        tmp.setId(id);
        int index = Collections.binarySearch(sysMenuPoList, tmp);
        if (index >= 0 && index < sysMenuPoList.size()) {
            tmp = sysMenuPoList.get(index);
        }
        return tmp;
    }

    /**
     * 【新建链接】数据检查
     *
     * @param sysMenuDto 前端表单数据
     */
    private void checkAddLink(SysMenuDto sysMenuDto) {
        sysMenuDto.setIsFrame(FlagConstant.ENABLED);
        if (sysMenuDto.getParentId() == -1) {
            sysMenuDto.setMenuType(MenuType.DIR.getType());
        } else {
            sysMenuDto.setMenuType(MenuType.MENU.getType());
        }
        // 判断是否为内部链接（在内容栏打开）
        String componentType = sysMenuDto.getComponentType();
        if (UEmpty.isNotEmpty(componentType)) {
            if (AdminConstant.INNER_LINK.equalsIgnoreCase(componentType) && !UString.isHttp(sysMenuDto.getPath())) {
                throw new ServiceException(MenuModule.MENU_MANAGEMENT, "menu.innerLink.path.require.http");
            }
        }
    }

    /**
     * 【新建按钮】数据检查
     *
     * @param sysMenuDto 前端表单数据
     */
    private void checkAddButton(SysMenuDto sysMenuDto) {
        SysMenuDto findMenuByParentIdDto = findMenuByParentId(sysMenuDto.getParentId());
        if (ObjectUtil.isNull(findMenuByParentIdDto.getId())) {
            throw new ServiceException(MenuModule.MENU_MANAGEMENT, "menu.button.find.parent.failed", sysMenuDto.getMenuName(), sysMenuDto.getParentId());
        }
    }

    /**
     * 【新建菜单】数据检查
     *
     * @param sysMenuDto 前端表单数据
     */
    private void checkAddMenu(SysMenuDto sysMenuDto) {
        // 防止在【新建目录】按钮中新建不合法的链接
        String componentType = sysMenuDto.getComponentType();
        if (UEmpty.isEmpty(componentType) && UString.isHttp(sysMenuDto.getPath())) {
            throw new ServiceException(MenuModule.MENU_MANAGEMENT, "menu.add.link.failed");
        }
        SysMenuPo sysMenuPo = UCopy.copyDto2Po(sysMenuDto, SysMenuPo.class);
        boolean flag = sysMenuMapper.findMenuPathExist(sysMenuPo);
        if (flag) {
            throw new ServiceException(MenuModule.MENU_MANAGEMENT, "menu.path.existed", sysMenuPo.getPath());
        }
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    private String getRouterPath(SysMenuDto menu) {
        String routerPath = menu.getPath();
        if (!AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId())) {
            // 如果是子菜单
            String component = menu.getComponent();
            if (UEmpty.isNotEmpty(component)) {
                routerPath = addForwardSlash(component);
            }
            // 内链打开外网方式
            if (isInnerLink(menu)) {
                routerPath = innerLinkReplaceEach(routerPath);
            }
        }
        // 非外链并且是顶级菜单（MenuType为DIR）
        if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId())
                && MenuType.DIR.getType().equals(menu.getMenuType())
                && FlagConstant.DISABLED.equals(menu.getIsFrame())) {
            routerPath = PREFIX + menu.getPath();
        } else if (isMenuFrame(menu)) {
            // 非外链并且MenuType为MENU
            routerPath = PREFIX;
        }
        return routerPath;
    }

    /**
     * 内链域名特殊字符替换
     */
    private String innerLinkReplaceEach(String routerPath) {
        return StringUtils.replaceEach(routerPath, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[]{"", "", "", PREFIX});
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isInnerLink(SysMenuDto menu) {
        return FlagConstant.ENABLED.equals(menu.getIsFrame()) && UString.isHttp(menu.getPath());
    }

    /**
     * 是否为顶级目录的内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isDirInnerLink(SysMenuDto menu) {
        return FlagConstant.ENABLED.equals(menu.getIsFrame())
                && MenuType.DIR.getType().equals(menu.getMenuType())
                && UString.isHttp(menu.getPath());
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    private String getRouteName(SysMenuDto menu) {
        String path = menu.getPath();
        if (UString.isHttp(path)) {
            return path;
        }
        String routerName = StringUtils.capitalize(path);
        // 非外链且是顶级菜单
        if (isMenuFrame(menu)) {
            routerName = UString.EMPTY;
        }
        return routerName;
    }

    /**
     * 判断是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return flag
     */
    private boolean isMenuFrame(SysMenuDto menu) {
        // 菜单信息：是顶级菜单的子菜单--是
        return AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId())
                && MenuType.MENU.getType().equals(menu.getMenuType())
                && menu.getIsFrame().equals(FlagConstant.ENABLED);
    }

    /**
     * 根据菜单数据，为router装配vue组件
     *
     * @param menu 菜单数据
     * @return vue组件名称
     */
    private String getComponent(SysMenuDto menu) {
        String component = AdminConstant.BASE_LAYOUT;
        String menuComponent = menu.getComponent();
        if (StringUtils.isNotEmpty(menuComponent) && !isMenuFrame(menu)) {
            component = menuComponent;
        } else if (StringUtils.isEmpty(menuComponent)
                && menu.getParentId().intValue() != AdminConstant.MENU_TOP_PARENT_ID
                && isInnerLink(menu)) {
            component = AdminConstant.INNER_LINK;
        } else if (StringUtils.isEmpty(menuComponent) && isBlankView(menu)) {
            component = AdminConstant.BLANK_LAYOUT;
        }
        return component;
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isBlankView(SysMenuDto menu) {
        return menu.getParentId().intValue() != AdminConstant.MENU_TOP_PARENT_ID
                && MenuType.MENU.getType().equals(menu.getMenuType());
    }

    /**
     * 判断是否为'/'开头，是则去掉
     *
     * @param component 组件路径
     * @return 检查后的组件路径
     */
    private String checkComponent(String component) {
        if (UEmpty.isNotEmpty(component)) {
            if (component.startsWith(PREFIX)) {
                return component.substring(1);
            }
        }
        return component;
    }

    private String addForwardSlash(String str) {
        if (UEmpty.isNotEmpty(str) && !str.startsWith(PREFIX)) {
            return PREFIX + str;
        }
        return str;
    }
}
