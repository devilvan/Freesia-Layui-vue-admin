import {defineStore} from 'pinia'
import {getInfo, getMenu, getRouters, logout} from "../api/Login";
import BaseLayout from "../layouts/BaseLayout.vue";
import BlankLayout from "../layouts/BlankLayout.vue";
import Inner from "../views/iframe/inner/index.vue"
import auth from "../directives/auth";
import {dynamicRoutes} from "../router/module/base-routes";
import router, {addRoutes} from "../router";
import {RouterComponent} from "../types/Menu";
import {reloadSysTenant} from "../api/system/Tenant";
import {useTabStore} from "../layouts/composable/useTabStore";
import {loginPath} from "../api/Http";
import {findUnreadCount} from "@/api/system/Notice";
import {SysNoticeVo} from "@/types/system/Notice";

// 匹配views里面所有的.vue文件
const modules = import.meta.glob('./../views/**/*.vue')
export const useUserStore = defineStore({
    id: 'user',
    state: () => {
        return {
            token: '',
            userInfo: {
                id: '',
                deptId: '',
                userName: '',
                nickName: '',
                userType: '',
                email: '',
                telNo: '',
                gender: '',
                avatar: '',
            },
            permissions: [],
            menus: [],
            roles: [],
            sidebarRoutes: [{}],
            sysTenantDtoList: [{}],
            noticeCount: 0,
            announcementCount: 0,
            unreadCount: 0
        }
    },
    actions: {
        async getInfo() {
            const {data, code} = await getInfo();
            if (code == 200) {
                this.userInfo = data.user;
                this.permissions = data.permissions;
                this.roles = data.roles;
                this.sysTenantDtoList = data.sysTenantDtoList
            }
        },
        async getRouters() {
            const {data, code} = await getRouters()
            if (!data || !data.data || data.data.length === 0) {
                // router.push(loginPath).then(r => r)
            }
            if (code === 200) {
                this.sidebarRoutes = filterAsyncRouter(data)
                let dynamic = filterDynamicRoutes(dynamicRoutes)
                dynamic.forEach((f: any) => {
                    this.sidebarRoutes.push(f);
                })
                addRoutes(this.sidebarRoutes, router);
            }
        },
        async getMenu() {
            const {data, code} = await getMenu();
            if (code == 200) {
                this.menus = data;
            }
        },
        async logout() {
            await logout();
            this.token = ''
            this.roles = []
            this.permissions = []
            this.menus = []
            this.userInfo = {
                id: '',
                deptId: '',
                userName: '',
                nickName: '',
                userType: '',
                email: '',
                telNo: '',
                gender: '',
                avatar: '',
            }
            this.sysTenantDtoList = [{}]
            useTabStore().tabs = []
            useTabStore().tabsCache = []
            useTabStore().currentPath = ''
            await router.replace(loginPath)
        },
        async reloadSysTenant() {
            const {data, code} = await reloadSysTenant()
            if (code === 200) {
                this.sysTenantDtoList = data
            }
        },
        async calculateSumCount() {
            this.unreadCount = 0;
            if (this.announcementCount) {
                this.unreadCount += this.announcementCount
            }
            if (this.noticeCount) {
                this.unreadCount += this.noticeCount
            }
        }
    },
    persist: {
        storage: localStorage,
        paths: ['token', 'userInfo', 'permissions', 'roles', 'sysTenantDtoList'],
    }
})


function filterAsyncRouter(asyncRouterMap: any, lastRouter = false, type = false) {
    return asyncRouterMap.filter((route: any) => {
        if (type && route.children) {
            route.children = filterChildren(route.children)
        }
        if (route.component) {
            // BaseLayout 组件特殊处理
            if (route.component === RouterComponent.BASE_LAYOUT) {
                route.component = BaseLayout
            } else if (route.component === RouterComponent.BLANK_LAYOUT) {
                route.component = BlankLayout
            } else if (route.component === RouterComponent.INNER_LINK) {
                route.component = Inner
            } else {
                route.component = loadView(route.component)
            }
        }
        if (route.children != null && route.children && route.children.length) {
            route.children = filterAsyncRouter(route.children, route, type)
        } else {
            delete route['children']
            delete route['redirect']
        }
        return true
    })
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes: any[]) {
    const res: any[] = []
    routes.forEach(route => {
        if (route.permissions) {
            if (auth.hasPermiOr(route.permissions)) {
                res.push(route)
            }
        } else if (route.roles) {
            if (auth.hasRoleOr(route.roles)) {
                res.push(route)
            }
        }
    })
    return res
}

function filterChildren(childrenMap: any, lastRouter = false) {
    var children: any[] = []
    childrenMap.forEach((el: { children: any[]; component: string; path: string; }, index: any) => {
        if (el.children && el.children.length) {
            if (el.component === RouterComponent.BLANK_LAYOUT && !lastRouter) {
                el.children.forEach(c => {
                    c.path = el.path + '/' + c.path
                    if (c.children && c.children.length) {
                        children = children.concat(filterChildren(c.children, c))
                        return
                    }
                    children.push(c)
                })
                return
            }
        }
        if (lastRouter) {
            el.path = '/' + el.path
        }
        children = children.concat(el)
    })
    return children
}

const loadView = (view: any) => {
    let res;
    for (const path in modules) {
        const dir = path.split('views/')[1].split('.vue')[0];
        if (dir === view) {
            res = () => modules[path]();
        }
    }
    return res;
}
