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
import {useTabStore} from '@/layouts/composable/useTabStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const tabStore = useTabStore()
const error = ref('')

onMounted(async () => {
  try {
    const token = route.query.token as string
    if (!token) {
      error.value = '缺少登录凭证'
      setTimeout(() => router.replace('/login'), 2000)
      return
    }
    userStore.token = token
    await userStore.getInfo()
    await userStore.getRouters()
    // 跳转到 redirectUrl 或默认首页
    const redirectUrl = (route.query.redirectUrl as string) || '/'
    router.replace(redirectUrl)
  } catch (e: any) {
    error.value = '登录失败: ' + (e.message || '未知错误')
    setTimeout(() => router.replace('/login'), 2000)
  }
})
</script>
