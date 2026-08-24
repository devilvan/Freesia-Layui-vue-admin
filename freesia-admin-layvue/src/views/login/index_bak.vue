<template>
  <div class="login-wrap">
    <div class="login-root">
      <div class="login-main">
        <img
          class="login-one-ball"
          src="https://assets.codehub.cn/micro-frontend/login/fca1d5960ccf0dfc8e32719d8a1d80d2.png"
        />
        <img
          class="login-two-ball"
          src="https://assets.codehub.cn/micro-frontend/login/4bcf705dad662b33a4fc24aaa67f6234.png"
        />
        <div class="login-container">
          <div class="login-side">
            <div class="login-bg-title">
              <h1>Freesia-Admin</h1>
              <h3 style="margin: 20px auto">开 箱 即 用 的 layui vue 企 业 级 前 端 模 板</h3>
            </div>
          </div>

          <div class="login-ID">
            <div class="login-heading">🎯 Sign in</div>

            <lay-tab v-model="method" type="brief">
              <lay-tab-item title="用户名" id="1">
                <lay-form
                  ref="loginFormRef"
                  :model="loginForm"
                  :rules="loginFormRules"
                  label-position="top"
                  @keydown.enter.prevent="loginSubmit"
                >
                  <lay-form-item label="用户名 / 邮箱" prop="username" required>
                    <lay-input
                      v-model="loginForm.username"
                      :allow-clear="true"
                      prefix-icon="layui-icon-username"
                      placeholder="用户名 / 邮箱"
                    />
                  </lay-form-item>

                  <lay-form-item label="密码" prop="password" required>
                    <lay-input
                      v-model="loginForm.password"
                      :allow-clear="false"
                      password
                      prefix-icon="layui-icon-password"
                      placeholder="密码"
                      type="password"
                    />
                  </lay-form-item>

                  <lay-form-item v-if="captchaEnabled" label="验证码" prop="code" required>
                    <div class="code-row">
                      <div class="code-input-wrap">
                        <lay-input
                          v-model="loginForm.code"
                          :allow-clear="true"
                          prefix-icon="layui-icon-vercode"
                          placeholder="验证码"
                        />
                      </div>
                      <div class="login-captcha" @click="refreshCaptcha">
                        <img :src="captchaImg" alt="获取验证码" />
                      </div>
                    </div>
                  </lay-form-item>

                  <lay-form-item>
                    <lay-button
                      type="primary"
                      :loading="loging"
                      :fluid="true"
                      loadingIcon="layui-icon-loading"
                      @click="loginSubmit"
                    >
                      登录
                    </lay-button>
                  </lay-form-item>
                </lay-form>
              </lay-tab-item>

              <lay-tab-item title="二维码" id="2">
                <div class="qrcode-panel">
                  <div v-if="qrcodeLoading" class="qrcode-loading">加载中...</div>
                  <template v-else>
                    <lay-qrcode :text="qrcodeTicket" :width="200" color="#000" />
                    <p class="qrcode-tip">请使用小程序扫描二维码</p>
                    <button class="qrcode-refresh" type="button" @click="refreshQrcode">
                      <lay-icon type="layui-icon-refresh-three"></lay-icon>
                      刷新二维码
                    </button>
                  </template>
                </div>
              </lay-tab-item>
            </lay-tab>

            <div class="auth-links">
              <span class="auth-link" @click="openRegisterModal">邮箱注册</span>
              <span class="auth-sep">|</span>
              <span class="auth-link" @click="openResetModal">忘记密码</span>
            </div>

            <lay-line>其他登录方式</lay-line>
            <ul class="other-ways">
              <li @click="oauthLogin('wechat_open')" style="cursor: pointer">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/WX.svg" />
                  <p class="text">微信</p>
                </div>
              </li>
              <li style="cursor: pointer">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/QQ.svg" />
                  <p class="text">QQ</p>
                </div>
              </li>
              <li @click="oauthLogin('gitee')" style="cursor: pointer">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/Gitee.svg" />
                  <p class="text">Gitee</p>
                </div>
              </li>
              <li @click="oauthLogin('github')" style="cursor: pointer">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/Github.svg" />
                  <p class="text">Github</p>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <lay-layer v-model="registerVisible" title="邮箱注册" :area="['460px']">
      <div class="auth-layer">
        <lay-form
          ref="registerFormRef"
          :model="registerForm"
          label-position="top"
          @keydown.enter.prevent="registerSubmit"
        >
          <lay-form-item label="邮箱" prop="email" required>
            <lay-input
              v-model="registerForm.email"
              :allow-clear="true"
              prefix-icon="layui-icon-email"
              placeholder="请输入邮箱"
            />
          </lay-form-item>

          <lay-form-item label="密码" prop="password" required>
            <lay-input
              v-model="registerForm.password"
              :allow-clear="false"
              password
              prefix-icon="layui-icon-password"
              placeholder="6-20位，包含字母、数字、特殊字符"
              type="password"
            />
          </lay-form-item>

          <lay-form-item label="确认密码" prop="confirmPassword" required>
            <lay-input
              v-model="registerConfirmPassword"
              :allow-clear="false"
              password
              prefix-icon="layui-icon-password"
              placeholder="再次输入密码"
              type="password"
            />
          </lay-form-item>

          <lay-form-item label="邮箱验证码" prop="code" required>
            <div class="code-row">
              <div class="code-input-wrap">
                <lay-input
                  v-model="registerForm.code"
                  :allow-clear="true"
                  prefix-icon="layui-icon-vercode"
                  placeholder="请输入邮箱验证码"
                />
              </div>
              <lay-button class="code-btn" type="primary" :disabled="registerCountdown > 0" @click="sendRegisterCode">
                {{ registerCountdown > 0 ? `${registerCountdown}s后重发` : '发送验证码' }}
              </lay-button>
            </div>
          </lay-form-item>
        </lay-form>

        <div class="auth-layer-footer">
          <lay-button type="primary" :loading="registering" loadingIcon="layui-icon-loading" @click="registerSubmit">
            注册
          </lay-button>
          <lay-button type="default" @click="closeRegisterModal">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="resetVisible" title="找回密码" :area="['460px']">
      <div class="auth-layer">
        <lay-form
          ref="resetFormRef"
          :model="resetForm"
          label-position="top"
          @keydown.enter.prevent="resetSubmit"
        >
          <lay-form-item label="邮箱" prop="email" required>
            <lay-input
              v-model="resetForm.email"
              :allow-clear="true"
              prefix-icon="layui-icon-email"
              placeholder="请输入邮箱"
            />
          </lay-form-item>

          <lay-form-item label="新密码" prop="password" required>
            <lay-input
              v-model="resetForm.password"
              :allow-clear="false"
              password
              prefix-icon="layui-icon-password"
              placeholder="6-20位，包含字母、数字、特殊字符"
              type="password"
            />
          </lay-form-item>

          <lay-form-item label="确认密码" prop="confirmPassword" required>
            <lay-input
              v-model="resetConfirmPassword"
              :allow-clear="false"
              password
              prefix-icon="layui-icon-password"
              placeholder="再次输入新密码"
              type="password"
            />
          </lay-form-item>

          <lay-form-item label="邮箱验证码" prop="code" required>
            <div class="code-row">
              <div class="code-input-wrap">
                <lay-input
                  v-model="resetForm.code"
                  :allow-clear="true"
                  prefix-icon="layui-icon-vercode"
                  placeholder="请输入邮箱验证码"
                />
              </div>
              <lay-button class="code-btn" type="primary" :disabled="resetCountdown > 0" @click="sendResetCode">
                {{ resetCountdown > 0 ? `${resetCountdown}s后重发` : '发送验证码' }}
              </lay-button>
            </div>
          </lay-form-item>
        </lay-form>

        <div class="auth-layer-footer">
          <lay-button type="primary" :loading="resetting" loadingIcon="layui-icon-loading" @click="resetSubmit">
            重置密码
          </lay-button>
          <lay-button type="default" @click="closeResetModal">取消</lay-button>
        </div>
      </div>
    </lay-layer>
  </div>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { layer } from '@layui/layer-vue'
import router from '@/router'
import { useUserStore } from '@/store/user'
import { useCryptStore } from '@/store/crypt'
import { getCaptchaCode } from '@/api/captcha/Captcha'
import { emailLogin, findCaptchaEnabled, login, register, resetPassword, sendEmailCode } from '@/api/Login'
import { loginQrcode } from '@/api/module/commone'
import { EmailRegisterVo, LoginVo, ResetPasswordVo } from '@/types/login/LoginForm'

const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,20}$/
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const method = ref('1')
const captchaImg = ref('')
const loging = ref(false)
const registering = ref(false)
const resetting = ref(false)
const captchaEnabled = ref(false)
const qrcodeTicket = ref('')
const qrcodeLoading = ref(false)
const registerVisible = ref(false)
const resetVisible = ref(false)
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

const loginFormRef = ref<any>(null)
const registerFormRef = ref<any>(null)
const resetFormRef = ref<any>(null)
const userStore = useUserStore()
const cryptStore = useCryptStore()

const loginFormRules = ref({
  username: [{ required: true, message: '请输入用户名/邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [
    {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!captchaEnabled.value) {
          return true
        }
        if (!value) {
          callback(new Error('请输入验证码'))
          return false
        }
        return true
      }
    }
  ]
})

let registerTimer: ReturnType<typeof setInterval> | null = null
let resetTimer: ReturnType<typeof setInterval> | null = null

onMounted(async () => {
  const { data, code } = await findCaptchaEnabled()
  if (code === 200 && data === true) {
    captchaEnabled.value = true
    await refreshCaptcha()
  }
})

watch(method, async (val) => {
  if (val === '2' && !qrcodeTicket.value) {
    await refreshQrcode()
  }
})

onBeforeUnmount(() => {
  stopRegisterCountdown()
  stopResetCountdown()
})

const openRegisterModal = () => {
  if (!registerForm.email && validateEmail(loginForm.username)) {
    registerForm.email = loginForm.username
  }
  registerVisible.value = true
}

const closeRegisterModal = () => {
  registerVisible.value = false
}

const openResetModal = () => {
  if (!resetForm.email && validateEmail(loginForm.username)) {
    resetForm.email = loginForm.username
  }
  resetVisible.value = true
}

const closeResetModal = () => {
  resetVisible.value = false
}

const loginSubmit = async () => {
  const form = loginFormRef.value
  if (!form) {
    return
  }

  form.validate(async (isValidate: boolean) => {
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
        layer.msg(res.msg, { icon: 1 }, async () => {
          userStore.token = res.data.token
          await userStore.getInfo()
          await router.push('/')
        })
        return
      }
      await refreshCaptcha()
      layer.msg(res.msg || '登录失败', { icon: 2 })
    } catch (e: any) {
      layer.msg(e?.message || e || '登录失败', { icon: 2 })
    } finally {
      loging.value = false
    }
  })
}

const registerSubmit = async () => {
  if (!validateEmail(registerForm.email)) {
    layer.msg('请输入正确的邮箱', { icon: 2 })
    return
  }
  if (!validatePassword(registerForm.password)) {
    layer.msg('密码需为6-20位且包含字母、数字、特殊字符', { icon: 2 })
    return
  }
  if (registerForm.password !== registerConfirmPassword.value) {
    layer.msg('两次输入的密码不一致', { icon: 2 })
    return
  }
  if (!registerForm.code) {
    layer.msg('请输入邮箱验证码', { icon: 2 })
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
      layer.msg(res.msg || '注册成功', { icon: 1 })
      const email = registerForm.email
      resetRegisterForm()
      closeRegisterModal()
      method.value = '1'
      loginForm.username = email
      loginForm.password = ''
      loginForm.code = ''
      return
    }
    layer.msg(res.msg || '注册失败', { icon: 2 })
  } catch (e: any) {
    layer.msg(e?.message || e || '注册失败', { icon: 2 })
  } finally {
    registering.value = false
  }
}

const resetSubmit = async () => {
  if (!validateEmail(resetForm.email)) {
    layer.msg('请输入正确的邮箱', { icon: 2 })
    return
  }
  if (!validatePassword(resetForm.password)) {
    layer.msg('密码需为6-20位且包含字母、数字、特殊字符', { icon: 2 })
    return
  }
  if (resetForm.password !== resetConfirmPassword.value) {
    layer.msg('两次输入的新密码不一致', { icon: 2 })
    return
  }
  if (!resetForm.code) {
    layer.msg('请输入邮箱验证码', { icon: 2 })
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
      layer.msg(res.msg || '重置成功', { icon: 1 })
      const email = resetForm.email
      resetResetForm()
      closeResetModal()
      method.value = '1'
      loginForm.username = email
      loginForm.password = ''
      loginForm.code = ''
      return
    }
    layer.msg(res.msg || '重置失败', { icon: 2 })
  } catch (e: any) {
    layer.msg(e?.message || e || '重置失败', { icon: 2 })
  } finally {
    resetting.value = false
  }
}

const sendRegisterCode = async () => {
  if (!validateEmail(registerForm.email)) {
    layer.msg('请输入正确的邮箱', { icon: 2 })
    return
  }
  if (registerCountdown.value > 0) {
    return
  }

  try {
    const encrypted = await cryptStore.encryptAes({ email: registerForm.email, scene: 'register' })
    const res: any = await sendEmailCode(encrypted)
    if (res.code === 200) {
      layer.msg(res.msg || '验证码已发送', { icon: 1 })
      startRegisterCountdown()
      return
    }
    layer.msg(res.msg || '验证码发送失败', { icon: 2 })
  } catch (e: any) {
    layer.msg(e?.message || e || '验证码发送失败', { icon: 2 })
  }
}

const sendResetCode = async () => {
  if (!validateEmail(resetForm.email)) {
    layer.msg('请输入正确的邮箱', { icon: 2 })
    return
  }
  if (resetCountdown.value > 0) {
    return
  }

  try {
    const encrypted = await cryptStore.encryptAes({ email: resetForm.email, scene: 'reset_password' })
    const res: any = await sendEmailCode(encrypted)
    if (res.code === 200) {
      layer.msg(res.msg || '验证码已发送', { icon: 1 })
      startResetCountdown()
      return
    }
    layer.msg(res.msg || '验证码发送失败', { icon: 2 })
  } catch (e: any) {
    layer.msg(e?.message || e || '验证码发送失败', { icon: 2 })
  }
}

const refreshQrcode = async () => {
  qrcodeLoading.value = true
  try {
    const { data, code, msg }: any = await loginQrcode()
    if (code === 200) {
      const ticket = data?.data?.ticket || data?.ticket || ''
      qrcodeTicket.value = ticket
      return
    }
    layer.msg(msg || '二维码获取失败', { icon: 2 })
  } catch (e: any) {
    layer.msg(e?.message || '二维码获取失败', { icon: 2 })
  } finally {
    qrcodeLoading.value = false
  }
}

const refreshCaptcha = async () => {
  const res: any = await getCaptchaCode()
  if (res.code === 200) {
    captchaImg.value = `data:image/gif;base64,${res.data?.captchaImg || ''}`
    loginForm.captchaKey = res.data?.captchaKey
    return
  }
  layer.msg(res.msg || '验证码获取失败', { icon: 2 })
}

const oauthLogin = (provider: string) => {
  const baseURL = import.meta.env.VITE_APP_BASE_URL as string
  const frontendCallbackUrl = window.location.origin + '/oauth/callback/' + provider
  const authorizeUrl =
    baseURL + '/api/sysLoginController/oauth/authorize/' + provider + '?redirectUrl=' + encodeURIComponent(frontendCallbackUrl)
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
.login-wrap {
  position: fixed;
  inset: 0;
  overflow: auto;
  min-width: 600px;
  z-index: 9;
  background-image: url(https://assets.codehub.cn/micro-frontend/login/f7eeecbeccefe963298c23b54741d473.png);
  background-repeat: no-repeat;
  background-size: cover;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  box-sizing: border-box;
}

.login-wrap :deep(.layui-input-block) {
  margin-left: 0 !important;
}

.login-root {
  position: relative;
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

.login-one-ball {
  opacity: 0.4;
  position: absolute;
  max-width: 568px;
  left: -400px;
  bottom: 0;
}

.login-two-ball {
  opacity: 0.4;
  position: absolute;
  max-width: 320px;
  right: -200px;
  top: -60px;
}

.login-container {
  position: relative;
  overflow: hidden;
  width: 940px;
  height: 720px;
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

.login-heading {
  font-size: 22px;
  margin-bottom: 15px;
  margin-top: 5px;
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
  margin: 10px 0;
  letter-spacing: 2px;
  height: 40px;
}

.login-container .layui-line-horizontal {
  letter-spacing: 2px;
  margin-bottom: 34px;
  margin-top: 24px;
}

.code-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.code-input-wrap {
  flex: 1;
  min-width: 0;
}

.login-captcha {
  display: inline-block;
  vertical-align: bottom;
  width: 108px;
  height: 40px;
  color: var(--global-primary-color);
  border-radius: 4px;
  border: 1px solid hsla(0, 0%, 60%, 0.46);
  transition: border 0.2s;
  box-sizing: border-box;
  background: #fff;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}

.login-captcha img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.auth-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 6px;
  color: #8592a6;
  font-size: 13px;
}

.auth-link {
  color: var(--global-primary-color);
  cursor: pointer;
}

.auth-sep {
  color: #cbd5e1;
}

.other-ways {
  display: flex;
  justify-content: space-between;
  margin: 0;
  padding: 20px 0 0;
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
  box-shadow: 0 1px 2px 0 rgb(9 30 66 / 4%), 0 1px 4px 0 rgb(9 30 66 / 10%), 0 0 1px 0 rgb(9 30 66 / 10%);
}

.line-container .text {
  display: block;
  margin: 12px 0 0;
  font-size: 12px;
  color: #8592a6;
}

.qrcode-panel {
  width: 220px;
  margin: 0 auto;
  text-align: center;
  padding: 10px 0 0;
}

.qrcode-loading {
  padding: 40px 0;
  color: #64748b;
}

.qrcode-tip {
  font-size: 12px;
  color: #999;
  margin: 10px 0 10px;
}

.qrcode-refresh {
  border: 0;
  background: transparent;
  color: var(--global-primary-color);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.auth-layer {
  padding: 20px;
}

.auth-layer-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

:deep(.layui-tab-title .layui-this) {
  background-color: transparent;
}

@media (max-width: 960px) {
  .login-wrap {
    min-width: 0;
    padding: 16px;
  }

  .login-container {
    width: 100%;
    height: auto;
    flex-direction: column;
  }

  .login-side {
    min-height: 240px;
  }

  .login-ID {
    width: 100%;
    min-width: 0;
    box-sizing: border-box;
  }

  .other-ways {
    flex-wrap: wrap;
    gap: 12px;
  }

  .other-ways li {
    width: calc(50% - 6px);
  }
}
</style>
