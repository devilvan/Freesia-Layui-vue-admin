<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;flex-direction:column">
    <h2>{{ error ? '登录异常' : statusText }}</h2>
    <p v-if="error" style="color:red;margin-top:12px">{{ error }}</p>
    <p v-else style="color:#999;margin-top:12px;font-size:14px">授权成功后自动登录，请稍候...</p>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const error = ref('')
const statusText = ref('正在处理登录...')

/**
 * 从浏览器 URL 中提取 token
 * 使用原生 API 直接解析，不依赖 vue-router 的路由对象
 */
function extractToken(): string | null {
  // 方式1：从 ?token=xxx 解析（token 在 query string 中）
  const searchParams = new URLSearchParams(window.location.search)
  let token = searchParams.get('token')
  if (token) {
    console.log('[OAuthCallback] 从 window.location.search 解析到 token:', token.substring(0, 20) + '...')
    return token
  }

  // 方式2：从 hash 后的 query 解析（兜底，如 /oauth/callback/gitee#?token=xxx）
  const hash = window.location.hash
  if (hash) {
    const qi = hash.indexOf('?')
    if (qi >= 0) {
      const hashParams = new URLSearchParams(hash.substring(qi))
      token = hashParams.get('token')
      if (token) {
        console.log('[OAuthCallback] 从 window.location.hash 解析到 token:', token.substring(0, 20) + '...')
        return token
      }
    }
  }

  console.error('[OAuthCallback] 未能从 URL 解析到 token')
  console.error('[OAuthCallback] location.search:', window.location.search)
  console.error('[OAuthCallback] location.hash:', window.location.hash)
  console.error('[OAuthCallback] location.href:', window.location.href)
  return null
}

onMounted(async () => {
  try {
    // Step 1: 从 URL 中提取 token（使用原生 API，绕过 vue-router）
    const token = extractToken()

    if (!token) {
      error.value = '缺少登录凭证，2 秒后返回登录页...'
      setTimeout(() => router.replace('/login'), 2000)
      return
    }

    // Step 2: 显式持久化 token 到 localStorage（防御层，不依赖 Pinia persist 插件）
    console.log('[OAuthCallback] 开始持久化 token...')
    localStorage.setItem('token', token)
    // 同时写入 cookie（防御层，兼容不同浏览器环境）
    try {
      document.cookie = `token=${encodeURIComponent(token)}; path=/; max-age=86400; SameSite=Lax`
    } catch (_) { /* cookie 写入失败不影响主流程 */ }

    // Step 3: 设置到 Pinia store（触发 persist 插件持久化）
    userStore.token = token
    console.log('[OAuthCallback] token 已设置到 store 和 localStorage')

    // Step 4: 获取用户信息
    statusText.value = '正在获取用户信息...'
    console.log('[OAuthCallback] 开始调用 getInfo()...')
    await userStore.getInfo()
    console.log('[OAuthCallback] getInfo() 完成, permissions:', userStore.permissions?.length || 0, 'roles:', userStore.roles?.length || 0)

    // Step 5: 获取路由（在导航前加载，避免路由守卫重复调用）  // 注释掉，由路由守卫处理
    // await userStore.getRouters()

    // Step 6: 跳转到首页
    statusText.value = '登录成功，正在跳转...'
    // 从 URL 中获取可能的 redirectUrl
    const redirectUrl = new URLSearchParams(window.location.search).get('redirectUrl') || '/'
    console.log('[OAuthCallback] 即将跳转到:', redirectUrl)
    router.replace(redirectUrl)
  } catch (e: any) {
    console.error('[OAuthCallback] 回调处理异常:', e)
    console.error('[OAuthCallback] 异常堆栈:', e.stack)
    error.value = '登录失败: ' + (e.message || '未知错误') + '，2 秒后返回登录页...'
    // 清理可能已持久化的 token
    try {
      localStorage.removeItem('token')
      document.cookie = 'token=; path=/; max-age=0'
    } catch (_) {}
    userStore.token = ''
    setTimeout(() => router.replace('/login'), 2000)
  }
})
</script>
