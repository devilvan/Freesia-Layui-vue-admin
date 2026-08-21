<template>
  <div class="login-wrap">
    <div class="login-root">
      <div class="login-main">
        <img class="login-one-ball"
             src="https://assets.codehub.cn/micro-frontend/login/fca1d5960ccf0dfc8e32719d8a1d80d2.png"/>
        <img class="login-two-ball"
             src="https://assets.codehub.cn/micro-frontend/login/4bcf705dad662b33a4fc24aaa67f6234.png"/>
        <div class="login-container">
          <div class="login-side">
            <div class="login-bg-title">
              <h1>Freesia-Admin</h1>
              <h3 style="margin: 20px auto">
                开 箱 即 用 的 layui vue 企 业 级 前 端 模 板
              </h3>
            </div>
          </div>
          <div class="login-ID">
            <div class="auth-header">
              <div class="auth-title">{{ pageTitle }}</div>
              <div class="auth-subtitle">{{ pageSubtitle }}</div>
            </div>

            <template v-if="pageMode === 'login'">
              <lay-tab type="brief" v-model="method">
                <lay-tab-item title="账号登录" id="1">
                  <lay-form :model="loginForm" label-position="top" ref="loginFormRef" :rules="loginFormRules"
                            @keydown.enter.prevent="loginSubmit">
                    <lay-form-item label="用户名 / 邮箱" prop="username" required>
                      <lay-input :allow-clear="true" prefix-icon="layui-icon-username" placeholder="用户名 / 邮箱"
                                 v-model="loginForm.username"></lay-input>
                    </lay-form-item>
                    <lay-form-item label="密码" prop="password" required>
                      <lay-input :allow-clear="false" prefix-icon="layui-icon-password" placeholder="密码" password
                                 type="password" v-model="loginForm.password"></lay-input>
                    </lay-form-item>
                    <lay-form-item label="验证码" prop="code" :style="captchaEnabled ? '' : 'display: none'"
                                   :required="captchaEnabled">
                      <div style="width: 60%; display: inline-block">
                        <lay-input :allow-clear="true" prefix-icon="layui-icon-vercode" placeholder="验证码"
                                   v-model="loginForm.code"></lay-input>
                      </div>
                      <div class="login-captcha" @click="toRefreshImg">
                        <img style="width: 100%" :src="captchaImg" alt="获取验证码"/>
                      </div>
                    </lay-form-item>
                    <lay-form-item>
                      <lay-button style="margin-top: 20px" type="primary" :loading="loging" :fluid="true"
                                  loadingIcon="layui-icon-loading" @click="loginSubmit">登录
                      </lay-button>
                    </lay-form-item>
                  </lay-form>
                </lay-tab-item>
                <lay-tab-item title="二维码" id="2">
                  <div class="qrcode-panel">
                    <div v-if="qrcodeLoading" class="qrcode-loading">加载中...</div>
                    <div v-else>
                      <lay-qrcode :text="qrcodeTicket" :width="200" color="#000" style="margin: 10px 0 20px"></lay-qrcode>
                      <p class="qrcode-tip">请使用小程序扫描二维码</p>
                      <div class="qrcode-refresh" @click="refreshQrcode">
                        <lay-icon type="layui-icon-refresh-three"></lay-icon>
                        刷新二维码
                      </div>
                    </div>
                  </div>
                </lay-tab-item>
              </lay-tab>

              <div class="auth-links">
                <span class="auth-link" @click="switchMode('register')">没有账号？去注册</span>
                <span class="auth-sep">|</span>
                <span class="auth-link" @click="switchMode('reset')">忘记密码？</span>
              </div>

              <lay-line>其他登录方式</lay-line>
              <ul class="other-ways">
                <li @click="oauthLogin('wechat_open')" style="cursor:pointer">
                  <div class="line-container">
                    <img class="icon" src="@/assets/login/WX.svg"/>
                    <p class="text">微信</p>
                  </div>
                </li>
                <li style="cursor:pointer">
                  <div class="line-container">
                    <img class="icon" src="@/assets/login/QQ.svg"/>
                    <p class="text">QQ</p>
                  </div>
                </li>
                <li @click="oauthLogin('gitee')" style="cursor:pointer">
                  <div class="line-container">
                    <img class="icon" src="@/assets/login/Gitee.svg"/>
                    <p class="text">Gitee</p>
                  </div>
                </li>
                <li @click="oauthLogin('github')" style="cursor:pointer">
                  <div class="line-container">
                    <img class="icon" src="@/assets/login/Github.svg"/>
                    <p class="text">Github</p>
                  </div>
                </li>
              </ul>
            </template>

            <template v-else-if="pageMode === 'register'">
              <lay-form :model="registerForm" label-position="top" ref="registerFormRef"
                        @keydown.enter.prevent="registerSubmit">
                <lay-form-item label="邮箱" prop="email" required>
                  <lay-input :allow-clear="true" prefix-icon="layui-icon-email" placeholder="请输入邮箱"
                             v-model="registerForm.email"></lay-input>
                </lay-form-item>
                <lay-form-item label="密码" prop="password" required>
                  <lay-input :allow-clear="false" prefix-icon="layui-icon-password" placeholder="6-20位，包含字母、数字、特殊字符"
                             password type="password" v-model="registerForm.password"></lay-input>
                </lay-form-item>
                <lay-form-item label="确认密码" prop="confirmPassword" required>
                  <lay-input :allow-clear="false" prefix-icon="layui-icon-password" placeholder="再次输入密码"
                             password type="password" v-model="registerConfirmPassword"></lay-input>
                </lay-form-item>
                <lay-form-item label="邮箱验证码" prop="code" required>
                  <div class="code-row">
                    <lay-input :allow-clear="true" prefix-icon="layui-icon-vercode" placeholder="请输入邮箱验证码"
                               v-model="registerForm.code"></lay-input>
                    <lay-button class="code-btn" type="primary" :disabled="registerCountdown > 0" @click="sendRegisterCode">
                      {{ registerCountdown > 0 ? `${registerCountdown}s后重发` : '发送验证码' }}
                    </lay-button>
                  </div>
                </lay-form-item>
                <lay-form-item>
                  <lay-button style="margin-top: 20px" type="primary" :loading="registering" :fluid="true"
                              loadingIcon="layui-icon-loading" @click="registerSubmit">注册
                  </lay-button>
                </lay-form-item>
                <lay-form-item>
                  <lay-button type="default" :fluid="true" @click="switchMode('login')">返回登录</lay-button>
                </lay-form-item>
              </lay-form>
            </template>

            <template v-else>
              <lay-form :model="resetForm" label-position="top" ref="resetFormRef"
                        @keydown.enter.prevent="resetSubmit">
                <lay-form-item label="邮箱" prop="email" required>
                  <lay-input :allow-clear="true" prefix-icon="layui-icon-email" placeholder="请输入邮箱"
                             v-model="resetForm.email"></lay-input>
                </lay-form-item>
                <lay-form-item label="新密码" prop="password" required>
                  <lay-input :allow-clear="false" prefix-icon="layui-icon-password" placeholder="6-20位，包含字母、数字、特殊字符"
                             password type="password" v-model="resetForm.password"></lay-input>
                </lay-form-item>
                <lay-form-item label="确认密码" prop="confirmPassword" required>
                  <lay-input :allow-clear="false" prefix-icon="layui-icon-password" placeholder="再次输入新密码"
                             password type="password" v-model="resetConfirmPassword"></lay-input>
                </lay-form-item>
                <lay-form-item label="邮箱验证码" prop="code" required>
                  <div class="code-row">
                    <lay-input :allow-clear="true" prefix-icon="layui-icon-vercode" placeholder="请输入邮箱验证码"
                               v-model="resetForm.code"></lay-input>
                    <lay-button class="code-btn" type="primary" :disabled="resetCountdown > 0" @click="sendResetCode">
                      {{ resetCountdown > 0 ? `${resetCountdown}s后重发` : '发送验证码' }}
                    </lay-button>
                  </div>
                </lay-form-item>
                <lay-form-item>
                  <lay-button style="margin-top: 20px" type="primary" :loading="resetting" :fluid="true"
                              loadingIcon="layui-icon-loading" @click="resetSubmit">重置密码
                  </lay-button>
                </lay-form-item>
                <lay-form-item>
                  <lay-button type="default" :fluid="true" @click="switchMode('login')">返回登录</lay-button>
                </lay-form-item>
              </lay-form>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref, watch} from 'vue'
import {useUserStore} from '@/store/user'
import {layer} from '@layui/layer-vue'
import {EmailRegisterVo, LoginVo, ResetPasswordVo} from "@/types/login/LoginForm";
import {emailLogin, findCaptchaEnabled, login, register, resetPassword, sendEmailCode} from "@/api/Login";
import {getCaptchaCode} from "@/api/captcha/Captcha";
import {loginQrcode} from "@/api/module/commone";
import router from "@/router";
import {useCryptStore} from "@/store/crypt";

type PageMode = 'login' | 'register' | 'reset'

const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,20}$/
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const pageMode = ref<PageMode>('login')
const method = ref('1')
const captchaImg = ref('')
const loging = ref(false)
const registering = ref(false)
const resetting = ref(false)
const captchaEnabled = ref(false)
const qrcodeTicket = ref('')
const qrcodeLoading = ref(false)
const registerCountdown = ref(0)
const resetCountdown = ref(0)
const registerConfirmPassword = ref('')
const resetConfirmPassword = ref('')
const loginForm = reactive<LoginVo>({
  username: '',
  password: '',
  code: '',
  captchaKey: ''
})
const registerForm = reactive<EmailRegisterVo>({
  email: '',
  password: '',
  code: '',
  nickName: ''
})
const resetForm = reactive<ResetPasswordVo>({
  email: '',
  password: '',
  code: ''
})
const loginFormRef = ref()
const registerFormRef = ref()
const resetFormRef = ref()
const userStore = useUserStore()
const cryptStore = useCryptStore()

let registerTimer: ReturnType<typeof setInterval> | null = null
let resetTimer: ReturnType<typeof setInterval> | null = null

const pageTitle = computed(() => {
  if (pageMode.value === 'register') return '创建账号'
  if (pageMode.value === 'reset') return '找回密码'
  return '🎯 Sign in'
})

const pageSubtitle = computed(() => {
  if (pageMode.value === 'register') return '使用邮箱完成注册'
  if (pageMode.value === 'reset') return '通过邮箱验证码重置密码'
  return '邮箱登录、账号登录、二维码登录'
})

onMounted(async () => {
  const {data, code} = await findCaptchaEnabled()
  if (code === 200 && data === true) {
    captchaEnabled.value = true
    await toRefreshImg()
  }
})

watch(method, async (val) => {
  if (pageMode.value === 'login' && val === '2' && !qrcodeTicket.value) {
    await refreshQrcode()
  }
})

onBeforeUnmount(() => {
  stopRegisterCountdown()
  stopResetCountdown()
})

const switchMode = (mode: PageMode) => {
  pageMode.value = mode
  if (mode === 'login' && method.value === '2' && !qrcodeTicket.value) {
    refreshQrcode()
  }
}

const loginSubmit = async () => {
  loginFormRef.value.validate(async (isValidate: boolean) => {
    if (!isValidate) {
      return
    }
    loging.value = true
    try {
      const payload = {
        username: loginForm.username,
        password: loginForm.password,
        code: loginForm.code,
        captchaKey: loginForm.captchaKey
      }
      const encrypted = await cryptStore.encryptAes(payload)
      const api = loginForm.username?.includes('@') ? emailLogin : login
      const res: any = await api(encrypted)
      if (res.code === 200) {
        layer.msg(res.msg, {icon: 1}, async () => {
          userStore.token = res.data.token
          await userStore.getInfo()
          await router.push('/')
        })
        return
      }
      await toRefreshImg()
      layer.msg(res.msg || '登录失败', {icon: 2})
    } catch (e: any) {
      layer.confirm(e?.message || e || '登录失败', {icon: 2})
    } finally {
      loging.value = false
    }
  })
}

const registerSubmit = async () => {
  if (!validateEmail(registerForm.email)) {
    layer.msg('请输入正确的邮箱', {icon: 2})
    return
  }
  if (!validatePassword(registerForm.password)) {
    layer.msg('密码需为6-20位且包含字母、数字、特殊字符', {icon: 2})
    return
  }
  if (registerForm.password !== registerConfirmPassword.value) {
    layer.msg('两次输入的密码不一致', {icon: 2})
    return
  }
  if (!registerForm.code) {
    layer.msg('请输入邮箱验证码', {icon: 2})
    return
  }
  registering.value = true
  try {
    const encrypted = await cryptStore.encryptAes({
      email: registerForm.email,
      password: registerForm.password,
      code: registerForm.code,
      nickName: registerForm.nickName
    })
    const res: any = await register(encrypted)
    if (res.code === 200) {
      layer.msg(res.msg || '注册成功', {icon: 1})
      const email = registerForm.email
      resetRegisterForm()
      method.value = '1'
      switchMode('login')
      loginForm.username = email
      return
    }
    layer.msg(res.msg || '注册失败', {icon: 2})
  } catch (e: any) {
    layer.confirm(e?.message || e || '注册失败', {icon: 2})
  } finally {
    registering.value = false
  }
}

const resetSubmit = async () => {
  if (!validateEmail(resetForm.email)) {
    layer.msg('请输入正确的邮箱', {icon: 2})
    return
  }
  if (!validatePassword(resetForm.password)) {
    layer.msg('密码需为6-20位且包含字母、数字、特殊字符', {icon: 2})
    return
  }
  if (resetForm.password !== resetConfirmPassword.value) {
    layer.msg('两次输入的新密码不一致', {icon: 2})
    return
  }
  if (!resetForm.code) {
    layer.msg('请输入邮箱验证码', {icon: 2})
    return
  }
  resetting.value = true
  try {
    const encrypted = await cryptStore.encryptAes({
      email: resetForm.email,
      password: resetForm.password,
      code: resetForm.code
    })
    const res: any = await resetPassword(encrypted)
    if (res.code === 200) {
      layer.msg(res.msg || '重置成功', {icon: 1})
      const email = resetForm.email
      resetResetForm()
      method.value = '1'
      switchMode('login')
      loginForm.username = email
      return
    }
    layer.msg(res.msg || '重置失败', {icon: 2})
  } catch (e: any) {
    layer.confirm(e?.message || e || '重置失败', {icon: 2})
  } finally {
    resetting.value = false
  }
}

const sendRegisterCode = async () => {
  if (!validateEmail(registerForm.email)) {
    layer.msg('请输入正确的邮箱', {icon: 2})
    return
  }
  if (registerCountdown.value > 0) {
    return
  }
  const encrypted = await cryptStore.encryptAes({email: registerForm.email, scene: 'register'})
  const res: any = await sendEmailCode(encrypted)
  if (res.code === 200) {
    layer.msg(res.msg || '验证码已发送', {icon: 1})
    startRegisterCountdown()
  } else {
    layer.msg(res.msg || '验证码发送失败', {icon: 2})
  }
}

const sendResetCode = async () => {
  if (!validateEmail(resetForm.email)) {
    layer.msg('请输入正确的邮箱', {icon: 2})
    return
  }
  if (resetCountdown.value > 0) {
    return
  }
  const encrypted = await cryptStore.encryptAes({email: resetForm.email, scene: 'reset_password'})
  const res: any = await sendEmailCode(encrypted)
  if (res.code === 200) {
    layer.msg(res.msg || '验证码已发送', {icon: 1})
    startResetCountdown()
  } else {
    layer.msg(res.msg || '验证码发送失败', {icon: 2})
  }
}

const refreshQrcode = async () => {
  qrcodeLoading.value = true
  try {
    const {data, code, msg}: any = await loginQrcode()
    if (code === 200) {
      qrcodeTicket.value = data?.data || data || ''
    } else {
      layer.msg(msg || '二维码获取失败', {icon: 2})
    }
  } catch (e) {
    layer.msg('二维码获取失败', {icon: 2})
  } finally {
    qrcodeLoading.value = false
  }
}

const toRefreshImg = async () => {
  const res: any = await getCaptchaCode()
  if (res.code === 200) {
    captchaImg.value = "data:image/gif;base64," + res.data?.captchaImg
    loginForm.captchaKey = res.data?.captchaKey
    return
  }
  layer.msg(res.msg, {icon: 2})
}

const oauthLogin = (provider: string) => {
  const baseURL = import.meta.env.VITE_APP_BASE_URL as string
  const frontendCallbackUrl = window.location.origin + '/oauth/callback/' + provider
  const authorizeUrl = baseURL + '/api/sysLoginController/oauth/authorize/' + provider
      + '?redirectUrl=' + encodeURIComponent(frontendCallbackUrl)
  window.location.href = authorizeUrl
}

const validateEmail = (email?: string) => !!email && EMAIL_PATTERN.test(email)

const validatePassword = (password?: string) => !!password && PASSWORD_PATTERN.test(password)

const startRegisterCountdown = () => {
  stopRegisterCountdown()
  registerCountdown.value = 60
  registerTimer = setInterval(() => {
    registerCountdown.value--
    if (registerCountdown.value <= 0) {
      stopRegisterCountdown()
    }
  }, 1000)
}

const startResetCountdown = () => {
  stopResetCountdown()
  resetCountdown.value = 60
  resetTimer = setInterval(() => {
    resetCountdown.value--
    if (resetCountdown.value <= 0) {
      stopResetCountdown()
    }
  }, 1000)
}

const stopRegisterCountdown = () => {
  if (registerTimer) {
    clearInterval(registerTimer)
    registerTimer = null
  }
  registerCountdown.value = 0
}

const stopResetCountdown = () => {
  if (resetTimer) {
    clearInterval(resetTimer)
    resetTimer = null
  }
  resetCountdown.value = 0
}

const resetRegisterForm = () => {
  registerForm.email = ''
  registerForm.password = ''
  registerForm.code = ''
  registerForm.nickName = ''
  registerConfirmPassword.value = ''
  stopRegisterCountdown()
}

const resetResetForm = () => {
  resetForm.email = ''
  resetForm.password = ''
  resetForm.code = ''
  resetConfirmPassword.value = ''
  stopResetCountdown()
}
</script>

<style scoped>
.login-captcha {
  display: inline-block;
  vertical-align: bottom;
  width: 108px;
  height: 40px;
  color: var(--global-primary-color);
  margin-left: 8px;
  border-radius: 4px;
  border: 1px solid hsla(0, 0%, 60%, 0.46);
  transition: border 0.2s;
  box-sizing: border-box;
  background: #fff;
  overflow: hidden;
  cursor: pointer;
}

.login-one-ball {
  opacity: 0.4;
  position: absolute;
  max-width: 568px;
  left: -400px;
  bottom: 0px;
}

.login-two-ball {
  opacity: 0.4;
  position: absolute;
  max-width: 320px;
  right: -200px;
  top: -60px;
}

.login-wrap {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  overflow: auto;
  min-width: 600px;
  z-index: 9;
  background-image: url(https://assets.codehub.cn/micro-frontend/login/f7eeecbeccefe963298c23b54741d473.png);
  background-repeat: no-repeat;
  background-size: cover;
  min-height: 100vh;
}

.login-wrap :deep(.layui-input-block) {
  margin-left: 0 !important;
}

.login-root {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  justify-content: center;
  width: 100%;
  min-width: 320px;
  background-color: initial;
}

.login-main {
  position: relative;
  display: block;
}

.login-container {
  position: relative;
  overflow: hidden;
  width: 940px;
  min-height: 720px;
  max-width: calc(100vw - 28px);
  border-radius: 4px;
  background: hsla(0, 0%, 100%, 0.5);
  backdrop-filter: blur(30px);
  display: flex;
  box-shadow: 6px 6px 12px 4px rgba(0, 0, 0, 0.1);
}

.login-side {
  padding: 40px 20px 20px;
  background-color: var(--global-primary-color);
  flex: 1;
  height: 100%;
}

.login-bg-title {
  flex: 1;
  height: 84%;
  color: #fff;
  text-align: center;
  background-image: url('@/assets/login/login-bg.svg');
  background-repeat: no-repeat;
  background-position: bottom;
  background-size: contain;
  min-width: 200px;
}

.login-ID {
  padding: 20px;
  width: 380px;
  min-width: 380px;
}

.auth-header {
  margin-bottom: 12px;
}

.auth-title {
  font-size: 22px;
  margin-bottom: 8px;
  margin-top: 5px;
  font-weight: 700;
}

.auth-subtitle {
  font-size: 13px;
  color: #8592a6;
}

.login-container .layui-tab-head {
  background: transparent;
}

.login-container .layui-input-wrapper {
  margin-top: 12px;
  margin-bottom: 12px;
}

.login-container .assist {
  margin-top: 5px;
  margin-bottom: 5px;
  letter-spacing: 2px;
}

.login-container .layui-btn {
  margin: 10px 0px 10px 0px;
  letter-spacing: 2px;
  height: 40px;
}

.login-container .layui-line-horizontal {
  letter-spacing: 2px;
  margin-bottom: 34px;
  margin-top: 24px;
}

.auth-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin: 8px 0 4px;
  color: #8592a6;
  font-size: 13px;
}

.auth-link {
  cursor: pointer;
  color: var(--global-primary-color);
}

.auth-sep {
  color: #c7cdd8;
}

.other-ways {
  display: flex;
  justify-content: space-between;
  margin: 0;
  padding: 20px;
  list-style: none;
  font-size: 14px;
  font-weight: 400;
}

.other-ways li {
  width: 100%;
}

.line-container {
  justify-content: center;
  align-items: center;
  text-align: center;
  cursor: pointer;
}

.line-container .icon {
  height: 28px;
  width: 28px;
  margin-right: 0;
  vertical-align: middle;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 2px 0 rgb(9 30 66 / 4%), 0 1px 4px 0 rgb(9 30 66 / 10%),
  0 0 1px 0 rgb(9 30 66 / 10%);
}

.line-container .text {
  display: block;
  margin: 12px 0 0;
  font-size: 12px;
  color: #8592a6;
}

.code-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.code-row :deep(.layui-input-wrapper) {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
}

.qrcode-panel {
  width: 220px;
  margin: 0 auto;
  text-align: center;
  padding: 20px 0 10px;
}

.qrcode-loading {
  padding: 40px 0;
}

.qrcode-tip {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.qrcode-refresh {
  cursor: pointer;
  color: var(--global-primary-color);
}

:deep(.layui-tab-title .layui-this) {
  background-color: transparent;
}
</style>
