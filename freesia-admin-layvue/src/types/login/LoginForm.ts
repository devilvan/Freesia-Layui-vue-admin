/**
 * 登录功能 值对象传递
 */
export interface LoginVo {
    username?: string;
    password?: string;
    code?: string;
    captchaKey?: string;
}

export interface SendEmailCodeVo {
    email?: string;
    scene?: 'register' | 'reset_password';
}

export interface EmailRegisterVo {
    email?: string;
    password?: string;
    code?: string;
    nickName?: string;
}

export interface ResetPasswordVo {
    email?: string;
    password?: string;
    code?: string;
}
