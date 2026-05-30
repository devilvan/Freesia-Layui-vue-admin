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
              <text class="subtitle">开箱即用的 uniapp 企业级前端模板</text>
            </view>
          </view>
          <view class="login-ID">
            <text class="sign-title">🎯 Sign in</text>
            <view class="tabs">
              <view :class="['tab-item', { active: method === '1' }]" @click="method = '1'">用户名</view>
              <view :class="['tab-item', { active: method === '2' }]" @click="method = '2'">二维码</view>
            </view>
            <view v-if="method === '1'" class="login-form">
              <view class="form-item">
                <text class="form-label">用户名</text>
                <view class="input-wrap">
                  <text class="input-icon">👤</text>
                  <input class="form-input" placeholder="用户名" v-model="loginForm.username"/>
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
            </view>
            <view v-else class="qrcode-wrap">
              <view class="qrcode-content">
                <canvas canvas-id="qrcodeCanvas" class="qrcode-canvas"></canvas>
                <view class="qrcode-refresh" @click="toRefreshQrcode">
                  <text class="refresh-icon">🔄</text>
                  <text>刷新二维码</text>
                </view>
              </view>
            </view>
            <view class="line-wrap">
              <view class="line"></view>
              <text class="line-text">Other login methods</text>
              <view class="line"></view>
            </view>
            <view class="other-ways">
              <view class="way-item" @click="loginWith('wechat')">
                <image class="way-icon" src="/static/login/w.svg" mode="aspectFit"/>
                <text class="way-text">微信</text>
              </view>
              <view class="way-item" @click="loginWith('dingding')">
                <image class="way-icon" src="/static/login/q.svg" mode="aspectFit"/>
                <text class="way-text">钉钉</text>
              </view>
              <view class="way-item" @click="loginWith('gitee')">
                <image class="way-icon" src="/static/login/a.svg" mode="aspectFit"/>
                <text class="way-text">Gitee</text>
              </view>
              <view class="way-item" @click="loginWith('github')">
                <image class="way-icon" src="/static/login/f.svg" mode="aspectFit"/>
                <text class="way-text">Github</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {login, findCaptchaEnabled} from '@/api/Login'
import {getCaptchaCode} from '@/api/captcha/Captcha'
import {loginQrcode} from '@/api/module/commone'
import {useCryptStore} from '@/store/crypt'
import {useUserStore} from '@/store/user'
import {setToken} from '@/utils/storage'

export default {
  data() {
    return {
      method: '1',
      captchaImg: '',
      loging: false,
      loginQrcodeText: '',
      loginForm: {
        username: '',
        password: '',
        code: '',
        captchaKey: ''
      },
      captchaEnabled: false
    }
  },
  onLoad() {
    this.init()
  },
  methods: {
    async init() {
      // 已有 token 则直接跳转主页
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
        const res = await login(encryptedData)
        this.loging = false
        if (res.code === 200) {
          // 立即持久化 token（同时写入 storage 和 cookie），防止页面切换导致 token 丢失
          setToken(res.data.token)
          // 加载用户信息以获取租户列表
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
        this.loging = false
        uni.showToast({title: '登录失败', icon: 'none'})
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
      const {data, code, msg} = await loginQrcode()
      if (code === 200) {
        this.loginQrcodeText = data.data
      } else {
        uni.showToast({title: msg, icon: 'none'})
      }
    },
    loginWith(type) {
      // 钉钉暂不支持
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
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.login-bg-title .subtitle {
  display: block;
  font-size: 26rpx;
  opacity: 0.9;
  letter-spacing: 4rpx;
}

.login-ID {
  padding: 30rpx;
}

.sign-title {
  font-size: 44rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  display: block;
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

.qrcode-wrap {
  padding: 40rpx 0;
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