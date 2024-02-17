import {LoginVo} from "../types/login/LoginForm";
import Http from "./Http";

export const login = function(loginForm: LoginVo) {
    return Http.post('/api/sysLoginController/sysLogin', loginForm)
}

export const menu = function() {
    return Http.get('/user/menu')
}

export const permission = function() {
    return Http.get('/user/permission')
}

export const getInfo = function () {
    return Http.get('/api/sysLoginController/getInfo')
}

export const getRouters = function () {
    return Http.get('/api/sysLoginController/getRouters')
}

export const getMenu = function () {
    return Http.get('/api/sysLoginController/getMenu')
}

export const logout = function () {
    return Http.post('/api/sysLoginController/sysLogOut')
}
