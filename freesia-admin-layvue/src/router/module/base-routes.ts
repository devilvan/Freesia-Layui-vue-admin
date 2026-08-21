import Login from '@/views/login/index.vue'
import BaseLayout from "@/layouts/BaseLayout.vue";
import ErrorCode from "@/views/error/ErrorCode.vue";
import {MenuPermission} from "@/types/Permission";

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
        redirect: '/workSpace/worldclock/index',
        component: BaseLayout,
        meta: {title: '世界时钟'},
    },
    {
        path: '/oauth/callback/:provider',
        component: () => import('@/views/login/oauthCallback.vue'),
        meta: {title: 'OAuth 登录回调'},
    },
    {
        path: '/chat/deepseek',
        component: BaseLayout,
        meta: {title: 'DeepSeek AI 对话'},
        children: [
            {
                path: 'index',
                component: () => import('@/views/chat/deepseek/index.vue'),
                name: 'DeepseekChat',
                meta: {title: 'DeepSeek AI 对话', affix: false, cache: false, closable: true}
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
        permissions: [MenuPermission.SYSTEM_USER_ASSIGN_ROLE],
        children: [
            {
                path: 'assignRole/:userId(\\d+)',
                component: () => import('@/views/system/user/assignRole.vue'),
                name: 'UserAssignRole',
                meta: {title: '分配角色', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/system/role',
        component: BaseLayout,
        hidden: true,
        permissions: [MenuPermission.SYSTEM_ROLE_ASSIGN_USER_EDIT],
        children: [
            {
                path: 'assignUser/:roleId(\\d+)',
                component: () => import('@/views/system/role/assignUser.vue'),
                name: 'RoleAssignUser',
                meta: {title: '分配用户', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/system/role',
        component: BaseLayout,
        hidden: true,
        permissions: [MenuPermission.SYSTEM_ROLE_ASSIGN_BUTTON_EDIT],
        children: [
            {
                path: 'assignButton/:roleId(\\d+)',
                component: () => import('@/views/system/role/assignButton.vue'),
                name: 'RoleAssignButton',
                meta: {title: '分配按钮权限', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/system/tenant',
        component: BaseLayout,
        hidden: true,
        permissions: [MenuPermission.SYSTEM_TENANT_ASSIGN_USER],
        children: [
            {
                path: 'assignUser/:tenantId(\\d+)',
                component: () => import('@/views/system/tenant/assignUser.vue'),
                name: 'TenantAssignUser',
                meta: {title: '分配用户', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/system/dept',
        component: BaseLayout,
        hidden: true,
        permissions: [MenuPermission.SYSTEM_DEPT_ASSIGN_ROLE],
        children: [
            {
                path: 'assignRole/:deptId(\\d+)',
                component: () => import('@/views/system/dept/assignRole.vue'),
                name: 'DeptAssignRole',
                meta: {title: '分配用户', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/common/iconTemplate',
        component: BaseLayout,
        hidden: true,
        permissions: [MenuPermission.COMMON_ICON_TEMPLATE_HEADER_SETUP_DETAIL],
        children: [
            {
                path: 'iconTemplateDetail/:headerId(\\d+)',
                component: () => import('@/views/common/iconTemplate/iconTemplateDetail.vue'),
                name: 'IconTemplateDetail',
                meta: {title: '图标模板明细', affix: false, cache: false, closable: true}
            }
        ]
    },
]
