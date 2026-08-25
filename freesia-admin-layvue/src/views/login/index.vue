<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="login-hero">
        <div class="hero-badge">Freesia Admin 后台管理系统</div>
        <h1 class="hero-title">统一认证入口</h1>
        <p class="hero-desc">
          支持账号登录、邮箱登录、二维码登录，以及邮箱注册和找回密码。
        </p>

        <div class="hero-points">
        </div>
      </section>

      <section class="login-panel">
        <header class="panel-header">
          <div class="panel-title">{{ pageTitle }}</div>
          <div class="panel-subtitle">{{ pageSubtitle }}</div>
        </header>

        <template v-if="pageMode === 'login'">
          <lay-tab v-model="method" type="brief">
            <lay-tab-item title="账号登录" id="1">
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
                    <lay-input
                      v-model="loginForm.code"
                      :allow-clear="true"
                      prefix-icon="layui-icon-vercode"
                      placeholder="验证码"
                    />
                    <div class="login-captcha" @click="refreshCaptcha">
                      <img :src="captchaImg" alt="验证码" />
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

            <lay-tab-item title="二维码登录" id="2">
              <div class="qrcode-panel">
                <div v-if="qrcodeLoading" class="qrcode-loading">加载中...</div>
                <template v-else>
                  <lay-qrcode :text="qrcodeTicket" :width="200" color="#000" />
                  <p class="qrcode-tip">请使用小程序扫描二维码</p>
                  <button class="qrcode-refresh" type="button" @click="refreshQrcode">刷新二维码</button>
                </template>
              </div>
            </lay-tab-item>
          </lay-tab>

          <div class="auth-links">
            <span class="auth-link" @click="switchMode('register')">没有账号？去注册</span>
            <span class="auth-sep">|</span>
            <span class="auth-link" @click="switchMode('reset')">忘记密码？</span>
          </div>

          <div class="oauth-title">其他登录方式</div>
          <div class="oauth-grid">
            <lay-tooltip :visible="false" trigger="hover" content="微信登录">
              <button class="oauth-btn" type="button" @click="oauthLogin('wechat_open')">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/WX.svg"/>
                </div>
              </button>
            </lay-tooltip>
            <lay-tooltip :visible="false" trigger="hover" content="QQ登录">
              <button class="oauth-btn" type="button" @click="oauthLogin('qq_open')">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/QQ.svg"/>
                </div>
              </button>
            </lay-tooltip>
            <lay-tooltip :visible="false" trigger="hover" content="Gitee登录">
              <button class="oauth-btn" type="button" @click="oauthLogin('gitee')">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/Gitee.svg"/>
                </div>
              </button>
            </lay-tooltip>
            <lay-tooltip :visible="false" trigger="hover" content="Github登录">
              <button class="oauth-btn" type="button" @click="oauthLogin('github')">
                <div class="line-container">
                  <img class="icon" src="@/assets/login/Github.svg"/>
                </div>
              </button>
            </lay-tooltip>
          </div>
        </template>

        <template v-else-if="pageMode === 'register'">
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
                <lay-input
                  v-model="registerForm.code"
                  :allow-clear="true"
                  prefix-icon="layui-icon-vercode"
                  placeholder="请输入邮箱验证码"
                />
                <lay-button class="code-btn" type="primary" :disabled="registerCountdown > 0" @click="sendRegisterCode">
                  {{ registerCountdown > 0 ? `${registerCountdown}s后重发` : '发送验证码' }}
                </lay-button>
              </div>
            </lay-form-item>

            <lay-form-item>
              <lay-button
                type="primary"
                :loading="registering"
                :fluid="true"
                loadingIcon="layui-icon-loading"
                @click="registerSubmit"
              >
                注册
              </lay-button>
            </lay-form-item>

            <lay-form-item>
              <lay-button type="default" :fluid="true" @click="switchMode('login')">返回登录</lay-button>
            </lay-form-item>
          </lay-form>
        </template>

        <template v-else>
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
                <lay-input
                  v-model="resetForm.code"
                  :allow-clear="true"
                  prefix-icon="layui-icon-vercode"
                  placeholder="请输入邮箱验证码"
                />
                <lay-button class="code-btn" type="primary" :disabled="resetCountdown > 0" @click="sendResetCode">
                  {{ resetCountdown > 0 ? `${resetCountdown}s后重发` : '发送验证码' }}
                </lay-button>
              </div>
            </lay-form-item>

            <lay-form-item>
              <lay-button
                type="primary"
                :loading="resetting"
                :fluid="true"
                loadingIcon="layui-icon-loading"
                @click="resetSubmit"
              >
                重置密码
              </lay-button>
            </lay-form-item>

            <lay-form-item>
              <lay-button type="default" :fluid="true" @click="switchMode('login')">返回登录</lay-button>
            </lay-form-item>
          </lay-form>
        </template>
      </section>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref, watch} from 'vue'
import {layer} from '@layui/layer-vue'
import router from '@/router'
import {useUserStore} from '@/store/user'
import {useCryptStore} from '@/store/crypt'
import {getCaptchaCode} from '@/api/captcha/Captcha'
import {emailLogin, findCaptchaEnabled, login, register, resetPassword, sendEmailCode} from '@/api/Login'
import {loginQrcode} from '@/api/module/commone'
import {EmailRegisterVo, LoginVo, ResetPasswordVo} from '@/types/login/LoginForm'

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

const loginFormRef = ref<any>(null)
const registerFormRef = ref<any>(null)
const resetFormRef = ref<any>(null)
const userStore = useUserStore()
const cryptStore = useCryptStore()

const loginFormRules = ref({
  username: [{required: true, message: '请输入用户名/邮箱', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}]
})

let registerTimer: ReturnType<typeof setInterval> | null = null
let resetTimer: ReturnType<typeof setInterval> | null = null

const pageTitle = computed(() => {
  if (pageMode.value === 'register') return '创建账号'
  if (pageMode.value === 'reset') return '找回密码'
  return '欢迎回来'
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
    await refreshCaptcha()
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
        layer.msg(res.msg, {icon: 1}, async () => {
          userStore.token = res.data.token
          await userStore.getInfo()
          await router.push('/')
        })
        return
      }
      await refreshCaptcha()
      layer.msg(res.msg || '登录失败', {icon: 2})
    } catch (e: any) {
      layer.msg(e?.message || e || '登录失败', {icon: 2})
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
    layer.msg(e?.message || e || '注册失败', {icon: 2})
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
    layer.msg(e?.message || e || '重置失败', {icon: 2})
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

  try {
    const encrypted = await cryptStore.encryptAes({email: registerForm.email, scene: 'register'})
    const res: any = await sendEmailCode(encrypted)
    if (res.code === 200) {
      layer.msg(res.msg || '验证码已发送', {icon: 1})
      startRegisterCountdown()
      return
    }
    layer.msg(res.msg || '验证码发送失败', {icon: 2})
  } catch (e: any) {
    layer.msg(e?.message || e || '验证码发送失败', {icon: 2})
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

  try {
    const encrypted = await cryptStore.encryptAes({email: resetForm.email, scene: 'reset_password'})
    const res: any = await sendEmailCode(encrypted)
    if (res.code === 200) {
      layer.msg(res.msg || '验证码已发送', {icon: 1})
      startResetCountdown()
      return
    }
    layer.msg(res.msg || '验证码发送失败', {icon: 2})
  } catch (e: any) {
    layer.msg(e?.message || e || '验证码发送失败', {icon: 2})
  }
}

const refreshQrcode = async () => {
  qrcodeLoading.value = true
  try {
    const {data, code, msg}: any = await loginQrcode()
    if (code === 200) {
      const ticket = data?.data?.ticket || data?.ticket || ''
      qrcodeTicket.value = ticket
      return
    }
    layer.msg(msg || '二维码获取失败', {icon: 2})
  } catch (e: any) {
    layer.msg(e?.message || '二维码获取失败', {icon: 2})
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
  layer.msg(res.msg || '验证码获取失败', {icon: 2})
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
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(26, 188, 156, 0.18), transparent 32%),
    radial-gradient(circle at bottom right, rgba(16, 185, 129, 0.14), transparent 28%),
    linear-gradient(135deg, #f5f7fb 0%, #eef3f8 100%);
}

.login-shell {
  width: min(1120px, 100%);
  min-height: 720px;
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.16);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(24px);
}

.login-hero {
  position: relative;
  padding: 56px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(15, 118, 110, 0.95), rgba(15, 23, 42, 0.96)),
    linear-gradient(135deg, #0f766e, #111827);
  isolation: isolate;
}

.login-hero::before,
.login-hero::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  filter: blur(2px);
}

.login-hero::before {
  width: 240px;
  height: 240px;
  right: -80px;
  top: -40px;
}

.login-hero::after {
  width: 320px;
  height: 320px;
  left: -140px;
  bottom: -120px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  letter-spacing: 0.08em;
  font-size: 16px;
}

.hero-title {
  margin: 28px 0 16px;
  font-size: 44px;
  line-height: 1.1;
  font-weight: 800;
}

.hero-desc {
  margin: 0;
  max-width: 420px;
  font-size: 16px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.82);
}

.hero-points {
  display: grid;
  gap: 14px;
  margin-top: 42px;

  flex: 1;
  height: 84%;
  color: #fff;
  text-align: center;
  background-image: url('@/assets/login/login-bg.svg');
  background-repeat: no-repeat;
  background-position: bottom;
  background-size: 135% 135%;
}

.hero-point {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.point-title {
  display: block;
  margin-bottom: 4px;
  font-weight: 700;
}

.point-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.76);
}

.login-panel {
  padding: 36px 36px 30px;
  background: rgba(255, 255, 255, 0.85);
}

.panel-header {
  margin-bottom: 18px;
}

.panel-title {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.panel-subtitle {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.auth-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.auth-link {
  color: #0f766e;
  cursor: pointer;
}

.auth-sep {
  color: #cbd5e1;
}

.oauth-title {
  margin: 28px 0 14px;
  color: #334155;
  font-weight: 700;
}

.oauth-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.oauth-btn {
  border: 1px solid #dbe3ee;
  border-radius: 14px;
  background: #fff;
  color: #334155;
  height: 44px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.oauth-btn:hover {
  border-color: #0f766e;
  color: #0f766e;
  transform: translateY(-1px);
}

.code-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.code-row :deep(.layui-input-wrapper) {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
}

.login-captcha {
  width: 112px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #dbe3ee;
  background: #fff;
  cursor: pointer;
  flex-shrink: 0;
}

.login-captcha img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.qrcode-panel {
  padding: 28px 0 10px;
  text-align: center;
}

.qrcode-loading {
  padding: 52px 0;
  color: #64748b;
}

.qrcode-tip {
  margin: 14px 0 12px;
  font-size: 12px;
  color: #64748b;
}

.qrcode-refresh {
  border: 0;
  background: transparent;
  color: #0f766e;
  cursor: pointer;
}

:deep(.layui-tab-title .layui-this) {
  background-color: transparent;
}

:deep(.layui-form-item) {
  margin-bottom: 14px;
}

:deep(.layui-form-item .layui-input-wrapper) {
  margin-top: 8px;
}

:deep(.layui-btn) {
  height: 42px;
}

@media (max-width: 960px) {
  .login-shell {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .login-hero {
    padding: 36px 28px;
  }

  .hero-title {
    font-size: 36px;
  }

  .login-panel {
    padding: 28px 20px 24px;
  }

  .oauth-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.line-container .icon {
  height: 28px;
  width: 28px;
  margin-right: 0px;
  vertical-align: middle;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 2px 0 rgb(9 30 66 / 4%), 0 1px 4px 0 rgb(9 30 66 / 10%), 0 0 1px 0 rgb(9 30 66 / 10%);
}
</style>
