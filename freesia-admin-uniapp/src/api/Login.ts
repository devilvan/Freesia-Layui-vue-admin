import Http from "./Http";

const encryptPost = (url, encrypt) => Http.post(url, { encrypt })

export const login = function (encrypt: string) {
  return encryptPost('/api/sysLoginController/sysLogin', encrypt)
}

export const emailLogin = function (encrypt: string) {
  return encryptPost('/api/sysLoginController/emailLogin', encrypt)
}

export const sendEmailCode = function (encrypt: string) {
  return encryptPost('/api/sysRegistryController/sendEmailCode', encrypt)
}

export const register = function (encrypt: string) {
  return encryptPost('/api/sysRegistryController/register', encrypt)
}

export const resetPassword = function (encrypt: string) {
  return encryptPost('/api/sysRegistryController/resetPassword', encrypt)
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

export function findCaptchaEnabled() {
  return Http.get("/api/sysLoginController/findCaptchaEnabled")
}

export const renewToken = function () {
  return Http.post('/api/sysLoginController/renewToken')
}
