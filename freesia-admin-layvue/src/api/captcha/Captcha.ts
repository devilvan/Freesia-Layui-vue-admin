import Http from "../Http";

export const getCaptchaCode = function () {
    return Http.get('/api/captchaController/getCaptchaCode')
}
