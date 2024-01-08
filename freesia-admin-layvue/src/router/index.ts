import {createRouter, createWebHistory, NavigationGuardNext, RouteLocationNormalized, RouteRecordRaw} from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {constantRoutes} from "./module/base-routes";
import {useUserStore} from "../store/user";
import {RouterComponent} from "../types/Menu";

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
    if (to.path === '/login') {
        if (!token) {
            userStore.token = ''
            next()
        } else {
            next()
        }
    }
    if (token) {
        if (!isGetRouter) {
            isGetRouter = true;
            await userStore.getRouters()
            next(to.fullPath)
        } else {
            next()
        }
    } else {
        next({path: '/login'})
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
