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

export default {
  name: 'OAuthCallback',
  setup() {
    const error = ref('')

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

        if (!token) {
          error.value = '缺少登录凭证'
          setTimeout(() => {
            uni.reLaunch({url: '/pages/login/index'})
          }, 2000)
          return
        }

        const userStore = useUserStore()
        userStore.setToken(token)
        await userStore.getInfo()
        uni.switchTab({url: '/pages/enrollee/accounts/mine/index'})
      } catch (e) {
        error.value = '登录失败'
        setTimeout(() => {
          uni.reLaunch({url: '/pages/login/index'})
        }, 2000)
      }
    })

    return {error}
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
