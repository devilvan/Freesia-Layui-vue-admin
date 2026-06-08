<template>
  <view class="callback-wrap">
    <view class="callback-main">
      <text class="callback-title">登录中，请稍候...</text>
      <text v-if="error" class="callback-error">{{ error }}</text>
    </view>
  </view>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useUserStore} from '@/store/user'
// #ifdef H5
import {setToken} from '@/utils/storage'
// #endif

export default {
  name: 'OAuthCallback',
  setup() {
    const error = ref('')
    const loging = ref(false)

    onMounted(async () => {
      try {
        // H5 模式：优先从 search 读（token 在 hash 之前），其次从 hash 中解析
        // #ifdef H5
        let token = new URLSearchParams(window.location.search).get('token')
        if (!token) {
          // hash 路由下 token 可能在 # 之后：/#/pages/callback?token=xxx
          const hash = window.location.hash
          const qi = hash.indexOf('?')
          if (qi >= 0) {
            token = new URLSearchParams(hash.substring(qi)).get('token')
          }
        }
        // #endif
        // #ifndef H5
        const token = ''
        // #endif

        console.log('[OAuthCallback] 解析到的 token:', token ? token.substring(0, 20) + '...' : '空')

        if (!token) {
          error.value = '缺少登录凭证'
          setTimeout(() => {
            uni.reLaunch({url: '/pages/login/index'})
          }, 2000)
          return
        }

        // 持久化 token（同时写入 storage 和 cookie），确保后续 API 请求能读取到
        // #ifdef H5
        setToken(token)
        // #endif
        const userStore = useUserStore()
        userStore.setToken(token)
        console.log('[OAuthCallback] token 已持久化，开始获取用户信息...')

        // 加载用户信息以获取租户列表
        const userData = await userStore.getInfo()
        if (!userData) {
          console.error('[OAuthCallback] 获取用户信息失败，返回值为空')
          error.value = '获取用户信息失败，请重试'
          setTimeout(() => {
            uni.reLaunch({url: '/pages/login/index'})
          }, 2000)
          return
        }

        console.log('[OAuthCallback] 用户信息获取成功，跳转主页')
        uni.showToast({title: '登录成功', icon: 'success'})
        // 延迟跳转确保 token 和用户信息完全持久化，与登录页面行为一致
        setTimeout(() => {
          // 使用 reLaunch 彻底重置页面栈，避免 OAuth 回调页残留
          uni.reLaunch({url: '/pages/enrollee/accounts/mine/index'})
        }, 800)
      } catch (e) {
        console.error('[OAuthCallback] 回调处理异常:', e)
        error.value = '登录失败'
        setTimeout(() => {
          uni.reLaunch({url: '/pages/login/index'})
        }, 2000)
      }
    })

    return {error, loging}
  }
}
</script>

<style lang="scss" scoped>
.callback-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.callback-main {
  text-align: center;
}

.callback-title {
  font-size: 32rpx;
  color: #333;
}

.callback-error {
  font-size: 26rpx;
  color: #ff5722;
  margin-top: 20rpx;
}
</style>
