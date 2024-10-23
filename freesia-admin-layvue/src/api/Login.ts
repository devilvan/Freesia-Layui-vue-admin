import Http from "./Http";

const sseDisconnectUrl = import.meta.env.VITE_SSE_DISCONNECT_URL

export const login = function (encrypt: string) {
    let param = {encrypt: encrypt}
    return Http.post('/api/sysLoginController/sysLogin', param)
}

export const menu = function () {
    return Http.get('/user/menu')
}

export const permission = function () {
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

export const sseDisconnect = function () {
    return Http.get(sseDisconnectUrl)
}
