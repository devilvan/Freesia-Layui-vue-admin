import Login from '../../views/login/index.vue'
import BaseLayout from "../../layouts/BaseLayout.vue";

const modules = import.meta.glob('./../../views/**/*.vue')

export const constantRoutes = [
    {
        path: '/',
        redirect: '/workSpace'
    },
    {
        path: '/login',
        component: Login,
        meta: {title: '登录页面'},
    },
    {
        path: "/:pathMatch(.*)*",
        component: () => import('@/views/error/404.vue'),
        hidden: true
    },
    {
        path: '/workSpace',
        redirect: '/workSpace/workbench',
        component: BaseLayout,
        meta: {title: '工作空间'},
        children: [
            {
                name: 'Workbench',
                path: '/workSpace/workbench',
                component: () => import('@/views/workSpace/workbench/index.vue'),
                // closable默认为false，true为不可关闭状态
                meta: {title: '工作台', requireAuth: true, affix: true, closable: true},
            },
            {
                path: '/workSpace/console',
                component: () => import('@/views/workSpace/console/index.vue'),
                meta: {title: '控制台', requireAuth: true},
            },
            {
                path: '/workSpace/analysis',
                component: () => import('@/views/workSpace/analysis/index.vue'),
                meta: {title: '分析页', requireAuth: true},
            },
            {
                path: '/workSpace/monitor',
                component: () => import('@/views/workSpace/monitor/index.vue'),
                meta: {title: '监控页', requireAuth: true},
            }
        ]
    },
    {
        path: '/error',
        component: BaseLayout,
        meta: {title: '错误页面'},
        children: [
            {
                path: '/error/401',
                component: () => import('@/views/error/401.vue'),
                meta: {title: '401'},
            },
            {
                path: '/error/403',
                component: () => import('@/views/error/403.vue'),
                meta: {title: '403'},
            },
            {
                path: '/error/404',
                component: () => import('@/views/error/404.vue'),
                meta: {title: '404'},
            },
            {
                path: '/error/500',
                component: () => import('@/views/error/500.vue'),
                meta: {title: '500'},
            }
        ]
    }

]

export const dynamicRoutes = []
