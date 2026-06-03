<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;flex-direction:column">
    <h2>登录中，请稍候...</h2>
    <p v-if="error" style="color:red">{{ error }}</p>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const error = ref('')

onMounted(async () => {
  try {
    const token = route.query.token as string
    console.log('[OAuthCallback] 解析到的 token:', token ? token.substring(0, 20) + '...' : '空')
    console.log('[OAuthCallback] 完整 query:', route.query)

    if (!token) {
      error.value = '缺少登录凭证'
      setTimeout(() => router.replace('/login'), 2000)
      return
    }

    // 持久化 token
    userStore.token = token
    console.log('[OAuthCallback] token 已设置到 store，开始获取用户信息...')

    // 加载用户信息（路由守卫会自动调用 getRouters 加载路由）
    await userStore.getInfo()
    console.log('[OAuthCallback] 用户信息加载完成，跳转首页')

    // 跳转到 redirectUrl 或默认首页，与登录页面行为一致
    const redirectUrl = (route.query.redirectUrl as string) || '/'
    router.replace(redirectUrl)
  } catch (e: any) {
    console.error('[OAuthCallback] 回调处理异常:', e)
    error.value = '登录失败: ' + (e.message || '未知错误')
    setTimeout(() => router.replace('/login'), 2000)
  }
})
</script>
