import Http from '../Http';

export const verificationImg = function () {
  return Http.get('/login/verificationImg')
}

export const loginQrcode = function () {
  return Http.get('/login/loginQrcode')
}
