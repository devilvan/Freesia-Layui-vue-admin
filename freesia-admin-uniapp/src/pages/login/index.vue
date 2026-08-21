<template>
  <view class="login-wrap">
    <view class="login-root">
      <view class="login-main">
        <image class="login-one-ball"
               src="https://assets.codehub.cn/micro-frontend/login/fca1d5960ccf0dfc8e32719d8a1d80d2.png"
               mode="aspectFit"></image>
        <image class="login-two-ball"
               src="https://assets.codehub.cn/micro-frontend/login/4bcf705dad662b33a4fc24aaa67f6234.png"
               mode="aspectFit"></image>
        <view class="login-container">
          <view class="login-side">
            <view class="login-bg-title">
              <text class="title">Freesia-Admin</text>
            </view>
          </view>
          <view class="login-ID">
            <view class="auth-header">
              <text class="sign-title">{{ pageTitle }}</text>
              <text class="sign-subtitle">{{ pageSubtitle }}</text>
            </view>

            <template v-if="pageMode === 'login'">
              <view class="tabs">
                <view :class="['tab-item', { active: method === '1' }]" @click="method = '1'">用户名</view>
                <view :class="['tab-item', { active: method === '2' }]" @click="method = '2'">二维码</view>
              </view>
              <view v-if="method === '1'" class="login-form">
                <view class="form-item">
                  <text class="form-label">用户名 / 邮箱</text>
                  <view class="input-wrap">
                    <text class="input-icon">👤</text>
                    <input class="form-input" placeholder="用户名 / 邮箱" v-model="loginForm.username"/>
                    <text v-if="loginForm.username" class="clear-icon" @click="loginForm.username = ''">✕</text>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">密码</text>
                  <view class="input-wrap">
                    <text class="input-icon">🔒</text>
                    <input class="form-input" type="password" placeholder="密码" v-model="loginForm.password"/>
                  </view>
                </view>
                <view class="form-item" v-if="captchaEnabled">
                  <text class="form-label">验证码</text>
                  <view class="captcha-wrap">
                    <view class="input-wrap captcha-input">
                      <text class="input-icon">🔢</text>
                      <input class="form-input" placeholder="验证码" v-model="loginForm.code"/>
                    </view>
                    <view class="login-captcha" @click="toRefreshImg">
                      <image v-if="captchaImg" :src="captchaImg" mode="aspectFit"/>
                      <text v-else>获取验证码</text>
                    </view>
                  </view>
                </view>
                <view class="form-item">
                  <button class="login-btn" :class="{ loading: loging }" @click="loginSubmit">
                    <text v-if="loging" class="loading-icon">⏳</text>
                    <text>{{ loging ? '登录中...' : '登录' }}</text>
                  </button>
                </view>
                <view class="auth-links">
                  <text class="auth-link" @click="switchMode('register')">没有账号？去注册</text>
                  <text class="auth-sep">|</text>
                  <text class="auth-link" @click="switchMode('reset')">忘记密码？</text>
                </view>
              </view>
              <view v-else class="qrcode-wrap">
                <view class="qrcode-content">
                  <canvas canvas-id="qrcodeCanvas" class="qrcode-canvas"></canvas>
                  <view class="qrcode-box">
                    <text class="qrcode-text">{{ loginQrcodeText || '二维码加载中...' }}</text>
                  </view>
                  <view class="qrcode-refresh" @click="toRefreshQrcode">
                    <text class="refresh-icon">🔄</text>
                    <text>刷新二维码</text>
                  </view>
                </view>
                <view class="auth-links">
                  <text class="auth-link" @click="switchMode('register')">没有账号？去注册</text>
                  <text class="auth-sep">|</text>
                  <text class="auth-link" @click="switchMode('reset')">忘记密码？</text>
                </view>
              </view>
              <view class="line-wrap">
                <view class="line"></view>
                <text class="line-text">其他登录方式</text>
                <view class="line"></view>
              </view>
              <view class="other-ways">
                <view class="way-item" @click="loginWith('wechat')">
                  <image class="way-icon" src="/static/login/WX.svg" mode="aspectFit"/>
                  <text class="way-text">微信</text>
                </view>
                <view class="way-item" @click="loginWith('qq')">
                  <image class="way-icon" src="/static/login/QQ.svg" mode="aspectFit"/>
                  <text class="way-text">QQ</text>
                </view>
                <view class="way-item" @click="loginWith('gitee')">
                  <image class="way-icon" src="/static/login/Gitee.svg" mode="aspectFit"/>
                  <text class="way-text">Gitee</text>
                </view>
                <view class="way-item" @click="loginWith('github')">
                  <image class="way-icon" src="/static/login/Github.svg" mode="aspectFit"/>
                  <text class="way-text">Github</text>
                </view>
              </view>
            </template>

            <template v-else-if="pageMode === 'register'">
              <view class="login-form">
                <view class="form-item">
                  <text class="form-label">邮箱</text>
                  <view class="input-wrap">
                    <text class="input-icon">✉️</text>
                    <input class="form-input" placeholder="请输入邮箱" v-model="registerForm.email"/>
                    <text v-if="registerForm.email" class="clear-icon" @click="registerForm.email = ''">✕</text>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">密码</text>
                  <view class="input-wrap">
                    <text class="input-icon">🔒</text>
                    <input class="form-input" type="password" placeholder="6-20位，包含字母、数字、特殊字符" v-model="registerForm.password"/>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">确认密码</text>
                  <view class="input-wrap">
                    <text class="input-icon">🔒</text>
                    <input class="form-input" type="password" placeholder="再次输入密码" v-model="registerConfirmPassword"/>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">邮箱验证码</text>
                  <view class="captcha-wrap">
                    <view class="input-wrap captcha-input">
                      <text class="input-icon">🔢</text>
                      <input class="form-input" placeholder="邮箱验证码" v-model="registerForm.code"/>
                    </view>
                    <view class="login-captcha code-btn" @click="sendRegisterCode">
                      <text v-if="registerCountdown > 0">{{ registerCountdown }}s后重发</text>
                      <text v-else>发送验证码</text>
                    </view>
                  </view>
                </view>
                <view class="form-item">
                  <button class="login-btn" :class="{ loading: registering }" @click="registerSubmit">
                    <text v-if="registering" class="loading-icon">⏳</text>
                    <text>{{ registering ? '提交中...' : '注册' }}</text>
                  </button>
                </view>
                <view class="auth-links">
                  <text class="auth-link" @click="switchMode('login')">返回登录</text>
                  <text class="auth-sep">|</text>
                  <text class="auth-link" @click="switchMode('reset')">忘记密码？</text>
                </view>
              </view>
            </template>

            <template v-else>
              <view class="login-form">
                <view class="form-item">
                  <text class="form-label">邮箱</text>
                  <view class="input-wrap">
                    <text class="input-icon">✉️</text>
                    <input class="form-input" placeholder="请输入邮箱" v-model="resetForm.email"/>
                    <text v-if="resetForm.email" class="clear-icon" @click="resetForm.email = ''">✕</text>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">新密码</text>
                  <view class="input-wrap">
                    <text class="input-icon">🔒</text>
                    <input class="form-input" type="password" placeholder="6-20位，包含字母、数字、特殊字符" v-model="resetForm.password"/>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">确认新密码</text>
                  <view class="input-wrap">
                    <text class="input-icon">🔒</text>
                    <input class="form-input" type="password" placeholder="再次输入新密码" v-model="resetConfirmPassword"/>
                  </view>
                </view>
                <view class="form-item">
                  <text class="form-label">邮箱验证码</text>
                  <view class="captcha-wrap">
                    <view class="input-wrap captcha-input">
                      <text class="input-icon">🔢</text>
                      <input class="form-input" placeholder="邮箱验证码" v-model="resetForm.code"/>
                    </view>
                    <view class="login-captcha code-btn" @click="sendResetCode">
                      <text v-if="resetCountdown > 0">{{ resetCountdown }}s后重发</text>
                      <text v-else>发送验证码</text>
                    </view>
                  </view>
                </view>
                <view class="form-item">
                  <button class="login-btn" :class="{ loading: resetting }" @click="resetSubmit">
                    <text v-if="resetting" class="loading-icon">⏳</text>
                    <text>{{ resetting ? '提交中...' : '重置密码' }}</text>
                  </button>
                </view>
                <view class="auth-links">
                  <text class="auth-link" @click="switchMode('login')">返回登录</text>
                  <text class="auth-sep">|</text>
                  <text class="auth-link" @click="switchMode('register')">去注册</text>
                </view>
              </view>
            </template>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {emailLogin, findCaptchaEnabled, login, register, resetPassword, sendEmailCode} from '@/api/Login'
import {getCaptchaCode} from '@/api/captcha/Captcha'
import {loginQrcode} from '@/api/module/commone'
import {useCryptStore} from '@/store/crypt'
import {useUserStore} from '@/store/user'
import {setToken} from '@/utils/storage'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,20}$/

export default {
  data() {
    return {
      pageMode: 'login',
      method: '1',
      captchaImg: '',
      loging: false,
      registering: false,
      resetting: false,
      loginQrcodeText: '',
      registerCountdown: 0,
      resetCountdown: 0,
      registerConfirmPassword: '',
      resetConfirmPassword: '',
      loginForm: {
        username: '',
        password: '',
        code: '',
        captchaKey: ''
      },
      registerForm: {
        email: '',
        password: '',
        code: ''
      },
      resetForm: {
        email: '',
        password: '',
        code: ''
      },
      captchaEnabled: false,
      registerTimer: null,
      resetTimer: null
    }
  },
  computed: {
    pageTitle() {
      if (this.pageMode === 'register') return '创建账号'
      if (this.pageMode === 'reset') return '找回密码'
      return '🎯 Sign in'
    },
    pageSubtitle() {
      if (this.pageMode === 'register') return '使用邮箱完成注册'
      if (this.pageMode === 'reset') return '通过邮箱验证码重置密码'
      return '邮箱登录、账号登录、二维码登录'
    }
  },
  onLoad() {
    this.init()
  },
  onUnload() {
    this.stopRegisterCountdown()
    this.stopResetCountdown()
  },
  methods: {
    async init() {
      const token = uni.getStorageSync('token')
      if (token) {
        try {
          const userStore = useUserStore()
          if (!userStore.state.sysTenantDtoList || userStore.state.sysTenantDtoList.length === 0) {
            await userStore.getInfo()
          }
          uni.switchTab({url: '/pages/enrollee/accounts/mine/index'})
          return
        } catch (e) {
          console.error('自动登录失败', e)
        }
      }
      await uni.$getPublicKey()
      const {data, code} = await findCaptchaEnabled()
      if (code === 200 && data === true) {
        this.captchaEnabled = true
        this.toRefreshImg()
      }
    },
    switchMode(mode) {
      this.pageMode = mode
      if (mode === 'login' && this.method === '2' && !this.loginQrcodeText) {
        this.toRefreshQrcode()
      }
    },
    async loginSubmit() {
      if (!this.loginForm.username) {
        uni.showToast({title: '请输入用户名', icon: 'none'})
        return
      }
      if (!this.loginForm.password) {
        uni.showToast({title: '请输入密码', icon: 'none'})
        return
      }
      if (this.captchaEnabled && !this.loginForm.code) {
        uni.showToast({title: '请输入验证码', icon: 'none'})
        return
      }
      this.loging = true
      try {
        const cryptStore = useCryptStore()
        const loginData = {
          username: this.loginForm.username,
          password: this.loginForm.password,
          code: this.loginForm.code,
          captchaKey: this.loginForm.captchaKey
        }
        const encryptedData = await cryptStore.encryptAes(loginData)
        const api = this.loginForm.username.indexOf('@') > -1 ? emailLogin : login
        const res = await api(encryptedData)
        if (res.code === 200) {
          setToken(res.data.token)
          const userStore = useUserStore()
          userStore.setToken(res.data.token)
          await userStore.getInfo()
          uni.showToast({title: '登录成功', icon: 'success'})
          setTimeout(() => {
            uni.switchTab({url: '/pages/enrollee/accounts/mine/index'})
          }, 1000)
        } else {
          uni.showToast({title: res.msg || '登录失败', icon: 'none'})
          if (this.captchaEnabled) {
            this.toRefreshImg()
          }
        }
      } catch (e) {
        uni.showToast({title: '登录失败', icon: 'none'})
      } finally {
        this.loging = false
      }
    },
    async registerSubmit() {
      if (!this.validateEmail(this.registerForm.email)) {
        uni.showToast({title: '请输入正确的邮箱', icon: 'none'})
        return
      }
      if (!this.validatePassword(this.registerForm.password)) {
        uni.showToast({title: '密码需为6-20位且包含字母、数字、特殊字符', icon: 'none'})
        return
      }
      if (this.registerForm.password !== this.registerConfirmPassword) {
        uni.showToast({title: '两次输入的密码不一致', icon: 'none'})
        return
      }
      if (!this.registerForm.code) {
        uni.showToast({title: '请输入邮箱验证码', icon: 'none'})
        return
      }
      this.registering = true
      try {
        const cryptStore = useCryptStore()
        const encryptedData = await cryptStore.encryptAes({
          email: this.registerForm.email,
          password: this.registerForm.password,
          code: this.registerForm.code
        })
        const res = await register(encryptedData)
        if (res.code === 200) {
          uni.showToast({title: res.msg || '注册成功', icon: 'success'})
          const email = this.registerForm.email
          this.loginForm.username = this.registerForm.email
          this.resetRegisterForm()
          this.method = '1'
          this.loginForm.username = email
          this.switchMode('login')
        } else {
          uni.showToast({title: res.msg || '注册失败', icon: 'none'})
        }
      } catch (e) {
        uni.showToast({title: '注册失败', icon: 'none'})
      } finally {
        this.registering = false
      }
    },
    async resetSubmit() {
      if (!this.validateEmail(this.resetForm.email)) {
        uni.showToast({title: '请输入正确的邮箱', icon: 'none'})
        return
      }
      if (!this.validatePassword(this.resetForm.password)) {
        uni.showToast({title: '密码需为6-20位且包含字母、数字、特殊字符', icon: 'none'})
        return
      }
      if (this.resetForm.password !== this.resetConfirmPassword) {
        uni.showToast({title: '两次输入的新密码不一致', icon: 'none'})
        return
      }
      if (!this.resetForm.code) {
        uni.showToast({title: '请输入邮箱验证码', icon: 'none'})
        return
      }
      this.resetting = true
      try {
        const cryptStore = useCryptStore()
        const encryptedData = await cryptStore.encryptAes({
          email: this.resetForm.email,
          password: this.resetForm.password,
          code: this.resetForm.code
        })
        const res = await resetPassword(encryptedData)
        if (res.code === 200) {
          uni.showToast({title: res.msg || '重置成功', icon: 'success'})
          const email = this.resetForm.email
          this.loginForm.username = this.resetForm.email
          this.resetResetForm()
          this.method = '1'
          this.loginForm.username = email
          this.switchMode('login')
        } else {
          uni.showToast({title: res.msg || '重置失败', icon: 'none'})
        }
      } catch (e) {
        uni.showToast({title: '重置失败', icon: 'none'})
      } finally {
        this.resetting = false
      }
    },
    async toRefreshImg() {
      try {
        const res = await getCaptchaCode()
        if (res.code === 200) {
          this.captchaImg = 'data:image/gif;base64,' + res.data?.captchaImg
          this.loginForm.captchaKey = res.data?.captchaKey
        } else {
          uni.showToast({title: res.msg, icon: 'none'})
        }
      } catch (e) {
        uni.showToast({title: '获取验证码失败', icon: 'none'})
      }
    },
    async toRefreshQrcode() {
      try {
        const {data, code, msg} = await loginQrcode()
        if (code === 200) {
          this.loginQrcodeText = data.data || data || ''
        } else {
          uni.showToast({title: msg, icon: 'none'})
        }
      } catch (e) {
        uni.showToast({title: '获取二维码失败', icon: 'none'})
      }
    },
    async sendRegisterCode() {
      if (!this.validateEmail(this.registerForm.email)) {
        uni.showToast({title: '请输入正确的邮箱', icon: 'none'})
        return
      }
      if (this.registerCountdown > 0) {
        return
      }
      const cryptStore = useCryptStore()
      const encrypted = await cryptStore.encryptAes({email: this.registerForm.email, scene: 'register'})
      const res = await sendEmailCode(encrypted)
      if (res.code === 200) {
        uni.showToast({title: res.msg || '验证码已发送', icon: 'success'})
        this.startRegisterCountdown()
      } else {
        uni.showToast({title: res.msg || '验证码发送失败', icon: 'none'})
      }
    },
    async sendResetCode() {
      if (!this.validateEmail(this.resetForm.email)) {
        uni.showToast({title: '请输入正确的邮箱', icon: 'none'})
        return
      }
      if (this.resetCountdown > 0) {
        return
      }
      const cryptStore = useCryptStore()
      const encrypted = await cryptStore.encryptAes({email: this.resetForm.email, scene: 'reset_password'})
      const res = await sendEmailCode(encrypted)
      if (res.code === 200) {
        uni.showToast({title: res.msg || '验证码已发送', icon: 'success'})
        this.startResetCountdown()
      } else {
        uni.showToast({title: res.msg || '验证码发送失败', icon: 'none'})
      }
    },
    loginWith(type) {
      if (type === 'dingding') {
        uni.showToast({title: '钉钉登录暂未支持', icon: 'none'})
        return
      }
      // #ifdef MP-WEIXIN
      if (type === 'wechat') {
        uni.login({
          provider: 'weixin',
          success: async (loginRes) => {
            try {
              const res = await this.wxMiniProgramLogin(loginRes.code)
              if (res && res.code === 200) {
                uni.switchTab({url: '/pages/enrollee/accounts/mine/index'})
              }
            } catch (e) {
              uni.showToast({title: '微信登录失败', icon: 'none'})
            }
          },
          fail: () => {
            uni.showToast({title: '微信授权失败', icon: 'none'})
          }
        })
        return
      }
      // #endif
      // #ifdef H5
      const providerMap = {wechat: 'wechat_open', gitee: 'gitee', github: 'github'}
      const provider = providerMap[type]
      if (!provider) return
      const baseURL = 'http://localhost:8570'
      const frontendCallbackUrl = window.location.origin + '/#/pages/login/oauthCallback'
      const authorizeUrl = baseURL + '/api/sysLoginController/oauth/authorize/' + provider
          + '?redirectUrl=' + encodeURIComponent(frontendCallbackUrl)
      window.location.href = authorizeUrl
      // #endif
    },
    validateEmail(email) {
      return EMAIL_PATTERN.test(email || '')
    },
    validatePassword(password) {
      return PASSWORD_PATTERN.test(password || '')
    },
    startRegisterCountdown() {
      this.stopRegisterCountdown()
      this.registerCountdown = 60
      this.registerTimer = setInterval(() => {
        this.registerCountdown--
        if (this.registerCountdown <= 0) {
          this.stopRegisterCountdown()
        }
      }, 1000)
    },
    startResetCountdown() {
      this.stopResetCountdown()
      this.resetCountdown = 60
      this.resetTimer = setInterval(() => {
        this.resetCountdown--
        if (this.resetCountdown <= 0) {
          this.stopResetCountdown()
        }
      }, 1000)
    },
    stopRegisterCountdown() {
      if (this.registerTimer) {
        clearInterval(this.registerTimer)
        this.registerTimer = null
      }
      this.registerCountdown = 0
    },
    stopResetCountdown() {
      if (this.resetTimer) {
        clearInterval(this.resetTimer)
        this.resetTimer = null
      }
      this.resetCountdown = 0
    },
    resetRegisterForm() {
      this.registerForm.email = ''
      this.registerForm.password = ''
      this.registerForm.code = ''
      this.registerConfirmPassword = ''
      this.stopRegisterCountdown()
    },
    resetResetForm() {
      this.resetForm.email = ''
      this.resetForm.password = ''
      this.resetForm.code = ''
      this.resetConfirmPassword = ''
      this.stopResetCountdown()
    }
  }
}
</script>

<style lang="scss">
.login-wrap {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  background-image: url(https://assets.codehub.cn/micro-frontend/login/f7eeecbeccefe963298c23b54741d473.png);
  background-repeat: no-repeat;
  background-size: cover;
  min-height: 100vh;
}

.login-one-ball {
  opacity: 0.4;
  position: absolute;
  width: 568rpx;
  left: -200rpx;
  bottom: 0;
}

.login-two-ball {
  opacity: 0.4;
  position: absolute;
  width: 320rpx;
  right: -100rpx;
  top: -30rpx;
}

.login-root {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  padding: 20rpx;
  box-sizing: border-box;
}

.login-main {
  position: relative;
  width: 100%;
}

.login-container {
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 940rpx;
  margin: 0 auto;
  border-radius: 8rpx;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(30px);
  box-shadow: 0 0 20rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.login-side {
  padding: 40rpx 30rpx;
  background: linear-gradient(135deg, #009688 0%, #007a71 100%);
  text-align: center;
  background-image: url('/static/login/login-bg.svg');
  background-repeat: no-repeat;
  background-position: bottom;
  background-size: contain;
  min-height: 300rpx;
}

.login-bg-title {
  color: #fff;
}

.login-bg-title .title {
  color: #fff;
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.login-ID {
  padding: 30rpx;
}

.auth-header {
  margin-bottom: 20rpx;
}

.sign-title {
  font-size: 44rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.sign-subtitle {
  font-size: 24rpx;
  color: #999;
}

.tabs {
  display: flex;
  margin-bottom: 30rpx;
  border-bottom: 1rpx solid #e8e8e8;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
  transition: color 0.3s;
}

.tab-item.active {
  color: #009688;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: #009688;
  border-radius: 2rpx;
}

.login-form {
  margin-bottom: 20rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
}

.input-wrap {
  display: flex;
  align-items: center;
  background: #fff;
  border: 1rpx solid #e8e8e8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  height: 80rpx;
  transition: border-color 0.3s;
}

.input-wrap:focus-within {
  border-color: #009688;
}

.input-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
}

.form-input {
  flex: 1;
  height: 100%;
  font-size: 28rpx;
}

.clear-icon {
  font-size: 24rpx;
  color: #999;
  padding: 8rpx;
}

.captcha-wrap {
  display: flex;
  gap: 16rpx;
}

.captcha-input {
  flex: 1;
}

.login-captcha {
  width: 180rpx;
  height: 80rpx;
  border: 1rpx solid #e8e8e8;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  overflow: hidden;
}

.login-captcha image {
  width: 100%;
  height: 100%;
}

.login-captcha text {
  font-size: 24rpx;
  color: #999;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #009688 0%, #007a71 100%);
  color: #fff;
  border: none;
  border-radius: 8rpx;
  font-size: 32rpx;
  font-weight: bold;
  letter-spacing: 4rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.login-btn.loading {
  opacity: 0.8;
}

.loading-icon {
  font-size: 32rpx;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.auth-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16rpx;
  margin-top: 20rpx;
}

.auth-link {
  font-size: 26rpx;
  color: #009688;
}

.auth-sep {
  font-size: 24rpx;
  color: #ccc;
}

.qrcode-wrap {
  padding: 20rpx 0 0;
}

.qrcode-content {
  text-align: center;
}

.qrcode-canvas {
  width: 300rpx;
  height: 300rpx;
  margin: 0 auto 20rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 10rpx;
}

.qrcode-box {
  min-height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.82);
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  word-break: break-all;
}

.qrcode-text {
  font-size: 24rpx;
  color: #333;
}

.qrcode-refresh {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #009688;
}

.refresh-icon {
  font-size: 28rpx;
}

.line-wrap {
  display: flex;
  align-items: center;
  margin: 30rpx 0;
}

.line {
  flex: 1;
  height: 1rpx;
  background: #e8e8e8;
}

.line-text {
  padding: 0 20rpx;
  font-size: 24rpx;
  color: #999;
  letter-spacing: 2rpx;
}

.other-ways {
  display: flex;
  justify-content: space-around;
}

.way-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
}

.way-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  margin-bottom: 16rpx;
}

.way-text {
  font-size: 24rpx;
  color: #8592a6;
}

@media screen and (min-width: 750px) {
  .login-container {
    flex-direction: row;
    height: 600rpx;
  }

  .login-side {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: auto;
  }

  .login-ID {
    width: 450rpx;
  }
}
</style>
