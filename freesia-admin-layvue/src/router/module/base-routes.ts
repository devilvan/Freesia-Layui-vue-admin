import Login from '../../views/login/index.vue'
import BaseLayout from "../../layouts/BaseLayout.vue";
import ErrorCode from "../../views/error/ErrorCode.vue";

// const modules = import.meta.glob('./../../views/**/*.vue')

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
        path: '/workSpace',
        redirect: '/workSpace/workbench/index',
        component: BaseLayout,
        meta: {title: '工作空间'},
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
    },
    {
        path: '/error/:code',
        component: ErrorCode,
        meta: {title: '错误页面'},
    },
    {
        path: "/:pathMatch(.*)*",
        component: () => import('@/views/error/404.vue'),
        hidden: true
    },

]

export const dynamicRoutes = [
    {
        path: '/system/user',
        component: BaseLayout,
        hidden: true,
        permissions: ['system:user:assignRole'],
        children: [
            {
                path: 'assignRole/:userId(\\d+)',
                component: () => import('@/views/system/user/assignRole.vue'),
                name: 'AssignRole',
                meta: {title: '分配角色', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/system/role',
        component: BaseLayout,
        hidden: true,
        permissions: ['system:role:assignUser'],
        children: [
            {
                path: 'assignUser/:roleId(\\d+)',
                component: () => import('@/views/system/role/assignUser.vue'),
                name: 'AssignUser',
                meta: {title: '分配用户', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/system/tenant',
        component: BaseLayout,
        hidden: true,
        permissions: ['system:tenant:assignUser'],
        children: [
            {
                path: 'assignUser/:tenantId(\\d+)',
                component: () => import('@/views/system/tenant/assignUser.vue'),
                name: 'TenantAssignUser',
                meta: {title: '分配用户', affix: false, cache: false, closable: true}
            }
        ]
    },
]
