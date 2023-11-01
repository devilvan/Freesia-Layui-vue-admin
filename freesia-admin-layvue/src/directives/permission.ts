// noinspection DuplicatedCode

import {Directive} from 'vue';
import {useUserStore} from '../store/user';
import app from '../main'

export const permission: Directive = {
    mounted(el, binding) {
        toolPermission(el, binding);
    },
    updated(el, binding) {
        toolPermission(el, binding);
    }
}

export const role: Directive = {
    mounted(el, binding) {
        toolRole(el, binding);
    },
    updated(el, binding) {
        toolRole(el, binding);
    }
}

const toolPermission = (el: any, binding: any) => {
    const {value} = binding;
    const userInfoStore = useUserStore();
    const permissions = userInfoStore.permissions;
    if (value && value instanceof Array && value.length > 0) {
        const hasPermission = permissions.some((permission) => {
            return app.config.globalProperties.$ADMIN_PERMISSION === permission || value.includes(permission);
        })
        if (!hasPermission) {
            el.parentNode && el.parentNode.removeChild(el);
        }
    }
}

const toolRole = (el: any, binding: any) => {
    const {value} = binding;
    const userInfoStore = useUserStore();
    const roles = userInfoStore.roles;
    if (value && value instanceof Array && value.length > 0) {
        const hasRole = roles.some((role) => {
            return app.config.globalProperties.$ADMIN_ROLE === role || value.includes(role);
        })
        if (!hasRole) {
            el.parentNode && el.parentNode.removeChild(el);
        }
    }
}
