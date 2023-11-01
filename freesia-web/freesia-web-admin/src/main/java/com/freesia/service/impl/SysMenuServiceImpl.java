package com.freesia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.*;
import com.freesia.dto.MetaDto;
import com.freesia.dto.RouterDto;
import com.freesia.dto.SysMenuDto;
import com.freesia.entity.FindAllMenuTreeEntity;
import com.freesia.entity.FindMenuListByUserIdEntity;
import com.freesia.entity.FindTreeMenuSelectEntity;
import com.freesia.entity.RouterEntity;
import com.freesia.exception.ServiceException;
import com.freesia.mapper.SysMenuMapper;
import com.freesia.mapper.SysRoleMapper;
import com.freesia.model.LoginUserModel;
import com.freesia.po.SysMenuPo;
import com.freesia.po.SysRolePo;
import com.freesia.repository.SysMenuRepository;
import com.freesia.service.SysMenuService;
import com.freesia.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 业务逻辑类
 * @date 2023-08-17
 */
@Valid
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuPo> implements SysMenuService {
    private static final String NO_REDIRECT = "noRedirect";

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuRepository sysMenuRepository;

    @Override
    public SysMenuDto saveUpdate(SysMenuDto sysMenuDto) {
        SysMenuPo sysMenuPo = UCopy.copyDto2Po(sysMenuDto, SysMenuPo.class);
        return UCopy.copyPo2Dto(sysMenuRepository.saveAndFlush(sysMenuPo), SysMenuDto.class);
    }

    @Override
    public List<SysMenuDto> saveUpdateBatch(List<SysMenuDto> list) {
        List<SysMenuPo> sysMenuPoList = UCopy.fullCopyCollections(list, SysMenuPo.class);
        return UCopy.fullCopyCollections(sysMenuRepository.saveAllAndFlush(sysMenuPoList), SysMenuDto.class);
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
        List<SysMenuDto> sysMenuDtoList = UCopy.fullCopyCollections(sysMenuPoList, SysMenuDto.class);
        return UTree.buildTree(sysMenuDtoList);
    }

    @Override
    public List<RouterDto> buildRouters(List<SysMenuDto> sysMenuDtoList) {
        List<RouterDto> routerDtoList = new LinkedList<>();
        for (SysMenuDto menu : sysMenuDtoList) {
            RouterDto routerDto = new RouterDto();
            routerDto.setHidden(FlagConstant.DISABLED.equals(menu.getVisible()));
            routerDto.setComponent(getComponent(menu));
            routerDto.setName(getRouteName(menu));
            routerDto.setPath(getRouterPath(menu));
            routerDto.setQuery(menu.getQueryParam());
            MetaDto meta = new MetaDto(menu.getMenuName(), menu.getIcon(),
                    UString.equals(FlagConstant.ENABLED, menu.getIsCache()), menu.getPath());
            routerDto.setMeta(meta);
            List<SysMenuDto> children = menu.getChildren();
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
                childrenRouterDto.setComponent(menu.getComponent());
                childrenRouterDto.setName(StringUtils.capitalize(menu.getPath()));
                childrenRouterDto.setMeta(meta);
                childrenRouterDto.setQuery(menu.getQueryParam());
                childrenList.add(childrenRouterDto);
                routerDto.setChildren(childrenList);
            } else if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && isInnerLink(menu)) {
                routerDto.setMeta(new MetaDto(menu.getMenuName(), menu.getIcon()));
                routerDto.setPath("/");
                List<RouterDto> childrenList = new ArrayList<>();
                RouterDto childrenRouterDto = new RouterDto();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                childrenRouterDto.setPath(routerPath);
                childrenRouterDto.setComponent(AdminConstant.INNER_LINK);
                childrenRouterDto.setName(StringUtils.capitalize(routerPath));
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
            if (FlagConstant.ENABLED.equals(menu.getIsFrame()) && linkType.contains(menu.getComponent())) {
                routerEntity.setId(menu.getPath());
                routerEntity.setComponent(menu.getComponent());
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
            } else if (AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId()) && isInnerLink(menu)) {
                routerEntity.setIcon(menu.getIcon());
                routerEntity.setTitle(menu.getMenuName());
                routerEntity.setId("/");
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
        List<SysMenuPo> sysMenuPoList;
        QueryWrapper<SysMenuPo> wrapper = Wrappers.<SysMenuPo>query()
                .eq("M.LOGIC_DEL", FlagConstant.ENABLED)
                .eq("M.STATUS", FlagConstant.ENABLED)
                .orderByAsc("M.PARENT_ID")
                .orderByAsc("M.ORDER_NUM");
        sysMenuPoList = AdminConstant.ADMIN_ID == userId ? sysMenuMapper.findAllMenuTree(wrapper) :
                sysMenuMapper.findAllMenuTree(wrapper.eq(ObjectUtil.isNotNull(userId), "SUR.USER_ID", userId));
        List<FindAllMenuTreeEntity> findAllMenuTreeEntityList = UCopy.fullCopyCollections(sysMenuPoList, FindAllMenuTreeEntity.class);
        return UTree.buildTree(findAllMenuTreeEntityList);
    }

    @Override
    public List<Long> findSelectedMenuListByRoleId(Long roleId) {
        SysRolePo sysRolePo = sysRoleMapper.selectById(roleId);
        if (AdminConstant.ADMIN.equals(sysRolePo.getRoleKey())) {
            return sysMenuMapper.findAdminMenuList();
        }
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
                .eq("M.LOGIC_DEL", FlagConstant.ENABLED)
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
        wrapper.eq(SysMenuPo::getLogicDel, FlagConstant.ENABLED)
                .eq(SysMenuPo::getId, parentId)
                .eq(SysMenuPo::getMenuType, MenuType.MENU.getType());
        SysMenuPo sysMenuPo = Optional.ofNullable(this.getOne(wrapper)).orElseGet(SysMenuPo::new);
        return UCopy.copyPo2Dto(sysMenuPo, SysMenuDto.class);
    }

    @Override
    public SysMenuDto saveMenu(SysMenuDto sysMenuDto) {
        if (MenuType.MENU.getType().equals(sysMenuDto.getMenuType())) {
            checkAddMenu(sysMenuDto);
        } else if (MenuType.BUTTON.getType().equals(sysMenuDto.getMenuType())) {
            checkAddButton(sysMenuDto);
        }
        if (MenuType.LINK.getType().equals(sysMenuDto.getMenuType())) {
            checkAddLink(sysMenuDto);
        }
        return saveUpdate(sysMenuDto);
    }

    @Override
    public void deleteMenu(Long id) {
        LoginUserModel loginUser = USecurity.getLoginUser();
        if (ObjectUtil.isNull(loginUser)) {
            throw new ServiceException(SysModule.MENU_MANAGEMENT, "user.info.null");
        }
        Long userId = loginUser.getUserId();
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
                throw new ServiceException(SysModule.MENU_MANAGEMENT, "menu.innerLink.path.require.http");
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
            throw new ServiceException(SysModule.MENU_MANAGEMENT, "menu.button.find.parent.failed", sysMenuDto.getMenuName(), sysMenuDto.getParentId());
        } else {
            String component = findMenuByParentIdDto.getComponent();
            String path = sysMenuDto.getPath();
            String buttonPath = component.substring(0, component.lastIndexOf("/") + 1) + path;
            String buttonPerms = buttonPath.replaceAll("/", ":");
            sysMenuDto.setPerms(buttonPerms);
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
            throw new ServiceException(SysModule.MENU_MANAGEMENT, "menu.add.link.failed");
        }
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    private String getRouterPath(SysMenuDto menu) {
//        List<String> linkType = Arrays.asList(AdminConstant.MODAL, AdminConstant.BLANK);
        String routerPath = menu.getPath();
        if (!AdminConstant.MENU_TOP_PARENT_ID.equals(menu.getParentId())) {
            // 如果是子菜单
            String component = menu.getComponent();
            if (UEmpty.isNotEmpty(component)) {
                routerPath = "/" + component.replace("/index", "");
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
            routerPath = "/" + menu.getPath();
        } else if (isMenuFrame(menu)) {
            // 非外链并且MenuType为MENU
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 内链域名特殊字符替换
     */
    private String innerLinkReplaceEach(String routerPath) {
        return StringUtils.replaceEach(routerPath, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[]{"", "", "", "/"});
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isInnerLink(SysMenuDto menu) {
        return FlagConstant.DISABLED.equals(menu.getIsFrame()) && UString.isHttp(menu.getPath());
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
               && menu.getIsFrame().equals(FlagConstant.DISABLED);
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
}
