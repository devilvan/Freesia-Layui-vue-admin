import {createRouter, createWebHistory, NavigationGuardNext, RouteLocationNormalized, RouteRecordRaw} from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {constantRoutes} from "./module/base-routes";
import {useUserStore} from "../store/user";
import {RouterComponent} from "../types/Menu";
import {loginPath, isTokenExpired} from "../api/Http";
import {layer} from '@layui/layui-vue';
import {useTabStore} from "../layouts/composable/useTabStore";

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
// OAuth 回调、错误页面等无需登录即可访问的路径
const whiteList = ['/oauth/callback', '/error', '/404']

router.beforeEach(async (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
    const userStore = useUserStore();
    NProgress.start();
    let token = userStore.token;

    // 从 URL query 中检测 OAuth 回调 token（防御层：无论重定向到哪个页面，只要 URL 带有 token 就自动处理）
    const urlToken = to.query.token as string
    if (urlToken && !token) {
        console.log('[RouterGuard] 从 URL 检测到 OAuth token:', urlToken.substring(0, 20) + '...')
        // 显式持久化（不依赖 Pinia persist 插件）
        localStorage.setItem('token', urlToken)
        try {
            document.cookie = `token=${encodeURIComponent(urlToken)}; path=/; max-age=86400; SameSite=Lax`
        } catch (_) { /* 非关键 */ }
        // 设置到 store
        userStore.token = urlToken
        token = urlToken

        // 异步加载用户信息（不阻塞当前导航，确保第三方登录用户信息完整）
        userStore.getInfo().then(() => {
            console.log('[RouterGuard] getInfo() 完成, permissions:', userStore.permissions?.length || 0)
        }).catch((e: any) => {
            console.error('[RouterGuard] getInfo() 失败:', e)
        })
    }

    // 白名单路径：无需 token 验证，直接放行
    if (whiteList.some(path => to.path.startsWith(path))) {
        next()
        return
    }

    if (to.path === loginPath) {
        if (!token || token === '') {
            // 如果token不存在，直接跳转到登录页；会话已失效，重新登录后需重新加载路由
            isGetRouter = false
            userStore.token = ''
            next()
        } else if ((token || token !== '') && to.path === loginPath) {
            // 如果token存在（已登录），则跳转到默认页
            next({path: '/'})
        } else {
            next()
        }
        return
    }
    if (token) {
        // 检测到上一次登录的 token 已过期：不初始化任何请求，清空状态并跳登录页，只提示一次
        if (isTokenExpired(token)) {
            isGetRouter = false
            userStore.$reset()
            layer.msg('登录已过期，请重新登录', { icon: 2 })
            next({path: loginPath, replace: true})
            return
        }
        if (!isGetRouter) {
            isGetRouter = true;
            await userStore.getRouters()
            next(to.fullPath)
        } else {
            next()
        }
    } else {
        next({path: loginPath})
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
