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
        meta: {title: '鐧诲綍椤甸潰'},
    },
    {
        path: '/workSpace',
        redirect: '/workSpace/worldclock/index',
        component: BaseLayout,
        meta: {title: '涓栫晫鏃堕挓'},
    },
    {
        path: '/oauth/callback/:provider',
        component: () => import('@/views/login/oauthCallback.vue'),
        meta: {title: 'OAuth 鐧诲綍鍥炶皟'},
    },
    {
        path: '/chat/deepseek',
        component: BaseLayout,
        meta: {title: 'DeepSeek AI 瀵硅瘽'},
        children: [
            {
                path: 'index',
                component: () => import('@/views/chat/deepseek/index.vue'),
                name: 'DeepseekChat',
                meta: {title: 'DeepSeek AI 瀵硅瘽', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/fusebean/home',
        component: BaseLayout,
        meta: {title: '鎷艰眴鍥剧焊'},
        children: [
            {
                path: 'index',
                component: () => import('@/views/fusebean/home/index.vue'),
                name: 'FuseBeanHome',
                meta: {title: '鎷艰眴鍥剧焊鐢熸垚', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/workSpace/todayhistory',
        component: BaseLayout,
        meta: {title: '鍘嗗彶涓婄殑浠婂ぉ'},
        children: [
            {
                path: 'index',
                component: () => import('@/views/workSpace/todayhistory/index.vue'),
                name: 'TodayHistory',
                meta: {title: '鍘嗗彶涓婄殑浠婂ぉ', affix: false, cache: false, closable: true}
            }
        ]
    },
    {
        path: '/error/:code',
        component: ErrorCode,
        meta: {title: '閿欒椤甸潰'},
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
                meta: {title: '鍒嗛厤瑙掕壊', affix: false, cache: false, closable: true}
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
                meta: {title: '鍒嗛厤鐢ㄦ埛', affix: false, cache: false, closable: true}
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
                meta: {title: '鍒嗛厤鎸夐挳鏉冮檺', affix: false, cache: false, closable: true}
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
                meta: {title: '鍒嗛厤鐢ㄦ埛', affix: false, cache: false, closable: true}
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
                meta: {title: '鍒嗛厤鐢ㄦ埛', affix: false, cache: false, closable: true}
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
                meta: {title: '鍥炬爣妯℃澘鏄庣粏', affix: false, cache: false, closable: true}
            }
        ]
    },
]
