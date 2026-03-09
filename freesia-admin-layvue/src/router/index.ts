import {createRouter, createWebHistory, NavigationGuardNext, RouteLocationNormalized, RouteRecordRaw} from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {constantRoutes} from "./module/base-routes";
import {useUserStore} from "../store/user";
import {RouterComponent} from "../types/Menu";
import {isMobile, loginPath, mobileLoginPath} from "../api/Http";

NProgress.configure({showSpinner: false})


/* 构建路由*/
export const router = createRouter({
    history: createWebHistory('/'),
    routes: constantRoutes as RouteRecordRaw[],
});

/* 构建路由*/

/**
 * Router 前置拦截
 *
 * 1.验证 token 存在, 并且有效, 否则 -> login.vue
 * 2.验证 permission 存在, 否则 -> 403.vue
 * 3.验证 router 是否存在, 否则 -> 404.vue
 *
 * @param to 目标
 * @param from 来至
 */
let isGetRouter: boolean = false
router.beforeEach(async (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
    const userStore = useUserStore();
    NProgress.start();
    let token = userStore.token;

    // 处理登录页面访问
    if (to.path === loginPath || to.path === mobileLoginPath) {
        if (!token || token === '') {
            // 如果token不存在，直接进入登录页
            userStore.token = ''
            next()
        } else {
            // 如果token存在（已登录），则跳转到默认页
            next({path: '/'})
        }
    }
    // 处理已登录状态
    else if (token) {
        if (!isGetRouter) {
            isGetRouter = true;
            await userStore.getRouters()
            next(to.fullPath)
        } else {
            next()
        }
    }
    // 处理未登录状态
    else {
        if (isMobile()) {
            next({path: mobileLoginPath})
        } else {
            next({path: loginPath})
        }
    }
})

router.afterEach(() => {
    NProgress.done();
})

export const addRoutes = (routes: any, router: any) => {
    if (routes && routes.length > 0) {
        routes.forEach((route: any) => {
            router.addRoute(route.name || RouterComponent.BASE_LAYOUT, route);
        })
    }
}

export default router
